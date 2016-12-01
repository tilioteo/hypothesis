/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.cdi.NormalUIScoped;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.interfaces.GroupService;
import org.hypothesis.data.interfaces.PermissionService;
import org.hypothesis.data.interfaces.RoleService;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.*;
import org.hypothesis.data.service.RoleServiceImpl;
import org.hypothesis.data.validator.RoleValidator;
import org.hypothesis.data.validator.UsernameValidator;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.interfaces.UserWindowPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.table.CheckTable;
import org.hypothesis.ui.table.DoubleCheckerColumnGenerator;
import org.hypothesis.ui.table.SimpleCheckerColumnGenerator;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
@NormalUIScoped
public class UserWindowPresenterImpl extends AbstractWindowPresenter implements UserWindowPresenter {

	@Inject
	private GroupService groupService;
	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;
	@Inject
	private PermissionService permissionService;

	@Inject
	private Event<MainUIEvent> mainEvent;

	private TextField idField;
	private TextField usernameField;
	private TextField generatedGroupField;
	private TextField generatedCountField;
	private TextField passwordField;
	private OptionGroup rolesField;
	private CheckBox enabledField;
	private PopupDateField expireDateField;
	private TextField noteField;
	private Table groupsField;
	private Table packsField;

	private Boolean generateNames = false;

	private void buildIdField() {
		if (idField == null) {
			idField = new TextField(Messages.getString("Caption.Field.Id"));
			idField.setEnabled(false);
		}
	}

	private void buildGeneratedGroupField() {
		if (generatedGroupField == null) {
			generatedGroupField = new TextField();
			generatedGroupField.setMaxLength(30);
			generatedGroupField.setNullRepresentation("");
			generatedGroupField.setRequired(true);
			generatedGroupField.setRequiredError(Messages.getString("Message.Error.GeneratedGroupRequired"));
			generatedGroupField.addValidator(new StringLengthValidator(
					Messages.getString("Message.Error.GeneratedGroupLength", 4, 30), 4, 30, false));
		}
	}

	private void buildGeneratedCountField() {
		if (generatedCountField == null) {
			generatedCountField = new TextField();
			generatedCountField.setConverter(new StringToIntegerConverter());
			generatedCountField.setConversionError(Messages.getString("Message.Error.GeneratedCountInteger", 1, 999));
			generatedCountField.setMaxLength(3);
			generatedCountField.setNullRepresentation("");
			generatedCountField.setWidth(3, Unit.EM);
			generatedCountField.setRequired(true);
			generatedCountField.setRequiredError(Messages.getString("Message.Error.GeneratedCountRequired"));
			generatedCountField.addValidator(new IntegerRangeValidator(
					Messages.getString("Message.Error.GeneratedCountInteger", 1, 999), 1, 999));
		}
	}

	private void buildUsernameField() {
		if (usernameField == null) {
			usernameField = new TextField(Messages.getString("Caption.Field.Username"));
			usernameField.setNullRepresentation("");
			usernameField.setMaxLength(30);
			usernameField.setRequired(true);
			usernameField.setRequiredError(Messages.getString("Message.Error.UsernameRequired"));
			usernameField.addValidator(
					new StringLengthValidator(Messages.getString("Message.Error.UsernameLength", 4, 30), 4, 30, false));
		}
	}

