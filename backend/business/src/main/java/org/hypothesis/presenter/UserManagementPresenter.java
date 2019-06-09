/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import static java.util.stream.Collectors.toSet;
import static org.hypothesis.data.api.Roles.ROLE_SUPERUSER;
import static org.hypothesis.utility.UserUtility.userHasAnyRole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.api.Roles;
import org.hypothesis.data.dto.GroupDto;
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.RoleDto;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.data.dto.UserDto;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.service.GroupService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.service.impl.GroupServiceImpl;
import org.hypothesis.data.service.impl.PermissionServiceImpl;
import org.hypothesis.data.service.impl.UserServiceImpl;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.server.Messages;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class UserManagementPresenter extends AbstractManagementPresenter implements ColumnGenerator {

	private final PermissionService permissionService;
	private final UserService userService;
	private final GroupService groupService;

	private UserWindowPresenter userWindowPresenter;

	public UserManagementPresenter() {
		permissionService = new PermissionServiceImpl();
		userService = new UserServiceImpl();
		groupService = new GroupServiceImpl();
	}

	@Override
	public void init() {
		super.init();

		userWindowPresenter = new UserWindowPresenter(getBus());
	}

	@Override
	public Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setSpacing(true);

		Label title = new Label(Messages.getString("Caption.Label.UsersManagement"));
		title.addStyleName(ValoTheme.LABEL_HUGE);
		header.addComponent(title);
		header.addComponent(buildTools());
		header.setExpandRatio(title, 1);

		return header;
	}

	@Override
	protected Button buildAddButton() {
		final Button addButton = new Button(Messages.getString("Caption.Button.Add"));
		addButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		addButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				SimpleUserDto loggedUser = getLoggedUser();
				if (!userHasAnyRole(loggedUser, Roles.ROLE_SUPERUSER)
						&& groupService.findOwnerGroups(loggedUser.getId()).isEmpty()) {
					Notification.show(Messages.getString("Message.Error.CreateGroup"), Type.WARNING_MESSAGE);
				} else {
					userWindowPresenter.showWindow();
				}
			}
		});

		return addButton;
	}

	@Override
	protected ComboBox buildSelection() {
		final ComboBox selectionType = new ComboBox();
		selectionType.setTextInputAllowed(false);
		selectionType.setNullSelectionAllowed(false);

		selectionType.addItem(Messages.getString("Caption.Item.Selected"));
		selectionType.addItem(Messages.getString("Caption.Item.All"));
		selectionType.select(Messages.getString("Caption.Item.Selected"));

		selectionType.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				allSelected = selectionType.getValue().equals(Messages.getString("Caption.Item.All"));
				getBus().post(new MainUIEvent.UserSelectionChangedEvent());
			}
		});

		return selectionType;
	}

	@Override
	protected Button buildUpdateButton() {
		Button updateButton = new Button(Messages.getString("Caption.Button.Update"));
		updateButton.setClickShortcut(KeyCode.ENTER);
		updateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				editUsers(getSelectedUsers());
			}
		});
		return updateButton;
	}

	@Override
	protected Button buildDeleteButton() {
		Button deleteButton = new Button(Messages.getString("Caption.Button.Delete"));
		deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
		deleteButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				String question = allSelected ? Messages.getString("Caption.Confirm.User.DeleteAll")
						: Messages.getString("Caption.Confirm.User.DeleteSelected");

				deletionConfirmDialog = ConfirmDialog.show(UI.getCurrent(),
						Messages.getString("Caption.Dialog.ConfirmDeletion"), question,
						Messages.getString("Caption.Button.Confirm"), Messages.getString("Caption.Button.Cancel"),
						UserManagementPresenter.this);
			}
		});
		return deleteButton;
	}

	@Override
	protected Resource getExportResource() {
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			@Override
			public InputStream getStream() {
				return getExportFile();
			}
		};

		String filename = Messages.getString("Caption.Export.UserFileName");

		return new StreamResource(source, filename);
	}

	private InputStream getExportFile() {
		try {
			OutputStream output = new ByteArrayOutputStream();
			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);

			Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.UserSheetName"));

			int rowNr = 0;
			Row row = sheet.createRow(rowNr++);
			sheet.createFreezePane(0, 1);

			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Id"));
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Name"));
			row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Password"));

			for (Iterator<UserDto> i = getSelectedUsers().iterator(); i.hasNext();) {
				row = sheet.createRow(rowNr++);
				UserDto user = i.next();
				row.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(user.getId());
				row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(user.getUsername());
				row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(user.getPassword());
			}
			workbook.write(output);
			workbook.close();
			output.close();

			return new ByteArrayInputStream(((ByteArrayOutputStream) output).toByteArray());

		} catch (IOException e) {
			Notification.show(Messages.getString("Message.Error.ExportCreateFile"), e.getMessage(), Type.ERROR_MESSAGE);
		}

		return null;
	}

	private void deleteUsers() {
		Collection<UserDto> users = getSelectedUsers();

		SimpleUserDto loggedUser = getLoggedUser();
		checkDeletionPermission(loggedUser, users);
		checkSuperuserLeft(loggedUser);

		for (Iterator<UserDto> iterator = users.iterator(); iterator.hasNext();) {
			UserDto user = iterator.next();
			// user = userService.merge(user);

			userService.delete(user);

			for (GroupDto group : user.getGroups()) {
				if (group != null) {
					getBus().post(new MainUIEvent.GroupUsersChangedEvent(group));
				}
			}

			table.removeItem(user.getId());
		}

		if (users.contains(loggedUser)) {
			getBus().post(new MainUIEvent.UserLoggedOutEvent());
		}
	}

	private void checkDeletionPermission(SimpleUserDto currentUser, Collection<UserDto> users) {
		String exceptionMessage = Messages.getString("Message.Error.SuperuserDelete");

		if (userHasAnyRole(currentUser, ROLE_SUPERUSER)) {
			return;
		}

		if (allSelected) {
			throw new UnsupportedOperationException(exceptionMessage);
		}

		for (Iterator<UserDto> iterator = users.iterator(); iterator.hasNext();) {
			UserDto user = iterator.next();
			if (userHasAnyRole(user, ROLE_SUPERUSER)) {
				throw new UnsupportedOperationException(exceptionMessage);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void checkSuperuserLeft(SimpleUserDto currentUser) {
		String exceptionMessage = Messages.getString("Message.Error.SuperuserLeft");

		if (allSelected) {
			throw new UnsupportedOperationException(exceptionMessage);
		}

		if (userHasAnyRole(currentUser, ROLE_SUPERUSER)) {
			boolean superuserLeft = false;
			for (Iterator<Long> iterator = ((Collection<Long>) table.getItemIds()).iterator(); iterator.hasNext();) {
				Long id = iterator.next();
				if (!table.isSelected(id)) {
					UserDto user = ((BeanItem<UserDto>) table.getItem(id)).getBean();
					if (userHasAnyRole(user, ROLE_SUPERUSER)) {
						superuserLeft = true;
						break;
					}
				}
			}

			if (!superuserLeft) {
				throw new UnsupportedOperationException(exceptionMessage);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Long> getSelectedUserIds() {
		Collection<Long> userIds;
		if (allSelected) {
			userIds = (Collection<Long>) table.getItemIds();
		} else {
			userIds = (Collection<Long>) table.getValue();
		}
		return userIds;
	}

	@SuppressWarnings("unchecked")
	private Collection<UserDto> getSelectedUsers() {
		Collection<UserDto> users = new HashSet<>();
		for (Long id : getSelectedUserIds()) {
			users.add(((BeanItem<UserDto>) table.getItem(id)).getBean());
		}
		return users;
	}

	@Override
	public Table buildTable() {
		Table table = new Table();
		table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setColumnCollapsingAllowed(true);
		table.setSortContainerPropertyId(FieldConstants.USERNAME);

		BeanContainer<Long, UserDto> dataSource = new BeanContainer<Long, UserDto>(UserDto.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);

		List<UserDto> users;
		SimpleUserDto loggedUser = getLoggedUser();
		if (userHasAnyRole(loggedUser, ROLE_SUPERUSER)) {
			users = userService.findAll();
		} else {
			users = userService.findOwnerUsers(loggedUser.getId());
		}
		for (UserDto user : users) {
			dataSource.addBean(user);
		}
		table.setContainerDataSource(dataSource);
		dataSource.setItemSorter(new CaseInsensitiveItemSorter());
		table.sort();

		table.addGeneratedColumn(FieldConstants.ROLES, this);
		table.addGeneratedColumn(FieldConstants.GROUPS, this);
		table.addGeneratedColumn(FieldConstants.ENABLED, this);
		table.addGeneratedColumn(FieldConstants.EXPIRE_DATE, this);
		table.addGeneratedColumn(FieldConstants.AVAILABLE_PACKS, this);

		table.setColumnCollapsed(FieldConstants.PASSWORD, true);
		table.setColumnCollapsed(FieldConstants.ENABLED, true);
		table.setColumnCollapsed(FieldConstants.EXPIRE_DATE, true);

		table.setVisibleColumns(FieldConstants.ID, FieldConstants.USERNAME, FieldConstants.PASSWORD,
				FieldConstants.ROLES, FieldConstants.GROUPS, FieldConstants.AVAILABLE_PACKS, FieldConstants.ENABLED,
				FieldConstants.EXPIRE_DATE, FieldConstants.NOTE);

		table.setColumnHeaders(Messages.getString("Caption.Field.Id"), Messages.getString("Caption.Field.Username"),
				Messages.getString("Caption.Field.Password"), Messages.getString("Caption.Field.Role"),
				Messages.getString("Caption.Field.Groups"), Messages.getString("Caption.Field.AvailablePacks"),
				Messages.getString("Caption.Field.Enabled"), Messages.getString("Caption.Field.ExpireDate"),
				Messages.getString("Caption.Field.Note"));

		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				getBus().post(new MainUIEvent.UserSelectionChangedEvent());
			}
		});

		table.addItemClickListener(new ItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					UserDto user = ((BeanItem<UserDto>) event.getItem()).getBean();
					editUsers(Stream.of(user).collect(toSet()));
				}
			}
		});

		this.table = table;
		return table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		if (columnId.equals(FieldConstants.ROLES)) {
			UserDto user = ((BeanItem<UserDto>) source.getItem(itemId)).getBean();

			Set<RoleDto> roles = user.getRoles();
			List<String> sortedRoles = new ArrayList<>();
			for (RoleDto role : roles) {
				sortedRoles.add(role.getName());
			}
			Collections.sort(sortedRoles);
			Label rolesLabel = new Label();

			StringBuilder descriptionBuilder = new StringBuilder();
			StringBuilder labelBuilder = new StringBuilder();
			for (String role : sortedRoles) {
				if (descriptionBuilder.length() != 0) {
					descriptionBuilder.append("<br/>");
					labelBuilder.append(", ");
				}
				descriptionBuilder.append(role);
				labelBuilder.append(role);
			}
			rolesLabel.setDescription(descriptionBuilder.toString());
			rolesLabel.setValue(labelBuilder.toString());

			return rolesLabel;
		}

		else if (columnId.equals(FieldConstants.GROUPS)) {
			UserDto user = ((BeanItem<UserDto>) source.getItem(itemId)).getBean();

			Set<GroupDto> groups = user.getGroups();
			List<String> sortedGroups = new ArrayList<>();
			for (GroupDto group : groups) {
				sortedGroups.add(group.getName());
			}
			Collections.sort(sortedGroups);
			Label groupsLabel = new Label();

			StringBuilder descriptionBuilder = new StringBuilder();
			StringBuilder labelBuilder = new StringBuilder();
			for (String group : sortedGroups) {
				if (descriptionBuilder.length() != 0) {
					descriptionBuilder.append("<br/>");
					labelBuilder.append(", ");
				}
				descriptionBuilder.append(group);
				labelBuilder.append(group);
			}
			groupsLabel.setDescription(descriptionBuilder.toString());

			if (groups.size() < 5) {
				groupsLabel.setValue(labelBuilder.toString());
			} else {
				groupsLabel.setValue(Messages.getString("Caption.Label.MultipleGroups", groups.size()));
			}

			return groupsLabel;
		}

		else if (columnId.equals(FieldConstants.ENABLED)) {
			Boolean enabled = (Boolean) source.getItem(itemId).getItemProperty(columnId).getValue();
			if (enabled) {
				return new Label(FontAwesome.CHECK.getHtml(), ContentMode.HTML);
			} else {
				return new Label(FontAwesome.TIMES.getHtml(), ContentMode.HTML);
			}
		}

		else if (columnId.equals(FieldConstants.EXPIRE_DATE)) {
			Property<?> property = source.getItem(itemId).getItemProperty(columnId);
			if (property != null) {
				Date expireDate = (Date) property.getValue();
				if (expireDate == null) {
					return null;
				} else {
					DateFormat date = new SimpleDateFormat(Messages.getString("Format.Date"));
					return new Label(date.format(expireDate));
				}
			} else {
				return null;
			}
		}

		else if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
			UserDto user = ((BeanItem<UserDto>) source.getItem(itemId)).getBean();

			List<PackDto> packs = permissionService.findUserPacks2(user.getId(), false);
			List<String> sortedPacks = new ArrayList<>();
			List<String> sortedPackDescs = new ArrayList<>();
			for (PackDto pack : packs) {
				sortedPacks.add(Messages.getString("Caption.Item.PackLabel", pack.getName(), pack.getId()));
				sortedPackDescs.add(Messages.getString("Caption.Item.PackDescription", pack.getName(), pack.getId(),
						pack.getDescription()));
			}
			Collections.sort(sortedPacks);
			Collections.sort(sortedPackDescs);

			StringBuilder labelBuilder = new StringBuilder();
			for (String pack : sortedPacks) {
				if (labelBuilder.length() != 0) {
					labelBuilder.append(", ");
				}
				labelBuilder.append(pack);
			}
			StringBuilder descriptionBuilder = new StringBuilder();
			descriptionBuilder.append("<ul>");
			for (String pack : sortedPackDescs) {
				descriptionBuilder.append("<li>" + pack + "</li>");
			}
			descriptionBuilder.append("</ul>");

			Label packsLabel = new Label();
			packsLabel.setDescription(descriptionBuilder.toString());

			if (packs.size() < 5) {
				packsLabel.setValue(labelBuilder.toString());
			} else {
				packsLabel.setValue(Messages.getString("Caption.Label.MultiplePacks", packs.size()));
			}

			return packsLabel;
		}

		return null;
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			if (dialog.equals(deletionConfirmDialog)) {
				try {
					deleteUsers();
					Notification.show(Messages.getString("Message.Info.UsersDeleted"));

				} catch (Exception e) {
					Notification.show(Messages.getString("Message.Error.UsersDeletion"), e.getMessage(),
							Notification.Type.ERROR_MESSAGE);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Handler
	public void addUserIntoTable(final MainUIEvent.UserAddedEvent event) {
		UserDto user = event.getUser();
		BeanContainer<Long, UserDto> container = (BeanContainer<Long, UserDto>) table.getContainerDataSource();

		container.removeItem(user.getId());
		container.addItem(user.getId(), user);

		((Table) table).sort();
	}

	@SuppressWarnings("unchecked")
	@Handler
	public void changeUserGroups(final MainUIEvent.UserGroupsChangedEvent event) {
		UserDto user = event.getUser();
		BeanContainer<Long, UserDto> container = (BeanContainer<Long, UserDto>) table.getContainerDataSource();

		container.removeItem(user.getId());
		container.addItem(user.getId(), user);
	}

	@SuppressWarnings("unchecked")
	@Handler
	public void setToolsEnabled(final MainUIEvent.UserSelectionChangedEvent event) {
		boolean itemsSelected = !((Set<Object>) table.getValue()).isEmpty();
		boolean toolsEnabled = allSelected || itemsSelected;
		buttonGroup.setEnabled(toolsEnabled);
	}

	private void editUsers(Collection<UserDto> users) {
		if (!users.isEmpty()) {
			final Set<UserDto> freshUsers = users.stream()//
					.map(UserDto::getId)//
					.map(userService::getById)//
					.filter(Objects::nonNull)//
					.collect(toSet());

			if (freshUsers.size() == 1) {
				userWindowPresenter.showWindow(freshUsers.iterator().next());
			} else {
				userWindowPresenter.showWindow(freshUsers);
			}
		}
	}

}
