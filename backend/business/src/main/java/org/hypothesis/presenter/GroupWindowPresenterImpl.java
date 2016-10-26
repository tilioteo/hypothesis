/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.interfaces.GroupService;
import org.hypothesis.data.interfaces.PermissionService;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.GroupPermission;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.RoleServiceImpl;
import org.hypothesis.data.validator.GroupNameValidator;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.interfaces.GroupWindowPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.table.SimpleCheckerColumnGenerator;

import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
@NormalUIScoped
public class GroupWindowPresenterImpl extends AbstractWindowPresenter implements GroupWindowPresenter {

	@Inject
	private GroupService groupService;
	@Inject
	private UserService userService;
	@Inject
	private PermissionService permissionService;

	@Inject
	private Event<MainUIEvent> mainEvent;

	private TextField idField;
	private TextField nameField;
	private Table usersField;
	private TextField noteField;
	private Table packsField;

	private void buildIdField() {
		if (idField == null) {
			idField = new TextField(Messages.getString("Caption.Field.Id"));
			idField.setEnabled(false);
		}
	}

	private void buildNameField() {
		if (nameField == null) {
			nameField = new TextField(Messages.getString("Caption.Field.Name"));
			nameField.setNullRepresentation("");
			nameField.setMaxLength(30);
			nameField.setRequired(true);
			nameField.setRequiredError(Messages.getString("Message.Error.NameRequired"));
			nameField.addValidator(
					new StringLengthValidator(Messages.getString("Message.Error.NameLength", 4, 30), 4, 30, false));
		}
	}

	private void buildNoteField() {
		if (noteField == null) {
			noteField = new TextField(Messages.getString("Caption.Field.Note"));
			noteField.setNullRepresentation("");
		}
	}