	private void buildPasswordField() {
		if (passwordField == null) {
			passwordField = new TextField(Messages.getString("Caption.Field.Password"));
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
			rolesField.addStyleName("horizontal");
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

	private void buildExpireDateField() {
		if (expireDateField == null) {
			expireDateField = new PopupDateField(Messages.getString("Caption.Field.ExpireDate"));
			expireDateField.setResolution(Resolution.DAY);
			expireDateField.setDateFormat(Messages.getString("Format.Date"));
		}
	}

	private void buildNoteField() {
		if (noteField == null) {
			noteField = new TextField(Messages.getString("Caption.Field.Note"));
			noteField.setNullRepresentation("");
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

			table.addGeneratedColumn(FieldConstants.ENABLER, new SimpleCheckerColumnGenerator(FieldConstants.SELECTED));

			table.setItemDescriptionGenerator((source, itemId, propertyId) -> {
                Group group = (Group) itemId;
                return Messages.getString("Caption.Item.GroupDescription", group.getName(), group.getId());
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
			table.setSelectable(false);
			table.addStyleName(ValoTheme.TABLE_SMALL);
			table.addStyleName(ValoTheme.TABLE_NO_HEADER);
			table.addStyleName(ValoTheme.TABLE_BORDERLESS);
			table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
			table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
			table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
			table.addStyleName(ValoTheme.TABLE_COMPACT);

			table.addContainerProperty(FieldConstants.NAME, String.class, null);
			table.addContainerProperty(FieldConstants.TEST_STATE, Boolean.class, null);

			table.addGeneratedColumn(FieldConstants.TEST_ENABLER,
					new DoubleCheckerColumnGenerator(FieldConstants.TEST_STATE));

			table.setItemDescriptionGenerator((source, itemId, propertyId) -> {
                Pack pack = (Pack) itemId;
                return Messages.getString("Caption.Item.PackDescription", pack.getName(), pack.getId(),
                        pack.getDescription());
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
		if (state == WindowState.CREATE) {
			usernameField.addValidator(new UsernameValidator(null));
		} else if (state == WindowState.UPDATE) {
			usernameField.addValidator(new UsernameValidator(((User) source).getId()));
		}

		// username generator
		buildGeneratedGroupField();
		buildGeneratedCountField();

		// password
		buildPasswordField();

		// roles
		buildRolesField();

		BeanItemContainer<Role> rolesSource = (BeanItemContainer<Role>) rolesField.getContainerDataSource();
		rolesSource.addAll(roleService.findAll());
		rolesSource.sort(new Object[] { FieldConstants.ID }, new boolean[] { true });

		if (!loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			rolesField.select(RoleServiceImpl.ROLE_USER);
			rolesField.setEnabled(false);
		} else if (state != WindowState.CREATE) {
			rolesField.setRequired(true);
			rolesField.setRequiredError(Messages.getString("Message.Error.RoleRequired"));
			rolesField.addValidator(new RoleValidator(source, loggedUser));
		}

		// enabled
		buildEnabledField();

		// expire date
		buildExpireDateField();

		// note
		buildNoteField();

		// groups
		Collection<Group> groups;

		if (loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			groups = groupService.findAll();
		} else {
			groups = groupService.findOwnerGroups(loggedUser);
		}

		if (!groups.isEmpty()) {
			buildGroupsField(!loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER));

			groups.forEach(e -> {
				groupsField.addItem(e);
				Item row = groupsField.getItem(e);
				row.getItemProperty(FieldConstants.NAME).setValue(e.getName());
			});

			((IndexedContainer) groupsField.getContainerDataSource()).setItemSorter(new CaseInsensitiveItemSorter());
			groupsField.sort(new Object[] { FieldConstants.NAME }, new boolean[] { true });
		}

		// packs
		buildPacksField();

		Collection<Pack> packs;
		if (loggedUser.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			packs = permissionService.findAllPacks();
		} else {
			packs = permissionService.findUserPacks2(loggedUser, false);
		}

		// TODO: upozornit, pokud nema uzivatel pristupne zadne packy?

		packs.forEach(e -> {
			packsField.addItem(e);
			Item row = packsField.getItem(e);
			row.getItemProperty(FieldConstants.NAME).setValue(e.getName());
		});

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
		passwordField.setValue(user.getPassword());
		rolesField.setValue(user.getRoles());
		enabledField.setValue(user.getEnabled());
		expireDateField.setValue(user.getExpireDate());
		noteField.setValue(user.getNote());

		// groups
		if (groupsField != null) {
			Set<Group> groups;

			if (state == WindowState.UPDATE) {
				groups = user.getGroups();
			} else {
				groups = new HashSet<>();
			}

			groupsField.getItemIds().forEach(e -> {
				Item row = groupsField.getItem(e);
				Group group = groupService.merge((Group) e);

				if (groups.contains(group)) {
					row.getItemProperty(FieldConstants.SELECTED).setValue(true);
				} else {
					row.getItemProperty(FieldConstants.SELECTED).setValue(false);
				}
			});
		}

		// packs
		Set<Pack> enabledPacks;
		Set<Pack> disabledPacks;

		if (state == WindowState.UPDATE) {
			enabledPacks = permissionService.getUserPacks(user, true, null);
			disabledPacks = permissionService.getUserPacks(user, false, null);
		} else {
			enabledPacks = new HashSet<>();
			disabledPacks = new HashSet<>();
		}

		packsField.getItemIds().forEach(e -> {
			Item row = packsField.getItem(e);

			if (enabledPacks.contains(e)) {
				row.getItemProperty(FieldConstants.TEST_STATE).setValue(true);
			} else if (disabledPacks.contains(e)) {
				row.getItemProperty(FieldConstants.TEST_STATE).setValue(false);
			} else {
				row.getItemProperty(FieldConstants.TEST_STATE).setValue(null);
			}
		});
	}

	@Override
	protected void clearFields() {
		fields.clear();

		idField = null;
		usernameField = null;
		generatedGroupField = null;
		generatedCountField = null;
		passwordField = null;
		rolesField = null;
		enabledField = null;
		expireDateField = null;
		noteField = null;
		groupsField = null;
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

		tabSheet.addComponent(buildUserDetailsTab());
		tabSheet.addComponent(buildUserGroupsTab());
		tabSheet.addComponent(buildUserTestsTab());

		content.addComponent(buildFooter());

		setValidationVisible(false);
	}

	private Component buildUserDetailsTab() {
		VerticalLayout tab = new VerticalLayout();
		tab.setCaption(Messages.getString("Caption.Tab.UserDetails"));
		tab.setIcon(FontAwesome.USER);
		tab.setSpacing(true);
		tab.setMargin(true);
		tab.setSizeFull();

		Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.setContent(buildUserDetailsForm());
		tab.addComponent(panel);

		detailsTab = tab;

		return tab;
	}

	private Component buildUserDetailsForm() {
		VerticalLayout layout = new VerticalLayout();

		if (state == WindowState.MULTIUPDATE) {
			addInformationLabel(layout);
		}

		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		// ID
		if (state == WindowState.UPDATE) {
			addField(form, idField);
		}

		// username
		if (state == WindowState.CREATE) {
			final VerticalLayout nameLayout = new VerticalLayout();
			nameLayout.setCaption(usernameField.getCaption());
			nameLayout.setSpacing(true);

			final HorizontalLayout generatedNameLayout = new HorizontalLayout();
			generatedNameLayout.addComponent(generatedGroupField);
			generatedNameLayout.addComponent(new Label("-"));
			generatedNameLayout.addComponent(generatedCountField);
			generatedNameLayout.addComponent(new Label("-XXXX"));
			generatedNameLayout.setVisible(false);
			addField(form, generatedNameLayout);

			final CheckBox nameFieldSwitch = new CheckBox(Messages.getString("Caption.Button.GenerateName"));
			nameFieldSwitch.addValueChangeListener(e -> {
                boolean generate = nameFieldSwitch.getValue();
                generatedNameLayout.setVisible(generate);
                usernameField.setVisible(!generate);
                passwordField.setEnabled(!generate);
                generateNames = generate;

                if (generate) {
                    fields.add(0, generatedCountField);
                    fields.add(0, generatedGroupField);
                    fields.remove(usernameField);
                } else {
                    fields.remove(generatedGroupField);
                    fields.remove(generatedCountField);
                    fields.add(0, usernameField);
                }
            });

			nameLayout.addComponent(usernameField);
			nameLayout.addComponent(generatedNameLayout);
			nameLayout.addComponent(nameFieldSwitch);

			addField(form, nameLayout);
			fields.add(usernameField);
			usernameField.setCaption(null);

		} else if (state == WindowState.UPDATE) {
			addField(form, usernameField);
		}

		if (state != WindowState.MULTIUPDATE) {
			addField(form, passwordField);
		}

		addField(form, rolesField);
		addField(form, enabledField);
		addField(form, expireDateField);
		addField(form, noteField);

		layout.addComponent(form);
		return layout;
	}

	private Component buildUserGroupsTab() {
		VerticalLayout tab = new VerticalLayout();
		tab.setCaption(Messages.getString("Caption.Tab.UserGroups"));
		tab.setIcon(FontAwesome.GROUP);
		tab.setSpacing(true);
		tab.setMargin(true);
		tab.setSizeFull();

		Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName("borderless");
		panel.setContent(buildUserGroupsForm());
		tab.addComponent(panel);

		return tab;
	}

	private Component buildUserGroupsForm() {
		VerticalLayout layout = new VerticalLayout();

		if (groupsField != null) {
			if (state == WindowState.MULTIUPDATE) {
				addInformationLabel(layout);
			}

			GridLayout form = new GridLayout();
			form.setColumns(2);
			form.setMargin(true);
			form.setSpacing(true);

			addField(form, groupsField);

			layout.addComponent(form);

		} else {
			Label label = new Label(Messages.getString("Caption.Item.UserNoGroups"));
			layout.addComponent(label);
			layout.setSizeFull();
			layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		}

		return layout;
	}

	private Component buildUserTestsTab() {
		VerticalLayout tab = new VerticalLayout();
		tab.setCaption(Messages.getString("Caption.Tab.UserPacks"));
		tab.setIcon(FontAwesome.COG);
		tab.setSpacing(true);
		tab.setMargin(true);
		tab.setSizeFull();

		Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName("borderless");
		panel.setContent(buildUserTestsForm());
		tab.addComponent(panel);

		return tab;
	}

	private Component buildUserTestsForm() {
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
                    success = new Notification(Messages.getString("Message.Info.UserAdded"));
                } else if (state == WindowState.UPDATE) {
                    success = new Notification(Messages.getString("Message.Info.UserUpdated"));
                } else {
                    success = new Notification(Messages.getString("Message.Info.UsersUpdated"));
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
			((Collection<User>) source).forEach(e -> {
				User user = saveUser(e, true);
				if (user != null) {
					mainEvent.fire(new MainUIEvent.UserAddedEvent(user));
				}
			});

		} else if (generateNames) {
			String usernameGroup = generatedGroupField.getValue();
			int count = Integer.parseInt(generatedCountField.getValue());

			for (int i = 1; i <= count; i++) {
				User user = new User();
				user.setUsername(
						String.format("%s-%03d-%s", usernameGroup, i, generateString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 4)));
				user.setPassword(generateString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 8));
				saveUser(user, false);
			}

		} else {
			User user;
			if (state == WindowState.CREATE) {
				user = new User();
			} else {
				user = (User) source;
			}
			user = saveUser(user, true);
			if (user != null) {
				mainEvent.fire(new MainUIEvent.UserAddedEvent(user));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private User saveUser(User user, boolean includeGenerableFields) {
		boolean savingLoggedUser = user.equals(loggedUser);

		if (state == WindowState.CREATE) {
			user.setOwnerId(loggedUser.getId());
		}

		if (includeGenerableFields) {
			if (state != WindowState.MULTIUPDATE) {
				user.setUsername(usernameField.getValue());
				user.setPassword(passwordField.getValue());
			}
		}

		if (rolesField.isVisible()) {
			Set<Role> roles = user.getRoles().stream().collect(Collectors.toSet());
			roles.forEach(user::removeRole);
			((Set<Role>) rolesField.getValue()).forEach(user::addRole);
		}

		if (enabledField.isVisible()) {
			user.setEnabled(enabledField.getValue());
		}

		if (expireDateField.isVisible()) {
			user.setExpireDate(expireDateField.getValue());
		}

		if (noteField.isVisible()) {
			user.setNote(noteField.getValue());
		}

		final User finalUser = user;
		if (groupsField != null && groupsField.isVisible() && groupsField.isEnabled()) {
			groupsField.getItemIds().forEach(e -> {
				Item item = groupsField.getItem(e);
				Group group = groupService.merge((Group) e);
				Boolean selected = (Boolean) item.getItemProperty(FieldConstants.SELECTED).getValue();

				if (selected == null) {
					if (state == WindowState.MULTIUPDATE) {
						finalUser.removeGroup(group);
					}
				} else if (selected.equals(true)) {
					finalUser.addGroup(group);
				} else if (selected.equals(false)) {
					finalUser.removeGroup(group);
				}

				if (group != null) {
					mainEvent.fire(new MainUIEvent.GroupUsersChangedEvent(group));
				}
			});
		}

		user = userService.add(user);

		final User addedUser = user;

		if (packsField.isVisible()) {
			permissionService.deleteUserPermissions(user);

			packsField.getItemIds().forEach(e -> {
				Item item = packsField.getItem(e);
				Pack pack = (Pack) e;
				Boolean testState = (Boolean) item.getItemProperty(FieldConstants.TEST_STATE).getValue();

				if (testState != null) {
					permissionService.addUserPermission(new UserPermission(addedUser, pack, testState));
				}
			});
		}

		if (savingLoggedUser && rolesField.isVisible()) {
			Set<Role> oldRoles = loggedUser.getRoles();
			Set<Role> newRoles = user.getRoles();

			SessionManager.setLoggedUser(user);

			if (!oldRoles.equals(newRoles)) {
				// Superuser/Manager -> User degradation
				if (!newRoles.contains(RoleServiceImpl.ROLE_MANAGER)
						&& !newRoles.contains(RoleServiceImpl.ROLE_SUPERUSER)) {
					mainEvent.fire(new MainUIEvent.UserLoggedOutEvent());

					// Superuser -> Manager degradation
				} else if (oldRoles.contains(RoleServiceImpl.ROLE_SUPERUSER)
						&& !newRoles.contains(RoleServiceImpl.ROLE_SUPERUSER)) {
					mainEvent.fire(new MainUIEvent.ProfileUpdatedEvent());
					// TODO: zmena vypisu skupin
				}
			}

			mainEvent.fire(new MainUIEvent.UserPacksChangedEvent(user));
		}

		return user;
	}

	private void setValidationVisible(boolean visible) {
		idField.setValidationVisible(visible);
		usernameField.setValidationVisible(visible);
		generatedGroupField.setValidationVisible(visible);
		generatedCountField.setValidationVisible(visible);
		passwordField.setValidationVisible(visible);
		rolesField.setValidationVisible(visible);
		enabledField.setValidationVisible(visible);
		expireDateField.setValidationVisible(visible);
		noteField.setValidationVisible(visible);
		if (groupsField != null) {
			groupsField.setValidationVisible(visible);
		}
		packsField.setValidationVisible(visible);
	}

	private String generateString(String characters, int length) {
		Random rng = new Random();
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.presenter.UserWindowPresenter#showWindow(org.hypothesis.
	 * data.model.User)
	 */
	@Override
	public void showWindow(User user) {
		showWindow(WindowState.UPDATE, user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.presenter.UserWindowPresenter#showWindow(java.util.
	 * Collection)
	 */
	@Override
	public void showWindow(Collection<User> users) {
		showWindow(WindowState.MULTIUPDATE, users);
	}

}
