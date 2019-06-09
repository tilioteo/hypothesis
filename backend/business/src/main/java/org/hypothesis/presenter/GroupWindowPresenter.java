/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import static org.hypothesis.data.api.Roles.ROLE_MANAGER;
import static org.hypothesis.data.api.Roles.ROLE_SUPERUSER;
import static org.hypothesis.utility.UserUtility.userHasAnyRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.dto.GroupDto;
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.UserDto;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.service.GroupService;
import org.hypothesis.data.service.PackService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.service.impl.GroupServiceImpl;
import org.hypothesis.data.service.impl.PackServiceImpl;
import org.hypothesis.data.service.impl.PermissionServiceImpl;
import org.hypothesis.data.service.impl.UserServiceImpl;
import org.hypothesis.data.validator.GroupNameValidator;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.table.SimpleCheckerColumnGenerator;

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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
public class GroupWindowPresenter extends AbstractWindowPresenter {

	private final GroupService groupService;
	private final UserService userService;
	private final PermissionService permissionService;
	private final PackService packService;

	private TextField idField;
	private TextField nameField;
	private Table usersField;
	private TextField noteField;
	private Table packsField;

	public GroupWindowPresenter(MainEventBus bus) {
		super(bus);

		groupService = new GroupServiceImpl();
		userService = new UserServiceImpl();
		permissionService = new PermissionServiceImpl();
		packService = new PackServiceImpl();
	}

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

			table.addGeneratedColumn(FieldConstants.ENABLER, new SimpleCheckerColumnGenerator(FieldConstants.SELECTED,
					Messages.getString("Caption.Button.EnablePack")));

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					UserDto user = (UserDto) itemId;
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

			table.addGeneratedColumn(FieldConstants.ENABLER, new SimpleCheckerColumnGenerator(FieldConstants.SELECTED,
					Messages.getString("Caption.Button.EnablePack")));

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					PackDto pack = (PackDto) itemId;
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

		if (state.equals(WindowState.MULTIUPDATE)) {
			addInformationLabel(layout);
		}

		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		if (state.equals(WindowState.UPDATE)) {
			buildIdField();
			addField(form, idField);
		}

		if (!(state.equals(WindowState.MULTIUPDATE))) {
			buildNameField();
			addField(form, nameField);
		}

		buildNoteField();
		addField(form, noteField);

		buildUsersField(!userHasAnyRole(loggedUser, ROLE_SUPERUSER) || userHasAnyRole(loggedUser, ROLE_MANAGER));
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
		if (state.equals(WindowState.CREATE)) {
			nameField.addValidator(new GroupNameValidator(null));
		} else if (state.equals(WindowState.UPDATE)) {
			nameField.addValidator(new GroupNameValidator(((GroupDto) source).getId()));
		}

		// note
		buildNoteField();

		// users
		Collection<UserDto> users;

		if (userHasAnyRole(loggedUser, ROLE_SUPERUSER)) {
			users = userService.findAll();
		} else {
			users = userService.findOwnerUsers(loggedUser.getId());
		}

		if (!users.isEmpty()) {
			buildUsersField(userHasAnyRole(loggedUser, ROLE_SUPERUSER, ROLE_MANAGER));

			for (UserDto user : users) {
				Table table = usersField;
				table.addItem(user);
				Item row = table.getItem(user);
				row.getItemProperty(FieldConstants.USERNAME).setValue(user.getUsername());
			}

			((IndexedContainer) ((Table) usersField).getContainerDataSource())
					.setItemSorter(new CaseInsensitiveItemSorter());
			((Table) usersField).sort(new Object[] { FieldConstants.USERNAME }, new boolean[] { true });
		}

		// enabled packs
		buildPacksField();

		Collection<PackDto> packs;
		if (userHasAnyRole(loggedUser, ROLE_SUPERUSER)) {
			packs = packService.findAll();
		} else {
			packs = permissionService.findUserPacks2(loggedUser.getId(), false);
		}

		// TODO: upozornit, pokud nema uzivatel pristupne zadne packy?

		for (PackDto pack : packs) {
			Table table = packsField;
			table.addItem(pack);
			Item row = table.getItem(pack);
			row.getItemProperty(FieldConstants.NAME).setValue(pack.getName());
		}

