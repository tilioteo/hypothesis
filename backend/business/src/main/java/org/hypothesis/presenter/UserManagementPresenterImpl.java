/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.common.IntSequence;
import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.interfaces.GroupService;
import org.hypothesis.data.interfaces.PermissionService;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Role;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.RoleServiceImpl;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.interfaces.UserManagementPresenter;
import org.hypothesis.interfaces.UserWindowPresenter;
import org.hypothesis.server.Messages;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.cdi.NormalViewScoped;
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
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
@NormalViewScoped
public class UserManagementPresenterImpl extends AbstractManagementPresenter implements UserManagementPresenter {

	@Inject
	private PermissionService permissionService;
	@Inject
	private UserService userService;
	@Inject
	private GroupService groupService;

	@Inject
	private UserWindowPresenter userWindowPresenter;

	@Inject
	private Event<MainUIEvent> mainEvent;

	public UserManagementPresenterImpl() {
		System.out.println("Construct " + getClass().getName());
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
				User user = SessionManager.getLoggedUser();

				if (!user.hasRole(RoleServiceImpl.ROLE_SUPERUSER) && groupService.findOwnerGroups(user).isEmpty()) {
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
			@Override
			public void valueChange(ValueChangeEvent event) {
				allSelected = selectionType.getValue().equals(Messages.getString("Caption.Item.All"));
				mainEvent.fire(new MainUIEvent.UserSelectionChangedEvent());
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
				Collection<User> users = getSelectedUsers();

				if (users.size() == 1) {
					userWindowPresenter.showWindow(users.iterator().next());
				} else {
					userWindowPresenter.showWindow(users);
				}
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
						UserManagementPresenterImpl.this);
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

			final IntSequence seq = new IntSequence();
			Row row = sheet.createRow(seq.next());
			sheet.createFreezePane(0, 1);

			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Id"));
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Name"));
			row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Password"));

			getSelectedUsers().forEach(e -> {
				Row r = sheet.createRow(seq.next());
				r.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(e.getId());
				r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(e.getUsername());
				r.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(e.getPassword());
			});

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
		User loggedUser = SessionManager.getLoggedUser();

		Collection<User> users = getSelectedUsers();

		checkDeletionPermission(loggedUser, users);
		checkSuperuserLeft(loggedUser);

		users.forEach(e -> {
			userService.delete(e);

			e.getGroups().stream().filter(Objects::nonNull)
					.forEach(i -> bus.post(new MainUIEvent.GroupUsersChangedEvent(i)));

			table.removeItem(e.getId());
		});

		if (users.contains(loggedUser)) {
			mainEvent.fire(new MainUIEvent.UserLoggedOutEvent());
		}
	}

	private void checkDeletionPermission(User currentUser, Collection<User> users) {
		String exceptionMessage = Messages.getString("Message.Error.SuperuserDelete");

		if (currentUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			return;
		}

		if (allSelected) {
			throw new UnsupportedOperationException(exceptionMessage);
		}

		if (users.stream().anyMatch(e -> e.hasRole(RoleService.ROLE_SUPERUSER))) {
				throw new UnsupportedOperationException(exceptionMessage);
			}
		}

	@SuppressWarnings("unchecked")
	private void checkSuperuserLeft(User currentUser) {
		String exceptionMessage = Messages.getString("Message.Error.SuperuserLeft");

		if (allSelected) {
			throw new UnsupportedOperationException(exceptionMessage);
		}

		if (currentUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			boolean superuserLeft = ((Collection<Long>) table.getItemIds()).stream().filter(f -> !table.isSelected(f))
					.map(m -> ((BeanItem<User>) table.getItem(m)).getBean())
					.anyMatch(e -> e.hasRole(RoleService.ROLE_SUPERUSER));

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
	private Collection<User> getSelectedUsers() {
		return getSelectedUserIds().stream().map(m -> ((BeanItem<User>) table.getItem(m)).getBean())
				.collect(Collectors.toSet());
		}

	@Override
	public Table buildTable() {
		table = new Table();
		table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setColumnCollapsingAllowed(true);
		table.setSortContainerPropertyId(FieldConstants.USERNAME);

		BeanContainer<Long, User> dataSource = new BeanContainer<>(User.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);

		User loggedUser = SessionManager.getLoggedUser();

		List<User> users;
		if (loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			users = userService.findAll();
		} else {
			users = userService.findOwnerUsers(loggedUser);
		}
		users.forEach(e -> {
			User user = userService.merge(e);
			dataSource.addBean(user);
		});
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
				mainEvent.fire(new MainUIEvent.UserSelectionChangedEvent());
			}
		});

		table.addItemClickListener(new ItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					User user = ((BeanItem<User>) event.getItem()).getBean();
					userWindowPresenter.showWindow(user);
				}
			}
		});

		return table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		if (columnId.equals(FieldConstants.ROLES)) {
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();
			user = userService.merge(user);

			Set<Role> roles = user.getRoles();
			List<String> sortedRoles = new ArrayList<>();
			roles.stream().map(Role::getName).forEach(sortedRoles::add);

			Collections.sort(sortedRoles);
			Label rolesLabel = new Label();

			rolesLabel.setDescription(sortedRoles.stream().collect(Collectors.joining("<br/>")));
			rolesLabel.setValue(sortedRoles.stream().collect(Collectors.joining(", ")));

			return rolesLabel;
		}

		else if (columnId.equals(FieldConstants.GROUPS)) {
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();
			user = userService.merge(user);

			Set<Group> groups = user.getGroups();
			// FIXME sort in stream
			List<String> sortedGroups = new ArrayList<>();
			groups.stream().map(Group::getName).forEach(sortedGroups::add);

			Collections.sort(sortedGroups);
			Label groupsLabel = new Label();

			groupsLabel.setDescription(sortedGroups.stream().collect(Collectors.joining("<br/>")));

			if (groups.size() < 5) {
				groupsLabel.setValue(sortedGroups.stream().collect(Collectors.joining(", ")));
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
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();

			Set<Pack> packs = permissionService.findUserPacks2(user, false);
			List<String> sortedPacks = new ArrayList<>();
			List<String> sortedPackDescs = new ArrayList<>();

			packs.stream().forEach(e -> {
				sortedPacks.add(Messages.getString("Caption.Item.PackLabel", e.getName(), e.getId()));
				sortedPackDescs.add(
						Messages.getString("Caption.Item.PackDescription", e.getName(), e.getId(), e.getDescription()));
			});
			Collections.sort(sortedPacks);
			Collections.sort(sortedPackDescs);

			StringBuilder descriptionBuilder = new StringBuilder();
			descriptionBuilder.append("<ul>");
			descriptionBuilder.append(sortedPackDescs.stream().collect(Collectors.joining("", "<li>", "</li>")));
			descriptionBuilder.append("</ul>");

			Label packsLabel = new Label();
			packsLabel.setDescription(descriptionBuilder.toString());

			if (packs.size() < 5) {
				packsLabel.setValue(sortedPacks.stream().collect(Collectors.joining(", ")));
			} else {
				packsLabel.setValue(Messages.getString("Caption.Label.MultiplePacks", packs.size()));
			}

			return packsLabel;
		}

		return null;
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed() && dialog.equals(deletionConfirmDialog)) {
			try {
				deleteUsers();
				Notification.show(Messages.getString("Message.Info.UsersDeleted"));

			} catch (Exception e) {
				Notification.show(Messages.getString("Message.Error.UsersDeletion"), e.getMessage(),
						Notification.Type.ERROR_MESSAGE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.presenter.UserManagementPresenter#addUserIntoTable(org.
	 * hypothesis.event.interfaces.MainUIEvent.UserAddedEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void addUserIntoTable(@Observes final MainUIEvent.UserAddedEvent event) {
		User user = event.getUser();
		BeanContainer<Long, User> container = (BeanContainer<Long, User>) table.getContainerDataSource();

		container.removeItem(user.getId());
		container.addItem(user.getId(), user);

		table.sort();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.presenter.UserManagementPresenter#changeUserGroups(org.
	 * hypothesis.event.interfaces.MainUIEvent.UserGroupsChangedEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void changeUserGroups(@Observes final MainUIEvent.UserGroupsChangedEvent event) {
		User user = event.getUser();
		BeanContainer<Long, User> container = (BeanContainer<Long, User>) table.getContainerDataSource();

		container.removeItem(user.getId());
		container.addItem(user.getId(), user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.presenter.UserManagementPresenter#setToolsEnabled(org.
	 * hypothesis.event.interfaces.MainUIEvent.UserSelectionChangedEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setToolsEnabled(@Observes final MainUIEvent.UserSelectionChangedEvent event) {
		boolean itemsSelected = !((Set<Object>) table.getValue()).isEmpty();
		boolean toolsEnabled = allSelected || itemsSelected;
		buttonGroup.setEnabled(toolsEnabled);
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("PostConstruct " + getClass().getName());
	}

}