	private void buildUsersField(boolean required) {
		if (usersField == null) {
			final Table table = new Table(Messages.getString("Caption.Field.Users"));
			table.setSelectable(false);
			table.addStyleName(ValoTheme.TABLE_SMALL);
			table.addStyleName(ValoTheme.TABLE_NO_HEADER);
			table.addStyleName(ValoTheme.TABLE_BORDERLESS);
			table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
			table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
			table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
			table.addStyleName(ValoTheme.TABLE_COMPACT);

			table.addContainerProperty(FieldConstants.USERNAME, String.class, null);
			table.addContainerProperty(FieldConstants.SELECTED, Boolean.class, null);

			table.addGeneratedColumn(FieldConstants.ENABLER, new SimpleCheckerColumnGenerator(FieldConstants.SELECTED));

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					User user = (User) itemId;
					return Messages.getString("Caption.Item.UserDescription", user.getUsername(), user.getId());
				}
			});

			table.setVisibleColumns(FieldConstants.ENABLER, FieldConstants.USERNAME);
			table.setColumnHeaders(Messages.getString("Caption.Field.State"),
					Messages.getString("Caption.Field.Username"));

			table.setPageLength(table.size());

			usersField = table;

			if (required) {
				usersField.setRequired(true);
				usersField.setRequiredError(Messages.getString("Message.Error.UserRequired"));
			}
		}
	}

	private void buildPacksField() {
		if (packsField == null) {
			final Table table = new Table(Messages.getString("Caption.Field.EnabledPacks"));
			table.setSelectable(false);
			table.addStyleName(ValoTheme.TABLE_SMALL);
			table.addStyleName(ValoTheme.TABLE_NO_HEADER);
			table.addStyleName(ValoTheme.TABLE_BORDERLESS);
			table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
			table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
			table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
			table.addStyleName(ValoTheme.TABLE_COMPACT);
			table.setPageLength(8);

			table.addContainerProperty(FieldConstants.NAME, String.class, null);
			table.addContainerProperty(FieldConstants.SELECTED, Boolean.class, null);

			table.addGeneratedColumn(FieldConstants.ENABLER, new SimpleCheckerColumnGenerator(FieldConstants.SELECTED));

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					Pack pack = (Pack) itemId;
					return Messages.getString("Caption.Item.PackDescription", pack.getName(), pack.getId(),
							pack.getDescription());
				}
			});

			table.setVisibleColumns(FieldConstants.ENABLER, FieldConstants.NAME);
			table.setColumnHeaders(Messages.getString("Caption.Field.State"), Messages.getString("Caption.Field.Name"));

			table.setPageLength(table.size());

			packsField = table;
		}
	}

	private Component buildGroupDetailsForm() {
		VerticalLayout layout = new VerticalLayout();

		if (state == WindowState.MULTIUPDATE) {
			addInformationLabel(layout);
		}

		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		if (state == WindowState.UPDATE) {
			buildIdField();
			addField(form, idField);
		}

		if (state != WindowState.MULTIUPDATE) {
			buildNameField();
			addField(form, nameField);
		}

		buildNoteField();
		addField(form, noteField);

		buildUsersField(!(loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)
				|| loggedUser.hasRole(RoleServiceImpl.ROLE_MANAGER)));
		addField(form, usersField);
		// TODO: upozornit, ze uzivatel nema zadne uzivatele?

		layout.addComponent(form);

		return layout;
	}

	private void setValidationVisible(boolean visible) {
		idField.setValidationVisible(visible);
		nameField.setValidationVisible(visible);
		noteField.setValidationVisible(visible);
		if (usersField != null) {
			usersField.setValidationVisible(visible);
		}
		packsField.setValidationVisible(visible);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initFields() {
		fields = new ArrayList<>();

		// ID
		buildIdField();

		// name
		buildNameField();
		if (state == WindowState.CREATE) {
			nameField.addValidator(new GroupNameValidator(null));
		} else if (state == WindowState.UPDATE) {
			nameField.addValidator(new GroupNameValidator(((Group) source).getId()));
		}

		// note
		buildNoteField();

		// users
		Collection<User> users;

		if (loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			users = userService.findAll();
		} else {
			users = userService.findOwnerUsers(loggedUser);
		}

		if (!users.isEmpty()) {
			buildUsersField(!(loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)
					|| loggedUser.hasRole(RoleServiceImpl.ROLE_MANAGER)));

			users.forEach(e -> {
				Item row = usersField.addItem(e);
				row.getItemProperty(FieldConstants.USERNAME).setValue(e.getUsername());
			});

			((IndexedContainer) usersField.getContainerDataSource()).setItemSorter(new CaseInsensitiveItemSorter());
			usersField.sort(new Object[] { FieldConstants.USERNAME }, new boolean[] { true });
		}

		// enabled packs
		buildPacksField();

		Collection<Pack> packs;
		if (loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			packs = permissionService.findAllPacks();
		} else {
			packs = permissionService.findUserPacks2(loggedUser, false);
		}

		// TODO: upozornit, pokud nema uzivatel pristupne zadne packy?

		packs.forEach(e -> {
			Item row = packsField.addItem(e);
			row.getItemProperty(FieldConstants.NAME).setValue(e.getName());
		});

		((IndexedContainer) packsField.getContainerDataSource()).setItemSorter(new CaseInsensitiveItemSorter());
		packsField.sort(new Object[] { FieldConstants.NAME }, new boolean[] { true });
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void fillFields() {
		Group group = (Group) source;
		group = groupService.merge(group);

		idField.setValue(group.getId().toString());
		nameField.setValue(group.getName());
		noteField.setValue(group.getNote());

		// users
		if (usersField != null) {
			Set<User> users;

			if (state == WindowState.UPDATE) {
				users = group.getUsers();
			} else {
				users = new HashSet<>();
			}

			usersField.getItemIds().forEach(e -> {
				Item row = usersField.getItem(e);
				User user = userService.merge((User) e);

				if (users.contains(user)) {
					row.getItemProperty(FieldConstants.SELECTED).setValue(true);
				} else {
					row.getItemProperty(FieldConstants.SELECTED).setValue(false);
				}
			});
			}

		// packs
		Set<Pack> packs;

		if (state == WindowState.UPDATE) {
			packs = permissionService.getGroupPacks(group);
		} else {
			packs = new HashSet<>();
		}

		packsField.getItemIds().forEach(e -> {
			Item row = packsField.getItem(e);

			if (packs.contains(e)) {
				row.getItemProperty(FieldConstants.SELECTED).setValue(true);
			} else {
				row.getItemProperty(FieldConstants.SELECTED).setValue(false);
			}
		});
		}

	@Override
	protected void clearFields() {
		fields.clear();

		idField = null;
		nameField = null;
		noteField = null;
		usersField = null;
		packsField = null;
	}

	@Override
	protected void buildContent() {
		VerticalLayout content = new VerticalLayout();
		content.setSizeFull();
		window.setContent(content);

		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		tabSheet.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
		tabSheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		content.addComponent(tabSheet);
		content.setExpandRatio(tabSheet, 1f);

		tabSheet.addComponent(buildGroupDetailsTab());
		tabSheet.addComponent(buildGroupPacksTab());

		content.addComponent(buildFooter());

		setValidationVisible(false);
	}

	private Component buildGroupDetailsTab() {
		VerticalLayout tab = new VerticalLayout();
		tab.setCaption(Messages.getString("Caption.Tab.GroupDetails"));
		tab.setIcon(FontAwesome.GROUP);
		tab.setSpacing(true);
		tab.setMargin(true);
		tab.setSizeFull();

		Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.setContent(buildGroupDetailsForm());
		tab.addComponent(panel);

		return tab;
	}

	private Component buildGroupPacksTab() {
		VerticalLayout tab = new VerticalLayout();
		tab.setCaption(Messages.getString("Caption.Tab.GroupPacks"));
		tab.setIcon(FontAwesome.COG);
		tab.setSpacing(true);
		tab.setMargin(true);
		tab.setSizeFull();

		Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.setContent(buildGroupPacksForm());
		tab.addComponent(panel);

		detailsTab = tab;

		return tab;
	}

	private Component buildGroupPacksForm() {
		VerticalLayout layout = new VerticalLayout();

		if (state == WindowState.MULTIUPDATE) {
			addInformationLabel(layout);
		}

		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		addField(form, packsField);

		layout.addComponent(form);

		return layout;
	}

	private Component buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
		footer.setSpacing(true);

		Button ok = new Button(Messages.getString("Caption.Button.OK"));
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
		ok.addClickListener(e -> {
				try {
					commitForm();

					Notification success;
					if (state == WindowState.CREATE) {
						success = new Notification(Messages.getString("Message.Info.GroupAdded"));
					} else if (state == WindowState.UPDATE) {
						success = new Notification(Messages.getString("Message.Info.GroupUpdated"));
					} else {
						success = new Notification(Messages.getString("Message.Info.GroupsUpdated"));
					}
					success.setDelayMsec(2000);
					success.setPosition(Position.BOTTOM_CENTER);
					success.show(Page.getCurrent());

					window.close();

			} catch (CommitException ex) {
				Notification.show(ex.getMessage(), Type.WARNING_MESSAGE);
				}
		});
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

		Button cancel = new Button(Messages.getString("Caption.Button.Cancel"));
		cancel.addClickListener(e -> window.close());
		footer.addComponent(cancel);

		return footer;
	}

	@SuppressWarnings("unchecked")
	protected void commitForm() throws CommitException {
		for (AbstractField<?> field : fields) {
			try {
				if (field.isEnabled()) {
					field.validate();
				}
			} catch (InvalidValueException e) {
				field.focus();
				setValidationVisible(true);
				tabSheet.setSelectedTab(detailsTab);
				throw new CommitException(e.getMessage());
			}
		}

		if (state == WindowState.MULTIUPDATE) {
			((Collection<Group>) source).forEach(e -> {
				Group group = saveGroup(e);
				if (group != null) {
					mainEvent.fire(new MainUIEvent.GroupAddedEvent(group));
				}
			});

		} else {
			Group group;
			if (state == WindowState.CREATE) {
				group = new Group();
			} else {
				group = (Group) source;
			}
			group = saveGroup(group);
			if (group != null) {
				mainEvent.fire(new MainUIEvent.GroupAddedEvent(group));
			}
		}
	}

	private Group saveGroup(final Group group) {
		if (state == WindowState.CREATE) {
			group.setOwnerId(loggedUser.getId());
		}

		if (state != WindowState.MULTIUPDATE) {
			group.setName(nameField.getValue());
		}

		if (noteField.isVisible()) {
			group.setNote(noteField.getValue());
		}

		if (usersField != null && usersField.isVisible() && usersField.isEnabled()) {

			usersField.getItemIds().forEach(e -> {
				Item item = usersField.getItem(e);
				User user = userService.merge((User) e);
				Boolean selected = (Boolean) item.getItemProperty(FieldConstants.SELECTED).getValue();

				if (null == selected) {
					if (state == WindowState.MULTIUPDATE) {
						group.removeUser(user);
					}
				} else if (selected.equals(true)) {
					group.addUser(user);
				} else if (selected.equals(false)) {
					group.removeUser(user);
				}

				if (user != null) {
					mainEvent.fire(new MainUIEvent.UserGroupsChangedEvent(user));

					if (user.equals(loggedUser)) {
						SessionManager.setLoggedUser(user);
						bus.post(new MainUIEvent.UserPacksChangedEvent(user));
					}
				}
			});
			}

		final Group addedGroup = groupService.add(group);
		if (packsField.isVisible()) {
			permissionService.deleteGroupPermissions(group);

			packsField.getItemIds().forEach(e -> {
				Item item = packsField.getItem(e);
				Pack pack = (Pack) e;
				Boolean selected = (Boolean) item.getItemProperty(FieldConstants.SELECTED).getValue();

				if (selected != null && selected.equals(true)) {
					permissionService.addGroupPermission(new GroupPermission(addedGroup, pack));
				}
			});
			}

		return group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.presenter.GroupWindowPresenter#showWindow(org.hypothesis.
	 * data.model.Group)
	 */
	@Override
	public void showWindow(Group group) {
		showWindow(WindowState.UPDATE, group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.presenter.GroupWindowPresenter#showWindow(java.util.
	 * Collection)
	 */
	@Override
	public void showWindow(Collection<Group> groups) {
		showWindow(WindowState.MULTIUPDATE, groups);
	}

}
