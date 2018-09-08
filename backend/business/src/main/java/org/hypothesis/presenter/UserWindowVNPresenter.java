package org.hypothesis.presenter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.LongItemSorter;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Gender;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.PackSet;
import org.hypothesis.data.model.Role;
import org.hypothesis.data.model.User;
import org.hypothesis.data.model.UserPermission;
import org.hypothesis.data.service.PackSetService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.validator.RoleValidator;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;
import org.hypothesis.utility.BirthNumberUtility;
import org.hypothesis.utility.DateUtility;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.AbstractSelect.AcceptItem;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.TableDragMode;
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

	// private final GroupService groupService;
	private final UserService userService;
	private final RoleService roleService;
	private final PermissionService permissionService;
	private final PackSetService packSetService;

	private TextField idField;
	private TextField usernameField;
	private TextField nameField;
	private TextField passwordField;
	private TextField educationField;
	private ComboBox genderField;
	private DateField birthDateField;
	private DateField testingDateField;
	private OptionGroup rolesField;
	private CheckBox enabledField;
	private CheckBox autoDisableField;
	private TextField noteField;
	// private Table groupsField;
	// private Table packsField;

	private Table permittedPacks;

	private boolean committed = false;

	private boolean isFirstRoleSelected;

	public UserWindowVNPresenter(MainEventBus bus) {
		super(bus);

		// groupService = GroupService.newInstance();
		userService = UserService.newInstance();
		roleService = RoleService.newInstance();
		permissionService = PermissionService.newInstance();
		packSetService = PackSetService.newInstance();
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
			passwordField.setRequiredError(Messages.getString("Message.Error.PasswordRequired"));
			passwordField.addValidator(
					new StringLengthValidator(Messages.getString("Message.Error.PasswordLength", 4, 30), 4, 30, false));

			passwordField.addBlurListener(new BlurListener() {
				@Override
				public void blur(BlurEvent event) {
					if (WindowState.CREATE == state && StringUtils.isNotBlank(passwordField.getValue())) {
						processPeronalNumber(StringUtils.trim(passwordField.getValue()));
					}
				}
			});
		}
	}

	private void processPeronalNumber(String value) {
		List<User> oldUsers = userService.findByPasswordAkaBirthNumber(value);
		if (!oldUsers.isEmpty()) {
			if (oldUsers.size() == 1) {
				source = oldUsers.get(0);
				state = WindowState.UPDATE;
				fillFields();
				Notification.show(Messages.getString("Message.Info.PersonLoaded"));
			} else {
				Notification.show(Messages.getString("Message.Warning.MorePersons", value), Type.WARNING_MESSAGE);
			}
		} else {
			if (!BirthNumberUtility.isValid(value)) {
				Notification.show(Messages.getString("Message.Warning.InvalidBirthNumber", value),
						Type.WARNING_MESSAGE);
			}
			LocalDate birthDate = BirthNumberUtility.parseDate(value);
			if (birthDate == null) {
				Notification.show(Messages.getString("Message.Warning.CannotGetDateOfBirth"), Type.WARNING_MESSAGE);
			} else {
				birthDateField.setValue(DateUtility.toDate(birthDate));
				switch (BirthNumberUtility.getSexChar(value)) {
				case 'M':
					genderField.setValue(Gender.MALE);
					break;
				case 'F':
					genderField.setValue(Gender.FEMALE);
					break;
				default:
					Notification.show(Messages.getString("Message.Warning.CannotGetGender"), Type.WARNING_MESSAGE);
					break;
				}
			}

		}
	}

	private void buildRolesField() {
		if (rolesField == null) {
			rolesField = new OptionGroup(Messages.getString("Caption.Field.Role"));
			rolesField.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
			rolesField.setItemCaptionPropertyId(FieldConstants.NAME);
			rolesField.setMultiSelect(true);
			rolesField.addValueChangeListener(new ValueChangeListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if (WindowState.CREATE == state && !isFirstRoleSelected) {
						Set<Role> roles = (Set<Role>) event.getProperty().getValue();
						if (roles.size() == 1 && roles.contains(RoleService.ROLE_USER)) {
							isFirstRoleSelected = true;
							autoDisableField.setValue(true);
						}
					}
				}
			});

			BeanItemContainer<Role> dataSource = new BeanItemContainer<Role>(Role.class);
			rolesField.setContainerDataSource(dataSource);
		}
	}

	private void buildEnabledField() {
		if (enabledField == null) {
			enabledField = new CheckBox(Messages.getString("Caption.Field.Enabled"));
			enabledField.setValue(true);
		}
	}

	private void buildAutoDisableField() {
		if (autoDisableField == null) {
			autoDisableField = new CheckBox(Messages.getString("Caption.Field.AutoDisable"));
		}
	}

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
			genderField.setRequired(true);
			genderField.setRequiredError(Messages.getString("Message.Error.SelectGender"));

			genderField.addItem(Gender.MALE);
			genderField.addItem(Gender.FEMALE);
			genderField.setItemCaption(Gender.MALE, Messages.getString(Gender.MALE.getMessageCode()));
			genderField.setItemCaption(Gender.FEMALE, Messages.getString(Gender.FEMALE.getMessageCode()));
		}
	}

	private void buildBirthDateField() {
		if (birthDateField == null) {
			birthDateField = new DateField(Messages.getString("Caption.Field.DateOfBirth"));
			birthDateField.setDateFormat(Messages.getString("Format.Date"));
		}
	}

	private void buildTestingDateField() {
		if (testingDateField == null) {
			testingDateField = new DateField(Messages.getString("Caption.Field.DateOfTesting"));
			testingDateField.setDateFormat(Messages.getString("Format.Date"));
		}
	}

	// private void buildGroupsField(boolean required) {
	// if (groupsField == null) {
	// final Table table = new
	// CheckTable(Messages.getString("Caption.Field.Groups"));
	// table.setSelectable(false);
	// table.addStyleName(ValoTheme.TABLE_SMALL);
	// table.addStyleName(ValoTheme.TABLE_NO_HEADER);
	// table.addStyleName(ValoTheme.TABLE_BORDERLESS);
	// table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
	// table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
	// table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
	// table.addStyleName(ValoTheme.TABLE_COMPACT);
	//
	// table.addContainerProperty(FieldConstants.NAME, String.class, null);
	// table.addContainerProperty(FieldConstants.SELECTED, Boolean.class, null);
	//
	// table.addGeneratedColumn(FieldConstants.ENABLER, new
	// AbstractSimpleCheckerColumnGenerator(
	// FieldConstants.SELECTED, Messages.getString("Caption.Button.EnablePack"))
	// {
	//
	// @Override
	// public void onStateChanged(Object itemId, boolean checked) {
	// _updatePacksByGroup((Group) itemId, checked);
	// }
	// });
	//
	// table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
	// @Override
	// public String generateDescription(Component source, Object itemId, Object
	// propertyId) {
	// Group group = (Group) itemId;
	// return Messages.getString("Caption.Item.GroupDescription",
	// group.getName(), group.getId());
	// }
	// });
	//
	// table.setVisibleColumns(FieldConstants.ENABLER, FieldConstants.NAME);
	// table.setColumnHeaders(Messages.getString("Caption.Field.State"),
	// Messages.getString("Caption.Field.Name"));
	//
	// table.setPageLength(table.size());
	//
	// groupsField = table;
	//
	// // TODO: vymyslet validator na kontrolu vyberu
	// // (nevybiram primo v tabulce), vyhnout se CheckTable
	// // (je prilis zjednodsena a nedoladena)
	// if (required) {
	// groupsField.setRequired(true);
	// groupsField.setRequiredError(Messages.getString("Message.Error.GroupRequired"));
	// }
	// }
	// }

	// @SuppressWarnings("unchecked")
	// private void _updatePacksByGroup(Group group, boolean checked) {
	// Map<Object, DoubleCheckerColumnGenerator.ButtonsHolder> map =
	// (Map<Object, ButtonsHolder>) packsField.getData();
	// if (map != null) {
	// Set<Pack> enabledPacks;
	// Set<Pack> disabledPacks;
	// Set<Pack> groupPacks = new HashSet<>();
	// User user = (User) source;
	// if (WindowState.UPDATE == state) {
	// enabledPacks = permissionService.getUserPacks(user, true, null);
	// disabledPacks = permissionService.getUserPacks(user, false, null);
	// } else {
	// enabledPacks = new HashSet<>();
	// disabledPacks = new HashSet<>();
	// }
	//
	// Set<Group> groups = user != null ? user.getGroups() : new
	// HashSet<Group>();
	// if (checked) {
	// groups.add(group);
	// } else {
	// groups.remove(group);
	// }
	// if (WindowState.MULTIUPDATE != state && !groups.isEmpty()) {
	// for (GroupPermission groupPermission :
	// permissionService.getGroupsPermissions(groups)) {
	// groupPacks.add(groupPermission.getPack());
	// }
	// }
	//
	// for (Object itemId : packsField.getItemIds()) {
	// Item row = packsField.getItem(itemId);
	// Pack pack = (Pack) itemId;
	//
	// Status state = Status.NONE;
	//
	// if (groupPacks.contains(pack)) {
	// if (disabledPacks.contains(pack)) {
	// state = Status.DISABLED_OVERRIDE;
	// } else {
	// state = Status.ENABLED_INHERITED;
	// }
	// } else {
	// if (enabledPacks.contains(pack)) {
	// state = Status.ENABLED;
	// } else if (disabledPacks.contains(pack)) {
	// state = Status.DISABLED;
	// }
	// }
	//
	// row.getItemProperty(FieldConstants.TEST_STATE).setValue(state);
	// ButtonsHolder buttonsHolder = map.get(itemId);
	// DoubleCheckerColumnGenerator.setButtons(state,
	// buttonsHolder.enabledButton,
	// buttonsHolder.disabledButton);
	// }
	// }
	//
	// }

	// private void buildPacksField() {
	// if (packsField == null) {
	// final Table table = new Table(Messages.getString("Caption.Field.Packs"));
	// table.setSizeUndefined();
	// table.setSelectable(false);
	// table.addStyleName(ValoTheme.TABLE_SMALL);
	// table.addStyleName(ValoTheme.TABLE_NO_HEADER);
	// table.addStyleName(ValoTheme.TABLE_BORDERLESS);
	// table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
	// table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
	// table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
	// table.addStyleName(ValoTheme.TABLE_COMPACT);
	//
	// table.addContainerProperty(FieldConstants.NAME, String.class, null);
	// table.addContainerProperty(FieldConstants.TEST_STATE, Status.class,
	// null);
	//
	// table.addGeneratedColumn(FieldConstants.TEST_ENABLER,
	// new DoubleCheckerColumnGenerator(FieldConstants.TEST_STATE,
	// Messages.getString("Caption.Button.EnablePack"),
	// Messages.getString("Caption.Button.DisablePack")));
	//
	// table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
	// @Override
	// public String generateDescription(Component source, Object itemId, Object
	// propertyId) {
	// Pack pack = (Pack) itemId;
	// return Messages.getString("Caption.Item.PackDescription", pack.getName(),
	// pack.getId(),
	// pack.getDescription());
	// }
	// });
	//
	// table.setVisibleColumns(FieldConstants.TEST_ENABLER,
	// FieldConstants.NAME);
	// table.setColumnHeaders(Messages.getString("Caption.Field.State"),
	// Messages.getString("Caption.Field.Name"));
	//
	// table.setPageLength(table.size());
	//
	// packsField = table;
	// }
	// }

	@SuppressWarnings("unchecked")
	@Override
	protected void initFields() {
		isFirstRoleSelected = false;

		fields = new ArrayList<>();

		// ID
		buildIdField();

		// username
		buildUsernameField();

		// name
		buildNameField();

		// password
		buildPasswordField();

		// auto disable - must be created before roles field
		buildAutoDisableField();

		// roles
		buildRolesField();

		BeanItemContainer<Role> rolesSource = (BeanItemContainer<Role>) ((AbstractSelect) rolesField)
				.getContainerDataSource();
		rolesSource.addAll(roleService.findAll());
		rolesSource.sort(new Object[] { FieldConstants.ID }, new boolean[] { true });

		if (!loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			rolesField.select(RoleService.ROLE_USER);
			rolesField.setEnabled(false);
		} else if (WindowState.CREATE != state) {
			rolesField.setRequired(true);
			rolesField.setRequiredError(Messages.getString("Message.Error.RoleRequired"));
			rolesField.addValidator(new RoleValidator(source, loggedUser));
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
		
		// date of testing
		buildTestingDateField();

		if (WindowState.CREATE == state) {
			Set<Role> roles = new HashSet<>();
			roles.add(RoleService.ROLE_USER);
			rolesField.setValue(roles);
			
			testingDateField.setValue(DateUtility.toDate(LocalDate.now()));
		}

		// groups
		// Collection<Group> groups;
		//
		// if (loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
		// groups = groupService.findAll();
		// } else {
		// groups = groupService.findOwnerGroups(loggedUser);
		// }
		//
		// if (!groups.isEmpty()) {
		// buildGroupsField(!loggedUser.hasRole(RoleService.ROLE_SUPERUSER));
		//
		// for (Group group : groups) {
		// groupsField.addItem(group);
		// Item row = groupsField.getItem(group);
		// row.getItemProperty(FieldConstants.NAME).setValue(group.getName());
		// }
		//
		// ((IndexedContainer)
		// groupsField.getContainerDataSource()).setItemSorter(new
		// CaseInsensitiveItemSorter());
		// groupsField.sort(new Object[] { FieldConstants.NAME }, new boolean[]
		// { true });
		// }

		// packs
		// buildPacksField();
		//
		// Collection<Pack> packs;
		// if (loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
		// packs = permissionService.findAllPacks();
		// } else {
		// packs = permissionService.findUserPacks2(loggedUser, false);
		// }
		//
		// // TODO: upozornit, pokud nema uzivatel pristupne zadne packy?
		//
		// for (Pack pack : packs) {
		// packsField.addItem(pack);
		// Item row = packsField.getItem(pack);
		// row.getItemProperty(FieldConstants.NAME).setValue(pack.getName());
		// }
		//
		// ((IndexedContainer)
		// packsField.getContainerDataSource()).setItemSorter(new
		// CaseInsensitiveItemSorter());
		// packsField.sort(new Object[] { FieldConstants.NAME }, new boolean[] {
		// true });
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
		autoDisableField.setValue(user.getAutoDisable());
		noteField.setValue(user.getNote());
		genderField.select(Gender.get(user.getGender()));
		educationField.setValue(user.getEducation());
		birthDateField.setValue(user.getBirthDate());
		testingDateField.setValue(user.getTestingDate());

		// groups
		// if (groupsField != null) {
		// Set<Group> groups;
		//
		// if (WindowState.UPDATE == state) {
		// groups = user.getGroups();
		// } else {
		// groups = new HashSet<>();
		// }
		//
		// for (Object itemId : groupsField.getItemIds()) {
		// Item row = groupsField.getItem(itemId);
		// Group group = groupService.merge((Group) itemId);
		//
		// if (groups.contains(group)) {
		// row.getItemProperty(FieldConstants.SELECTED).setValue(true);
		// } else {
		// row.getItemProperty(FieldConstants.SELECTED).setValue(false);
		// }
		// }
		// }

		// packs
		// Set<Pack> enabledPacks;
		// Set<Pack> disabledPacks;
		// Set<Pack> groupPacks = new HashSet<>();
		//
		// if (WindowState.UPDATE == state) {
		// enabledPacks = permissionService.getUserPacks(user, true, null);
		// disabledPacks = permissionService.getUserPacks(user, false, null);
		// } else {
		// enabledPacks = new HashSet<>();
		// disabledPacks = new HashSet<>();
		// }
		//
		// if (WindowState.MULTIUPDATE != state && !user.getGroups().isEmpty())
		// {
		// for (GroupPermission groupPermission :
		// permissionService.getGroupsPermissions(user.getGroups())) {
		// groupPacks.add(groupPermission.getPack());
		// }
		// }
		//
		// for (Object itemId : packsField.getItemIds()) {
		// Item row = packsField.getItem(itemId);
		// Pack pack = (Pack) itemId;
		//
		// Status state = Status.NONE;
		//
		// if (groupPacks.contains(pack)) {
		// if (disabledPacks.contains(pack)) {
		// state = Status.DISABLED_OVERRIDE;
		// } else {
		// state = Status.ENABLED_INHERITED;
		// }
		// } else {
		// if (enabledPacks.contains(pack)) {
		// state = Status.ENABLED;
		// } else if (disabledPacks.contains(pack)) {
		// state = Status.DISABLED;
		// }
		// }
		//
		// row.getItemProperty(FieldConstants.TEST_STATE).setValue(state);
		// }
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
		autoDisableField = null;
		noteField = null;
		// groupsField = null;
		// packsField = null;
		genderField = null;
		educationField = null;
		birthDateField = null;
		testingDateField = null;
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

		content.addComponent(buildFooter());

		setValidationVisible(false);
	}

	private Component buildUserRelations() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		// layout.addComponent(buildUserGroups());
		layout.addComponent(buildPackSets());
		layout.addComponent(buildUserPacks2());

		return layout;
	}

	// private Component buildUserGroups() {
	// Panel panel = new Panel();
	// panel.setSizeFull();
	// panel.setCaption(Messages.getString("Caption.Tab.UserGroups"));
	// panel.setIcon(FontAwesome.GROUP);
	//
	// panel.setContent(buildUserGroupsForm());
	//
	// return panel;
	// }

	// private Component buildUserPacks() {
	// Panel panel = new Panel();
	// panel.setSizeFull();
	// panel.setCaption(Messages.getString("Caption.Tab.UserPacks"));
	// panel.setIcon(FontAwesome.COG);
	//
	// panel.setContent(buildUserPacksForm());
	//
	// return panel;
	// }

	private Component buildUserPacks2() {
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setCaption(Messages.getString("Caption.Tab.UserPacks"));
		panel.setIcon(FontAwesome.COG);

		panel.setContent(buildUserPacksForm2());

		return panel;
	}

	private Component buildUserPacksForm2() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setMargin(true);
		hl.setSpacing(true);

		final Button btnUp = new Button(FontAwesome.ARROW_UP);
		final Button btnDown = new Button(FontAwesome.ARROW_DOWN);
		final Button btnDelete = new Button(FontAwesome.TRASH);

		final Table table = new Table();
		table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.setSelectable(true);

		final BeanItemContainer<Pack> dataSource = new BeanItemContainer<Pack>(Pack.class);

		table.setContainerDataSource(dataSource);

		table.addGeneratedColumn(FieldConstants.ORDER, new ColumnGenerator() {
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				Container.Indexed container = (Container.Indexed) source.getContainerDataSource();
				return Integer.toString(container.indexOfId(itemId) + 1);
			}
		});

		table.setVisibleColumns(FieldConstants.ORDER, FieldConstants.NAME);
		table.setColumnHeaders(Messages.getString("Caption.Field.Order"), Messages.getString("Caption.Field.Name"));
		table.setSortEnabled(false);

		table.setDragMode(TableDragMode.ROW);

		table.setDropHandler(new DropHandler() {
			@Override
			public AcceptCriterion getAcceptCriterion() {
				return new And(new SourceIs(table), AcceptItem.ALL);
			}

			@Override
			public void drop(DragAndDropEvent event) {
				DataBoundTransferable t = (DataBoundTransferable) event.getTransferable();
				Pack sourceItemId = (Pack) t.getItemId();

				AbstractSelectTargetDetails dropData = (AbstractSelectTargetDetails) event.getTargetDetails();

				Pack targetItemId = (Pack) dropData.getItemIdOver();

				if (sourceItemId == targetItemId || targetItemId == null) {
					return;
				}

				dataSource.removeItem(sourceItemId);

				if (dropData.getDropLocation() == VerticalDropLocation.BOTTOM) {
					dataSource.addItemAfter(targetItemId, sourceItemId);
				} else {
					Object prevItemId = dataSource.prevItemId(targetItemId);
					dataSource.addItemAfter(prevItemId, sourceItemId);
				}
			}
		});

		hl.addComponent(table);
		hl.setExpandRatio(table, 1.0f);

		VerticalLayout buttonLayout = new VerticalLayout();
		buttonLayout.setWidthUndefined();
		buttonLayout.setSpacing(true);

		btnUp.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Pack pack = (Pack) table.getValue();
				if (pack != null) {
					List<Pack> list = dataSource.getItemIds();
					int idx = list.indexOf(pack);
					if (idx > 0) {
						dataSource.removeItem(pack);
						dataSource.addItemAt(idx - 1, pack);
					}
				}
			}
		});

		btnDown.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Pack pack = (Pack) table.getValue();
				if (pack != null) {
					List<Pack> list = dataSource.getItemIds();
					int idx = list.indexOf(pack);
					if (idx < list.size() - 1) {
						dataSource.removeItem(pack);
						dataSource.addItemAt(idx + 1, pack);
					}
				}
			}
		});

		btnDelete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Pack pack = (Pack) table.getValue();
				if (pack != null) {
					dataSource.removeItem(pack);
				}
			}
		});

		buttonLayout.addComponent(btnUp);
		buttonLayout.addComponent(btnDown);
		buttonLayout.addComponent(btnDelete);

		hl.addComponent(buttonLayout);

		permittedPacks = table;

		// fill table
		if (WindowState.UPDATE == state) {
			List<Pack> packs = permissionService.getUserPacksVN((User) source);
			for (Pack pack : packs) {
				dataSource.addBean(pack);
			}
		}

		return hl;
	}

	private Component buildPackSets() {
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setCaption(Messages.getString("Caption.Tab.PackSets"));
		panel.setIcon(FontAwesome.BOOK);

		panel.setContent(buildPacksForm());

		return panel;
	}

	private Component buildPacksForm() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.setSpacing(true);
		vl.setMargin(true);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setSpacing(true);

		Table table = new Table();
		table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.setSelectable(true);

		BeanItemContainer<PackSet> dataSource = new BeanItemContainer<PackSet>(PackSet.class);

		final BeanItemContainer<Pack> detailSource = new BeanItemContainer<Pack>(Pack.class);

		List<PackSet> packSets = packSetService.findAll();
		for (PackSet packSet : packSets) {
			packSet = packSetService.merge(packSet);
			dataSource.addBean(packSet);
		}
		table.setContainerDataSource(dataSource);
		dataSource.setItemSorter(new LongItemSorter());
		table.setSortEnabled(false);
		table.setSortContainerPropertyId(FieldConstants.ID);
		table.sort();

		table.setVisibleColumns(FieldConstants.NAME);
		table.setColumnHeaders(Messages.getString("Caption.Field.AvailablePackSets"));

		Table detailTable = new Table();
		detailTable.setSizeFull();
		detailTable.addStyleName(ValoTheme.TABLE_SMALL);

		detailTable.setContainerDataSource(detailSource);

		detailTable.setVisibleColumns(FieldConstants.NAME);
		detailTable.setColumnHeaders(Messages.getString("Caption.Field.PackSetContent"));
		detailTable.setSortEnabled(false);

		hl.addComponent(table);
		hl.addComponent(detailTable);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidth(100.0f, Unit.PERCENTAGE);
		buttonLayout.setSpacing(true);

		final Button btnAdd = new Button(Messages.getString("Caption.Field.AddPacks"), new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = table.getValue();
				if (itemId != null) {
					addSelectedPackSet((PackSet) itemId);
				}

			}
		});
		btnAdd.setEnabled(false);

		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				btnAdd.setEnabled(table.getValue() != null);

				updateDetailContainer(detailSource, (PackSet) table.getValue());
			}
		});

		buttonLayout.addComponent(btnAdd);

		vl.addComponent(hl);
		vl.addComponent(buttonLayout);

		vl.setExpandRatio(hl, 1.0f);

		return vl;
	}

	@SuppressWarnings("unchecked")
	private void addSelectedPackSet(PackSet packSet) {
		if (permittedPacks != null && permittedPacks.getContainerDataSource() != null) {
			BeanItemContainer<Pack> dataSource = (BeanItemContainer<Pack>) permittedPacks.getContainerDataSource();
			packSet = packSetService.merge(packSet);

			for (Pack pack : packSet.getPacks()) {
				if (!dataSource.containsId(pack)) {
					dataSource.addBean(pack);
				}
			}
		}
	}

	private void updateDetailContainer(BeanItemContainer<Pack> container, PackSet packSet) {
		if (container != null && packSet != null) {
			packSet = packSetService.merge(packSet);
			container.removeAllItems();
			for (Pack pack : packSet.getPacks()) {
				container.addBean(pack);
			}
		}
	}

	private Component buildUserDetail() {
		Panel panel = new Panel();
		panel.setSizeFull();

		panel.setCaption(Messages.getString("Caption.Tab.UserDetails"));
		panel.setIcon(FontAwesome.USER);

		panel.setContent(buildUserDetailsForm());

		return panel;
	}

	private Component buildUserDetailsForm() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		if (WindowState.MULTIUPDATE == state) {
			addInformationLabel(layout);
		}

		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		// ID
		if (WindowState.UPDATE == state) {
			addField(form, idField);
		}

		if (WindowState.MULTIUPDATE != state) {
			// password
			addField(form, passwordField);

			// username
			addField(form, usernameField);

			// name
			addField(form, nameField);
		}

		addField(form, rolesField);
		addField(form, enabledField);
		addField(form, autoDisableField);

		if (WindowState.MULTIUPDATE != state) {
			addField(form, birthDateField);
		}

		addField(form, genderField);
		addField(form, educationField);
		addField(form, testingDateField);
		addField(form, noteField);

		Panel panel = new Panel(form);
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

		layout.addComponent(panel);
		layout.setExpandRatio(panel, 1f);
		return layout;
	}

	// private Component buildUserGroupsForm() {
	// VerticalLayout layout = new VerticalLayout();
	// layout.setSizeFull();
	// layout.setMargin(true);
	//
	// if (groupsField != null) {
	// Panel panel = new Panel();
	// panel.setSizeFull();
	// panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
	//
	// if (WindowState.MULTIUPDATE == state) {
	// addInformationLabel(layout);
	//
	// GridLayout form = new GridLayout();
	// form.setColumns(2);
	// form.setSpacing(true);
	// form.setSizeUndefined();
	//
	// addField(form, groupsField);
	// panel.setContent(form);
	//
	// } else {
	// panel.setContent(groupsField);
	// }
	//
	// layout.addComponent(panel);
	// layout.setExpandRatio(panel, 1f);
	//
	// } else {
	// Label label = new Label(Messages.getString("Caption.Item.UserNoGroups"));
	// layout.addComponent(label);
	// layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
	// }
	//
	// return layout;
	// }

	// private Component buildUserPacksForm() {
	// VerticalLayout layout = new VerticalLayout();
	// layout.setSizeFull();
	// layout.setMargin(true);
	//
	// Panel panel = new Panel();
	// panel.setSizeFull();
	// panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
	//
	// if (WindowState.MULTIUPDATE == state) {
	// addInformationLabel(layout);
	//
	// GridLayout form = new GridLayout();
	// form.setColumns(2);
	// form.setSpacing(true);
	// form.setSizeUndefined();
	//
	// addField(form, packsField);
	// panel.setContent(form);
	//
	// } else {
	// panel.setContent(packsField);
	// }
	//
	// layout.addComponent(panel);
	// layout.setExpandRatio(panel, 1f);
	//
	// return layout;
	// }

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
				if (WindowState.CREATE == state) {
					success = new Notification(Messages.getString("Message.Info.UserAdded"));
				} else if (WindowState.UPDATE == state) {
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
			if (WindowState.CREATE == state) {
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

		if (WindowState.CREATE == state) {
			user.setOwnerId(loggedUser.getId());
		}

		if (WindowState.MULTIUPDATE != state) {
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

		if (autoDisableField.isVisible()) {
			user.setAutoDisable(autoDisableField.getValue());
		}

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

		if (testingDateField.isVisible()) {
			user.setTestingDate(testingDateField.getValue());
		}

		// if (groupsField != null && groupsField.isVisible() &&
		// groupsField.isEnabled()) {
		// for (Object itemId : groupsField.getItemIds()) {
		// Item item = groupsField.getItem(itemId);
		// Group group = groupService.merge((Group) itemId);
		// Boolean selected = (Boolean)
		// item.getItemProperty(FieldConstants.SELECTED).getValue();
		//
		// if (selected == null) {
		// if (WindowState.MULTIUPDATE == state) {
		// user.removeGroup(group);
		// }
		// } else if (selected.equals(true)) {
		// user.addGroup(group);
		// } else if (selected.equals(false)) {
		// user.removeGroup(group);
		// }
		//
		// if (group != null) {
		// bus.post(new MainUIEvent.GroupUsersChangedEvent(group));
		// }
		// }
		// }

		user = userService.add(user);

		if (permittedPacks.isVisible()) {
			permissionService.deleteUserPermissions(user);

			int rank = 1;
			for (Object itemId : permittedPacks.getItemIds()) {
				Pack pack = (Pack) itemId;
				permissionService.addUserPermission(new UserPermission(user, pack, true, 0, rank++));
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
		noteField.setValidationVisible(visible);
		// if (groupsField != null) {
		// groupsField.setValidationVisible(visible);
		// }
		// packsField.setValidationVisible(visible);
	}

	public void showWindow(User user) {
		showWindow(WindowState.UPDATE, user);
	}

	public void showWindow(Collection<User> users) {
		showWindow(WindowState.MULTIUPDATE, users);
	}
}
