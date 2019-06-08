/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import static org.hypothesis.data.api.Roles.ROLE_SUPERUSER;
import static org.hypothesis.utility.UserUtility.userHasAnyRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hypothesis.data.CaseInsensitiveItemSorter;
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
import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class UserManagementVNPresenter extends AbstractManagementPresenter implements ColumnGenerator {

	private final PermissionService permissionService;
	private final UserService userService;
	private final GroupService groupService;

	private UserWindowVNPresenter userWindowPresenter;

	public UserManagementVNPresenter() {
		permissionService = new PermissionServiceImpl();
		userService = new UserServiceImpl();
		groupService = new GroupServiceImpl();
	}

	@Override
	public void init() {
		super.init();

		userWindowPresenter = new UserWindowVNPresenter(getBus());
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
				if (!userHasAnyRole(loggedUser, ROLE_SUPERUSER)
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
		return null;
	}

	@Override
	protected Button buildUpdateButton() {
		Button updateButton = new Button(Messages.getString("Caption.Button.Update"));
		updateButton.setClickShortcut(KeyCode.ENTER);
		updateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				Collection<UserDto> users = getSelectedUsers();

				if (!users.isEmpty()) {
					if (users.size() == 1) {
						userWindowPresenter.showWindow(users.iterator().next());
					} else {
						userWindowPresenter.showWindow(users);
					}
				}
			}
		});
		return updateButton;
	}

	@Override
	protected Button buildDeleteButton() {
		return null;
	}

	@Override
	protected Resource getExportResource() {
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
	public Component buildTable() {
		FilterTable table = new FilterTable();
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
		table.addGeneratedColumn(FieldConstants.ENABLED, this);
		table.addGeneratedColumn(FieldConstants.AVAILABLE_PACKS, this);

		table.setVisibleColumns(FieldConstants.ID, FieldConstants.USERNAME, FieldConstants.NAME,
				FieldConstants.PASSWORD, FieldConstants.ROLES, FieldConstants.AVAILABLE_PACKS, FieldConstants.ENABLED);

		table.setColumnHeaders(Messages.getString("Caption.Field.Id"), Messages.getString("Caption.Field.Surname"),
				Messages.getString("Caption.Field.Name"), Messages.getString("Caption.Field.BirthNumber"),
				Messages.getString("Caption.Field.Role"), Messages.getString("Caption.Field.AvailablePacks"),
				Messages.getString("Caption.Field.Enabled"));

		table.setFilterBarVisible(true);

		table.setFilterDecorator(new UserTableFilterDecorator());
		table.setFilterFieldVisible(FieldConstants.ID, false);
		table.setFilterFieldVisible(FieldConstants.ROLES, false);
		table.setFilterFieldVisible(FieldConstants.AVAILABLE_PACKS, false);

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
					userWindowPresenter.showWindow(user);
				}
			}
		});

		this.table = table;
		return table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object generateCell(CustomTable source, Object itemId, Object columnId) {
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

		else if (columnId.equals(FieldConstants.ENABLED)) {
			Boolean enabled = (Boolean) source.getItem(itemId).getItemProperty(columnId).getValue();
			if (enabled) {
				return new Label(FontAwesome.CHECK.getHtml(), ContentMode.HTML);
			} else {
				return new Label(FontAwesome.TIMES.getHtml(), ContentMode.HTML);
			}
		}

		else if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
			UserDto user = ((BeanItem<UserDto>) source.getItem(itemId)).getBean();

			List<PackDto> packs = permissionService.getUserPacksVN(user.getId());
			List<String> sortedPacks = new ArrayList<>();
			List<String> sortedPackDescs = new ArrayList<>();
			for (PackDto pack : packs) {
				sortedPacks.add(Messages.getString("Caption.Item.PackLabel", pack.getName(), pack.getId()));
				sortedPackDescs.add(Messages.getString("Caption.Item.PackDescription", pack.getName(), pack.getId(),
						pack.getDescription()));
			}

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

		((FilterTable) table).sort();
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

	public static class UserTableFilterDecorator implements FilterDecorator {

		@Override
		public String getEnumFilterDisplayName(Object propertyId, Object value) {
			return null;
		}

		@Override
		public Resource getEnumFilterIcon(Object propertyId, Object value) {
			return null;
		}

		@Override
		public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
			if (FieldConstants.ENABLED.equals(propertyId)) {
				return Messages.getString(value ? "Caption.Label.True" : "Caption.Label.False");
			}
			return null;
		}

		@Override
		public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
			return null;
		}

		@Override
		public boolean isTextFilterImmediate(Object propertyId) {
			return true;
		}

		@Override
		public int getTextChangeTimeout(Object propertyId) {
			return 250;
		}

		@Override
		public String getFromCaption() {
			return null;
		}

		@Override
		public String getToCaption() {
			return null;
		}

		@Override
		public String getSetCaption() {
			return null;
		}

		@Override
		public String getClearCaption() {
			return null;
		}

		@Override
		public Resolution getDateFieldResolution(Object propertyId) {
			return null;
		}

		@Override
		public String getDateFormatPattern(Object propertyId) {
			return null;
		}

		@Override
		public Locale getLocale() {
			return null;
		}

		@Override
		public String getAllItemsVisibleString() {
			return null;
		}

		@Override
		public NumberFilterPopupConfig getNumberFilterPopupConfig() {
			return null;
		}

		@Override
		public boolean usePopupForNumericProperty(Object propertyId) {
			return false;
		}

	}

}
