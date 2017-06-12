/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Role;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.GroupService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
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
		permissionService = PermissionService.newInstance();
		userService = UserService.newInstance();
		groupService = GroupService.newInstance();
	}

	@Override
	public void init() {
		userWindowPresenter = new UserWindowVNPresenter(bus);
	}

	@Override
	public void setMainEventBus(MainEventBus bus) {
		if (this.bus != null) {
			this.bus.unregister(this);
		}

		super.setMainEventBus(bus);
		if (this.bus != null) {
			this.bus.register(this);
		}
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
				if (!loggedUser.hasRole(RoleService.ROLE_SUPERUSER)
						&& groupService.findOwnerGroups(loggedUser).isEmpty()) {
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
				Collection<User> users = getSelectedUsers();

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
		Collection<User> users = getSelectedUsers();

		checkDeletionPermission(loggedUser, users);
		checkSuperuserLeft(loggedUser);

		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			// user = userService.merge(user);

			userService.delete(user);

			for (Group group : user.getGroups()) {
				if (group != null) {
					bus.post(new MainUIEvent.GroupUsersChangedEvent(group));
				}
			}

			table.removeItem(user.getId());
		}

		if (users.contains(loggedUser)) {
			bus.post(new MainUIEvent.UserLoggedOutEvent());
		}
	}

	private void checkDeletionPermission(User currentUser, Collection<User> users) {
		String exceptionMessage = Messages.getString("Message.Error.SuperuserDelete");

		if (currentUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			return;
		}

		if (allSelected) {
			throw new UnsupportedOperationException(exceptionMessage);
		}

		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			if (user.hasRole(RoleService.ROLE_SUPERUSER)) {
				throw new UnsupportedOperationException(exceptionMessage);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void checkSuperuserLeft(User currentUser) {
		String exceptionMessage = Messages.getString("Message.Error.SuperuserLeft");

		if (allSelected) {
			throw new UnsupportedOperationException(exceptionMessage);
		}

		if (currentUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			boolean superuserLeft = false;
			for (Iterator<Long> iterator = ((Collection<Long>) table.getItemIds()).iterator(); iterator.hasNext();) {
				Long id = iterator.next();
				if (!table.isSelected(id)) {
					User user = ((BeanItem<User>) table.getItem(id)).getBean();
					if (user.hasRole(RoleService.ROLE_SUPERUSER)) {
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
	private Collection<User> getSelectedUsers() {
		Collection<User> users = new HashSet<>();
		for (Long id : getSelectedUserIds()) {
			users.add(((BeanItem<User>) table.getItem(id)).getBean());
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

		BeanContainer<Long, User> dataSource = new BeanContainer<Long, User>(User.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);

		List<User> users;
		if (loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			users = userService.findAll();
		} else {
			users = userService.findOwnerUsers(loggedUser);
		}
		for (User user : users) {
			user = userService.merge(user);
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
				bus.post(new MainUIEvent.UserSelectionChangedEvent());
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

		this.table = table;
		return table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object generateCell(CustomTable source, Object itemId, Object columnId) {
		if (columnId.equals(FieldConstants.ROLES)) {
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();
			user = userService.merge(user);

			Set<Role> roles = user.getRoles();
			List<String> sortedRoles = new ArrayList<>();
			for (Role role : roles) {
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
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();

			Set<Pack> packs = permissionService.findUserPacks2(user, false);
			List<String> sortedPacks = new ArrayList<>();
			List<String> sortedPackDescs = new ArrayList<>();
			for (Pack pack : packs) {
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
		User user = event.getUser();
		BeanContainer<Long, User> container = (BeanContainer<Long, User>) table.getContainerDataSource();

		container.removeItem(user.getId());
		container.addItem(user.getId(), user);

		((FilterTable) table).sort();
	}

	@SuppressWarnings("unchecked")
	@Handler
	public void changeUserGroups(final MainUIEvent.UserGroupsChangedEvent event) {
		User user = event.getUser();
		BeanContainer<Long, User> container = (BeanContainer<Long, User>) table.getContainerDataSource();

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
