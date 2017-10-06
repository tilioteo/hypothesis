package org.hypothesis.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Gender;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.GroupPermission;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Role;
import org.hypothesis.data.model.User;
import org.hypothesis.data.model.UserPermission;
import org.hypothesis.data.service.GroupService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.validator.RoleValidator;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.table.CheckTable;
import org.hypothesis.ui.table.DoubleCheckerColumnGenerator;
import org.hypothesis.ui.table.DoubleCheckerColumnGenerator.Status;
import org.hypothesis.ui.table.SimpleCheckerColumnGenerator;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;

import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class UserWindowVNPresenter extends AbstractWindowPresenter {

	private final GroupService groupService;
	private final UserService userService;
	private final RoleService roleService;
	private final PermissionService permissionService;

	private TextField idField;
	private TextField usernameField;
	private TextField nameField;
	private TextField passwordField;
	private TextField educationField;
	private ComboBox genderField;
	private DateField birthDateField;
	private OptionGroup rolesField;
	private CheckBox enabledField;
	// private PopupDateField expireDateField;
	private TextField noteField;
	private Table groupsField;
	private Table packsField;

	private boolean committed = false;

	public UserWindowVNPresenter(MainEventBus bus) {
		super(bus);

		groupService = GroupService.newInstance();
		userService = UserService.newInstance();
		roleService = RoleService.newInstance();
		permissionService = PermissionService.newInstance();
	}

	private void buildIdField() {
		if (idField == null) {
			idField = new TextField(Messages.getString("Caption.Field.Id"));
			idField.setEnabled(false);
		}
	}

	private void buildUsernameField() {
		if (usernameField == null) {
			usernameField = new TextField(Messages.getString("Caption.Field.SurnameAsUsername"));
			usernameField.setNullRepresentation("");
			usernameField.setMaxLength(30);
			usernameField.setRequired(true);
			usernameField.setRequiredError(Messages.getString("Message.Error.UsernameRequired"));
			// usernameField.addValidator(
			// new
			// StringLengthValidator(Messages.getString("Message.Error.UsernameLength",
			// 4, 30), 4, 30, false));
		}
	}

	private void buildNameField() {
		if (nameField == null) {
			nameField = new TextField(Messages.getString("Caption.Field.Name"));
			nameField.setNullRepresentation("");
		}
	}

	private void buildPasswordField() {
		if (passwordField == null) {
			passwordField = new TextField(Messages.getString("Caption.Field.BirthnumAsPassword"));
			passwordField.setNullRepresentation("");
			passwordField.setMaxLength(30);
			passwordField.setRequired(true);
			usernameField.setRequiredError(Messages.getString("Message.Error.PasswordRequired"));
			passwordField.addValidator(
					new StringLengthValidator(Messages.getString("Message.Error.PasswordLength", 4, 30), 4, 30, false));
		}
	}

	private void buildRolesField() {
		if (rolesField == null) {
			rolesField = new OptionGroup(Messages.getString("Caption.Field.Role"));
			rolesField.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
			rolesField.setItemCaptionPropertyId(FieldConstants.NAME);
			rolesField.setMultiSelect(true);

			BeanItemContainer<Role> dataSource = new BeanItemContainer<Role>(Role.class);
			rolesField.setContainerDataSource(dataSource);

			// rolesField.setRequired(true);
			// usernameField.setRequiredError(Messages.getString("Message.Error.RoleRequired"));
		}
	}

	private void buildEnabledField() {
		if (enabledField == null) {
			enabledField = new CheckBox(Messages.getString("Caption.Field.Enabled"));
		}
	}

	// private void buildExpireDateField() {
	// if (expireDateField == null) {
	// expireDateField = new
	// PopupDateField(Messages.getString("Caption.Field.ExpireDate"));
	// expireDateField.setResolution(Resolution.DAY);
	// expireDateField.setDateFormat(Messages.getString("Format.Date"));
	// }
	// }

	private void buildNoteField() {
		if (noteField == null) {
			noteField = new TextField(Messages.getString("Caption.Field.Note"));
			noteField.setNullRepresentation("");
		}
	}

	private void buildEducationField() {
		if (educationField == null) {
			educationField = new TextField(Messages.getString("Caption.Field.Education"));
			educationField.setNullRepresentation("");
		}
	}
	
	private void buildGenderField() {
		if (genderField == null) {
			genderField = new ComboBox(Messages.getString("Caption.Field.Gender"));
			genderField.setTextInputAllowed(false);
			//genderField.setNullSelectionAllowed(false);

			genderField.addItem(Gender.MALE);
			genderField.addItem(Gender.FEMALE);
			genderField.setItemCaption(Gender.MALE, Messages.getString(Gender.MALE.getMessageCode()));
			genderField.setItemCaption(Gender.FEMALE, Messages.getString(Gender.FEMALE.getMessageCode()));
		}
	}
	
	private void buildBirthDateField() {
		if (birthDateField == null) {
			birthDateField = new DateField(Messages.getString("Caption.Field.DateOfBirth"));
		}
	}

	private void buildGroupsField(boolean required) {
		if (groupsField == null) {
			final Table table = new CheckTable(Messages.getString("Caption.Field.Groups"));
			table.setSelectable(false);
			table.addStyleName(ValoTheme.TABLE_SMALL);
			table.addStyleName(ValoTheme.TABLE_NO_HEADER);
			table.addStyleName(ValoTheme.TABLE_BORDERLESS);
			table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
			table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
			table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
			table.addStyleName(ValoTheme.TABLE_COMPACT);

			table.addContainerProperty(FieldConstants.NAME, String.class, null);
			table.addContainerProperty(FieldConstants.SELECTED, Boolean.class, null);

			table.addGeneratedColumn(FieldConstants.ENABLER, new SimpleCheckerColumnGenerator(FieldConstants.SELECTED,
					Messages.getString("Caption.Button.EnablePack")));

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					Group group = (Group) itemId;
					return Messages.getString("Caption.Item.GroupDescription", group.getName(), group.getId());
				}
			});

			table.setVisibleColumns(FieldConstants.ENABLER, FieldConstants.NAME);
			table.setColumnHeaders(Messages.getString("Caption.Field.State"), Messages.getString("Caption.Field.Name"));

			table.setPageLength(table.size());

			groupsField = table;

			// TODO: vymyslet validator na kontrolu vyberu
			// (nevybiram primo v tabulce), vyhnout se CheckTable
			// (je prilis zjednodsena a nedoladena)
			if (required) {
				groupsField.setRequired(true);
				groupsField.setRequiredError(Messages.getString("Message.Error.GroupRequired"));
			}
		}
	}

	private void buildPacksField() {
		if (packsField == null) {
			final Table table = new Table(Messages.getString("Caption.Field.Packs"));
			table.setSizeUndefined();
			table.setSelectable(false);
			table.addStyleName(ValoTheme.TABLE_SMALL);
			table.addStyleName(ValoTheme.TABLE_NO_HEADER);
			table.addStyleName(ValoTheme.TABLE_BORDERLESS);
			table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
			table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
			table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
			table.addStyleName(ValoTheme.TABLE_COMPACT);

			table.addContainerProperty(FieldConstants.NAME, String.class, null);
			table.addContainerProperty(FieldConstants.TEST_STATE, Status.class, null);

			table.addGeneratedColumn(FieldConstants.TEST_ENABLER,
					new DoubleCheckerColumnGenerator(FieldConstants.TEST_STATE,
							Messages.getString("Caption.Button.EnablePack"),
							Messages.getString("Caption.Button.DisablePack")));

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					Pack pack = (Pack) itemId;
					return Messages.getString("Caption.Item.PackDescription", pack.getName(), pack.getId(),
							pack.getDescription());
				}
			});

			table.setVisibleColumns(FieldConstants.TEST_ENABLER, FieldConstants.NAME);
			table.setColumnHeaders(Messages.getString("Caption.Field.State"), Messages.getString("Caption.Field.Name"));

			table.setPageLength(table.size());

			packsField = table;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initFields() {
		fields = new ArrayList<>();

		// ID
		buildIdField();

		// username
		buildUsernameField();
		// if (state.equals(WindowState.CREATE)) {
		// usernameField.addValidator(new UsernameValidator(null));
		// } else if (state.equals(WindowState.UPDATE)) {
		// usernameField.addValidator(new UsernameValidator(((User)
		// source).getId()));
		// }

		// name
		buildNameField();

		// password
		buildPasswordField();

		// roles
		buildRolesField();

		BeanItemContainer<Role> rolesSource = (BeanItemContainer<Role>) ((AbstractSelect) rolesField)
				.getContainerDataSource();
		rolesSource.addAll(roleService.findAll());
		rolesSource.sort(new Object[] { FieldConstants.ID }, new boolean[] { true });

		if (!loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			rolesField.select(RoleService.ROLE_USER);
			rolesField.setEnabled(false);
		} else if (!state.equals(WindowState.CREATE)) {
			rolesField.setRequired(true);
			rolesField.setRequiredError(Messages.getString("Message.Error.RoleRequired"));
			rolesField.addValidator(new RoleValidator(source, loggedUser));
		}

		if (state == WindowState.CREATE) {
			Set<Role> roles = new HashSet<>();
			roles.add(RoleService.ROLE_USER);
			rolesField.setValue(roles);
		}

		if (!loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			rolesField.setItemEnabled(RoleService.ROLE_SUPERUSER, false);
			rolesField.setItemEnabled(RoleService.ROLE_MANAGER, false);
			rolesField.setItemEnabled(RoleService.ROLE_USER, false);
		}

		// enabled
		buildEnabledField();

		// note
		buildNoteField();
		
		// gender
		buildGenderField();
		
		// education
		buildEducationField();
		
		// date of birth
		buildBirthDateField();

		// groups
		Collection<Group> groups;

		if (loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			groups = groupService.findAll();
		} else {
			groups = groupService.findOwnerGroups(loggedUser);
		}

		if (!groups.isEmpty()) {
			buildGroupsField(!loggedUser.hasRole(RoleService.ROLE_SUPERUSER));

			for (Group group : groups) {
				groupsField.addItem(group);
				Item row = groupsField.getItem(group);
				row.getItemProperty(FieldConstants.NAME).setValue(group.getName());
			}

			((IndexedContainer) groupsField.getContainerDataSource()).setItemSorter(new CaseInsensitiveItemSorter());
			groupsField.sort(new Object[] { FieldConstants.NAME }, new boolean[] { true });
		}

		// packs
		buildPacksField();

		Collection<Pack> packs;
		if (loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			packs = permissionService.findAllPacks();
		} else {
			packs = permissionService.findUserPacks2(loggedUser, false);
		}

		// TODO: upozornit, pokud nema uzivatel pristupne zadne packy?

		for (Pack pack : packs) {
			packsField.addItem(pack);
			Item row = packsField.getItem(pack);
			row.getItemProperty(FieldConstants.NAME).setValue(pack.getName());
		}

		((IndexedContainer) packsField.getContainerDataSource()).setItemSorter(new CaseInsensitiveItemSorter());
		packsField.sort(new Object[] { FieldConstants.NAME }, new boolean[] { true });
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void fillFields() {
		User user = (User) source;
		user = userService.merge(user);

		idField.setValue(user.getId().toString());
		usernameField.setValue(user.getUsername());
		nameField.setValue(user.getName());
		passwordField.setValue(user.getPassword());
		rolesField.setValue(user.getRoles());
		enabledField.setValue(user.getEnabled());
		// expireDateField.setValue(user.getExpireDate());
		noteField.setValue(user.getNote());
		genderField.select(Gender.get(user.getGender()));
		educationField.setValue(user.getEducation());
		birthDateField.setValue(user.getBirthDate());

		// groups
		if (groupsField != null) {
			Set<Group> groups;

			if (state.equals(WindowState.UPDATE)) {
				groups = user.getGroups();
			} else {
				groups = new HashSet<>();
			}

			for (Object itemId : groupsField.getItemIds()) {
				Item row = groupsField.getItem(itemId);
				Group group = groupService.merge((Group) itemId);

				if (groups.contains(group)) {
					row.getItemProperty(FieldConstants.SELECTED).setValue(true);
				} else {
					row.getItemProperty(FieldConstants.SELECTED).setValue(false);
				}
			}
		}

		// packs
		Set<Pack> enabledPacks;
		Set<Pack> disabledPacks;
		Set<Pack> groupPacks = new HashSet<>();

		if (state.equals(WindowState.UPDATE)) {
			enabledPacks = permissionService.getUserPacks(user, true, null);
			disabledPacks = permissionService.getUserPacks(user, false, null);
		} else {
			enabledPacks = new HashSet<>();
			disabledPacks = new HashSet<>();
		}

		if (state != WindowState.MULTIUPDATE && !user.getGroups().isEmpty()) {
			for (GroupPermission groupPermission : permissionService.getGroupsPermissions(user.getGroups())) {
				groupPacks.add(groupPermission.getPack());
			}
		}

		for (Object itemId : packsField.getItemIds()) {
			Item row = packsField.getItem(itemId);
			Pack pack = (Pack) itemId;
			
			Status state = Status.NONE;
			
			if (groupPacks.contains(pack)) {
				if (disabledPacks.contains(pack)) {
					state = Status.DISABLED_OVERRIDE;
				} else {
					state = Status.ENABLED_INHERITED;
				}
			} else {
				if (enabledPacks.contains(pack)) {
					state = Status.ENABLED;
				} else if (disabledPacks.contains(pack)) {
					state = Status.DISABLED;
				}
			}

			row.getItemProperty(FieldConstants.TEST_STATE).setValue(state);
		}
	}

	@Override
	protected void clearFields() {
		fields.clear();

		idField = null;
		usernameField = null;
		nameField = null;
		passwordField = null;
		rolesField = null;
		enabledField = null;
		// expireDateField = null;
		noteField = null;
		groupsField = null;
		packsField = null;
		genderField = null;
		educationField = null;
		birthDateField = null;
	}

	@Override
	protected void buildContent() {
		VerticalLayout content = new VerticalLayout();
		content.setSizeFull();
		window.setContent(content);

		HorizontalLayout innerLayout = new HorizontalLayout();
		innerLayout.setSizeFull();

		content.addComponent(innerLayout);
		content.setExpandRatio(innerLayout, 1f);

		innerLayout.addComponent(buildUserDetail());
		innerLayout.addComponent(buildUserRelations());

		// Component userContent = buildUserContent();
		// content.addComponent(userContent);
		// content.setExpandRatio(userContent, 1f);

		// tabSheet = new TabSheet();
		// tabSheet.setSizeFull();
		// tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		// tabSheet.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
		// tabSheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		// content.addComponent(tabSheet);
		// content.setExpandRatio(tabSheet, 1f);

		// tabSheet.addComponent(buildUserDetailsTab());
		// tabSheet.addComponent(buildUserGroupsTab());
		// tabSheet.addComponent(buildUserTestsTab());

		content.addComponent(buildFooter());

		setValidationVisible(false);
	}

	private Component buildUserRelations() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		layout.addComponent(buildUserPacks());
		layout.addComponent(buildUserGroups());

		return layout;
	}

	private Component buildUserGroups() {
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setCaption(Messages.getString("Caption.Tab.UserGroups"));
		panel.setIcon(FontAwesome.GROUP);

		// panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.setContent(buildUserGroupsForm());

		return panel;
	}

	private Component buildUserPacks() {
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setCaption(Messages.getString("Caption.Tab.UserPacks"));
		panel.setIcon(FontAwesome.COG);

		// panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.setContent(buildUserPacksForm());

		return panel;
	}

	private Component buildUserDetail() {
		Panel panel = new Panel();
		panel.setSizeFull();

		panel.setCaption(Messages.getString("Caption.Tab.UserDetails"));
		panel.setIcon(FontAwesome.USER);

		// panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.setContent(buildUserDetailsForm());

		return panel;
	}

	// private Component buildUserContent() {
	// HorizontalLayout mainLayout = new HorizontalLayout();
	// mainLayout.setSizeFull();
	//
	// VerticalLayout subLayout = new VerticalLayout();
	// subLayout.setSizeFull();
	// subLayout.addComponent(buildUserTestsTab());
	// subLayout.addComponent(buildUserGroupsTab());
	//
	// mainLayout.addComponent(buildUserDetailsTab());
	// mainLayout.addComponent(subLayout);
	//
	//
	// return mainLayout;
	// }

	// private Component buildUserDetailsTab() {
	// VerticalLayout tab = new VerticalLayout();
	// tab.setCaption(Messages.getString("Caption.Tab.UserDetails"));
	// tab.setIcon(FontAwesome.USER);
	// tab.setSpacing(true);
	// tab.setMargin(true);
	// tab.setSizeFull();
	//
	// Panel panel = new Panel();
	// panel.setSizeFull();
	// panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
	// panel.setContent(buildUserDetailsForm());
	// tab.addComponent(panel);
	//
	// //detailsTab = tab;
	//
	// return tab;
	// }

	private Component buildUserDetailsForm() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		if (state.equals(WindowState.MULTIUPDATE)) {
			addInformationLabel(layout);
		}

		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		// ID
		if (state.equals(WindowState.UPDATE)) {
			addField(form, idField);
		}

		if (state != WindowState.MULTIUPDATE) {
			// username
			addField(form, usernameField);

			// name
			addField(form, nameField);

			// password
			addField(form, passwordField);
		}

		addField(form, rolesField);
		addField(form, enabledField);

		if (state != WindowState.MULTIUPDATE) {
			addField(form, birthDateField);
		}
		
		addField(form, genderField);
		addField(form, educationField);
		addField(form, noteField);

		Panel panel = new Panel(form);
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

		layout.addComponent(panel);
		layout.setExpandRatio(panel, 1f);
		return layout;
	}

	// private Component buildUserGroupsTab() {
	// VerticalLayout tab = new VerticalLayout();
	// tab.setCaption(Messages.getString("Caption.Tab.UserGroups"));
	// tab.setIcon(FontAwesome.GROUP);
	// tab.setSpacing(true);
	// tab.setMargin(true);
	// tab.setSizeFull();
	//
	// Panel panel = new Panel();
	// panel.setSizeFull();
	// panel.addStyleName("borderless");
	// panel.setContent(buildUserGroupsForm());
	// tab.addComponent(panel);
	//
	// return tab;
	// }

	private Component buildUserGroupsForm() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);

		if (groupsField != null) {
			Panel panel = new Panel();
			panel.setSizeFull();
			panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

			if (state.equals(WindowState.MULTIUPDATE)) {
				addInformationLabel(layout);

				GridLayout form = new GridLayout();
				form.setColumns(2);
				// form.setMargin(true);
				form.setSpacing(true);
				form.setSizeUndefined();

				addField(form, groupsField);
				panel.setContent(form);

			} else {
				panel.setContent(groupsField);
			}

			layout.addComponent(panel);
			layout.setExpandRatio(panel, 1f);

		} else {
			Label label = new Label(Messages.getString("Caption.Item.UserNoGroups"));
			layout.addComponent(label);
			layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		}

		return layout;
	}

	// private Component buildUserTestsTab() {
	// VerticalLayout tab = new VerticalLayout();
	// tab.setCaption(Messages.getString("Caption.Tab.UserPacks"));
	// tab.setIcon(FontAwesome.COG);
	// tab.setSpacing(true);
	// tab.setMargin(true);
	// tab.setSizeFull();
	//
	// Panel panel = new Panel();
	// panel.setSizeFull();
	// //panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
	// panel.setContent(buildUserTestsForm());
	// tab.addComponent(panel);
	//
	// return tab;
	// }

	private Component buildUserPacksForm() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);

		Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

		if (state.equals(WindowState.MULTIUPDATE)) {
			addInformationLabel(layout);

			GridLayout form = new GridLayout();
			form.setColumns(2);
			// form.setMargin(true);
			form.setSpacing(true);
			form.setSizeUndefined();

			addField(form, packsField);
			panel.setContent(form);

		} else {
			panel.setContent(packsField);
		}

		layout.addComponent(panel);
		layout.setExpandRatio(panel, 1f);

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
				commitFormWithMessage();
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

	protected void commitFormWithMessage() {
		try {
			commitForm();

			if (committed) {
				Notification success = null;
				if (state.equals(WindowState.CREATE)) {
					success = new Notification(Messages.getString("Message.Info.UserAdded"));
				} else if (state.equals(WindowState.UPDATE)) {
					success = new Notification(Messages.getString("Message.Info.UserUpdated"));
				} else {
					success = new Notification(Messages.getString("Message.Info.UsersUpdated"));
				}
				success.setDelayMsec(2000);
				success.setPosition(Position.BOTTOM_CENTER);
				success.show(Page.getCurrent());

				window.close();
			}

		} catch (CommitException e) {
			Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
		}
	}

	@SuppressWarnings("unchecked")
	protected void commitForm() throws CommitException {
		committed = false;

		for (AbstractField<?> field : fields) {
			try {
				if (field.isEnabled()) {
					field.validate();
				}
			} catch (InvalidValueException e) {
				field.focus();
				setValidationVisible(true);
				// tabSheet.setSelectedTab(detailsTab);
				throw new CommitException(e.getMessage());
			}
		}

		if (WindowState.CREATE == state) {
			final User oldUser = userService.findByUsernamePassword(usernameField.getValue(), passwordField.getValue());
			if (oldUser != null) {
				ConfirmDialog.show(UI.getCurrent(), Messages.getString("Caption.Dialog.ConfirmReplace"),
						Messages.getString("Caption.Confirm.User.OverwriteExisting"),
						Messages.getString("Caption.Button.Confirm"), Messages.getString("Caption.Button.Cancel"),
						new Listener() {
							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									state = WindowState.UPDATE;
									source = oldUser;
									loggedUser = SessionManager.getLoggedUser();

									commitFormWithMessage();
								}
							}
						});

				return;
			}
		}

		if (WindowState.MULTIUPDATE == state) {
			for (User user : (Collection<User>) source) {
				user = saveUser(user);
				if (user != null) {
					bus.post(new MainUIEvent.UserAddedEvent(user));
				}
			}

		} else {
			User user;
			if (state.equals(WindowState.CREATE)) {
				user = new User();
			} else {
				user = (User) source;
			}
			user = saveUser(user);
			if (user != null) {
				bus.post(new MainUIEvent.UserAddedEvent(user));
			}
		}

		committed = true;
	}

	@SuppressWarnings("unchecked")
	private User saveUser(User user) {
		boolean savingLoggedUser = user.equals(loggedUser);

		if (state.equals(WindowState.CREATE)) {
			user.setOwnerId(loggedUser.getId());
		}

		if (!(state.equals(WindowState.MULTIUPDATE))) {
			user.setUsername(usernameField.getValue());
			user.setPassword(passwordField.getValue());
		}

		if (nameField.isVisible()) {
			user.setName(nameField.getValue());
		}

		if (rolesField.isVisible()) {
			Set<Role> roles = new HashSet<>();
			for (Role role : user.getRoles()) {
				roles.add(role);
			}
			for (Role role : roles) {
				user.removeRole(role);
			}
			roles = (Set<Role>) rolesField.getValue();
			for (Role role : roles) {
				user.addRole(role);
			}
		}

		if (enabledField.isVisible()) {
			user.setEnabled(enabledField.getValue());
		}

		// if (expireDateField.isVisible()) {
		// user.setExpireDate(expireDateField.getValue());
		// }

		if (noteField.isVisible()) {
			user.setNote(noteField.getValue());
		}
		
		if (genderField.isVisible()) {
			if (genderField.getValue() != null) {
				user.setGender(((Gender) genderField.getValue()).getCode());
			} else {
				user.setGender(null);
			}
		}
		
		if (educationField.isVisible()) {
			user.setEducation(educationField.getValue());
		}
		
		if (birthDateField.isVisible()) {
			user.setBirthDate(birthDateField.getValue());
		}

		if (groupsField != null && groupsField.isVisible() && groupsField.isEnabled()) {
			for (Object itemId : groupsField.getItemIds()) {
				Item item = groupsField.getItem(itemId);
				Group group = groupService.merge((Group) itemId);
				Boolean selected = (Boolean) item.getItemProperty(FieldConstants.SELECTED).getValue();

				if (selected == null) {
					if (state.equals(WindowState.MULTIUPDATE)) {
						user.removeGroup(group);
					}
				} else if (selected.equals(true)) {
					user.addGroup(group);
				} else if (selected.equals(false)) {
					user.removeGroup(group);
				}

				if (group != null) {
					bus.post(new MainUIEvent.GroupUsersChangedEvent(group));
				}
			}
		}

		user = userService.add(user);

		if (packsField.isVisible()) {
			permissionService.deleteUserPermissions(user);

			for (Object itemId : packsField.getItemIds()) {
				Item item = packsField.getItem(itemId);
				Pack pack = (Pack) itemId;
				Status state = (Status) item.getItemProperty(FieldConstants.TEST_STATE).getValue();

				if (state != null && state != Status.NONE && state != Status.ENABLED_INHERITED) {
					boolean enabled = state == Status.ENABLED || !(state == Status.DISABLED || state == Status.DISABLED_OVERRIDE);
					permissionService.addUserPermission(new UserPermission(user, pack, enabled));
				}
			}
		}

		if (savingLoggedUser && rolesField.isVisible()) {
			Set<Role> oldRoles = loggedUser.getRoles();
			Set<Role> newRoles = user.getRoles();

			SessionManager.setLoggedUser(user);

			if (!oldRoles.equals(newRoles)) {
				// Superuser/Manager -> User degradation
				if (!newRoles.contains(RoleService.ROLE_MANAGER) && !newRoles.contains(RoleService.ROLE_SUPERUSER)) {
					bus.post(new MainUIEvent.UserLoggedOutEvent());

					// Superuser -> Manager degradation
				} else if (oldRoles.contains(RoleService.ROLE_SUPERUSER)
						&& !newRoles.contains(RoleService.ROLE_SUPERUSER)) {
					bus.post(new MainUIEvent.ProfileUpdatedEvent());
					// TODO: zmena vypisu skupin
				}
			}

			bus.post(new MainUIEvent.UserPacksChangedEvent(user));
		}

		return user;
	}

	private void setValidationVisible(boolean visible) {
		idField.setValidationVisible(visible);
		usernameField.setValidationVisible(visible);
		nameField.setValidationVisible(visible);
		passwordField.setValidationVisible(visible);
		rolesField.setValidationVisible(visible);
		enabledField.setValidationVisible(visible);
		// expireDateField.setValidationVisible(visible);
		noteField.setValidationVisible(visible);
		if (groupsField != null) {
			groupsField.setValidationVisible(visible);
		}
		packsField.setValidationVisible(visible);
	}

	public void showWindow(User user) {
		showWindow(WindowState.UPDATE, user);
	}

	public void showWindow(Collection<User> users) {
		showWindow(WindowState.MULTIUPDATE, users);
	}

}