		((IndexedContainer) ((Table) packsField).getContainerDataSource())
				.setItemSorter(new CaseInsensitiveItemSorter());
		((Table) packsField).sort(new Object[] { FieldConstants.NAME }, new boolean[] { true });
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void fillFields() {
		GroupDto group = (GroupDto) source;

		idField.setValue(group.getId().toString());
		nameField.setValue(group.getName());
		noteField.setValue(group.getNote());

		// users
		if (usersField != null) {
			Set<UserDto> users;

			if (state.equals(WindowState.UPDATE)) {
				users = group.getUsers();
			} else {
				users = new HashSet<>();
			}

			for (Object itemId : usersField.getItemIds()) {
				Item row = usersField.getItem(itemId);
				UserDto user = (UserDto) itemId;

				if (users.contains(user)) {
					row.getItemProperty(FieldConstants.SELECTED).setValue(true);
				} else {
					row.getItemProperty(FieldConstants.SELECTED).setValue(false);
				}
			}
		}

		// packs
		List<PackDto> packs;

		if (state.equals(WindowState.UPDATE)) {
			packs = permissionService.getGroupPacks(group.getId());
		} else {
			packs = new ArrayList<>();
		}

		for (Object itemId : packsField.getItemIds()) {
			Item row = packsField.getItem(itemId);
			PackDto pack = (PackDto) itemId;

			if (packs.contains(pack)) {
				row.getItemProperty(FieldConstants.SELECTED).setValue(true);
			} else {
				row.getItemProperty(FieldConstants.SELECTED).setValue(false);
			}
		}
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

		if (state.equals(WindowState.MULTIUPDATE)) {
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
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					commitForm();

					Notification success = null;
					if (state.equals(WindowState.CREATE)) {
						success = new Notification(Messages.getString("Message.Info.GroupAdded"));
					} else if (state.equals(WindowState.UPDATE)) {
						success = new Notification(Messages.getString("Message.Info.GroupUpdated"));
					} else {
						success = new Notification(Messages.getString("Message.Info.GroupsUpdated"));
					}
					success.setDelayMsec(2000);
					success.setPosition(Position.BOTTOM_CENTER);
					success.show(Page.getCurrent());

					window.close();

				} catch (CommitException e) {
					Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
				}
			}
		});
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

		Button cancel = new Button(Messages.getString("Caption.Button.Cancel"));
		cancel.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				window.close();
			}
		});
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

		if (state.equals(WindowState.MULTIUPDATE)) {
			for (GroupDto group : (Collection<GroupDto>) source) {
				group = saveGroup(group);
				if (group != null) {
					bus.post(new MainUIEvent.GroupAddedEvent(group));
				}
			}

		} else {
			GroupDto group;
			if (state.equals(WindowState.CREATE)) {
				group = new GroupDto();
			} else {
				group = (GroupDto) source;
			}
			group = saveGroup(group);
			if (group != null) {
				bus.post(new MainUIEvent.GroupAddedEvent(group));
			}
		}
	}

	private GroupDto saveGroup(GroupDto group) {
		UserDto updatedLoggedUser = null;

		if (state.equals(WindowState.CREATE)) {
			group.setOwnerId(loggedUser.getId());
		}

		if (!(state.equals(WindowState.MULTIUPDATE))) {
			group.setName(nameField.getValue());
		}

		if (noteField.isVisible()) {
			group.setNote(noteField.getValue());
		}

		if (usersField != null && usersField.isVisible() && usersField.isEnabled()) {

			for (Object itemId : usersField.getItemIds()) {
				Item item = usersField.getItem(itemId);
				UserDto user = (UserDto) itemId;
				Boolean selected = (Boolean) item.getItemProperty(FieldConstants.SELECTED).getValue();

				if (selected == null) {
					if (state.equals(WindowState.MULTIUPDATE)) {
						group.getUsers().remove(user);
					}
				} else if (selected.equals(true)) {
					group.getUsers().add(user);
				} else if (selected.equals(false)) {
					group.getUsers().remove(user);
				}

				if (user != null) {
					bus.post(new MainUIEvent.UserGroupsChangedEvent(user));

					if (user.equals(loggedUser)) {
						updatedLoggedUser = user;
					}
				}
			}
		}

		group = groupService.save(group);

		if (packsField.isVisible()) {
			final Set<PackDto> packs = new HashSet<>();
			for (Object itemId : packsField.getItemIds()) {
				Item item = packsField.getItem(itemId);
				PackDto pack = (PackDto) itemId;
				Boolean selected = (Boolean) item.getItemProperty(FieldConstants.SELECTED).getValue();

				if (selected != null && selected.equals(true)) {
					packs.add(pack);
				}
			}
			permissionService.setGroupPermissions(group.getId(), packs);
		}

		if (updatedLoggedUser != null) {
			SessionManager.setLoggedUser2(userService.getSimpleById(updatedLoggedUser.getId()));
			bus.post(new MainUIEvent.UserPacksChangedEvent(updatedLoggedUser));
		}

		return group;
	}

	public void showWindow(GroupDto group) {
		showWindow(WindowState.UPDATE, group);
	}

	public void showWindow(Collection<GroupDto> groups) {
		showWindow(WindowState.MULTIUPDATE, groups);
	}

}
