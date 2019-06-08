package org.hypothesis.presenter;

import static com.vaadin.server.FontAwesome.ARROW_DOWN;
import static com.vaadin.server.FontAwesome.ARROW_UP;
import static com.vaadin.server.FontAwesome.BOOK;
import static com.vaadin.server.FontAwesome.COG;
import static com.vaadin.server.FontAwesome.TRASH;
import static com.vaadin.server.FontAwesome.USER;
import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.shared.Position.BOTTOM_CENTER;
import static com.vaadin.shared.ui.dd.VerticalDropLocation.BOTTOM;
import static com.vaadin.ui.AbstractSelect.AcceptItem.ALL;
import static com.vaadin.ui.Alignment.TOP_RIGHT;
import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static com.vaadin.ui.Table.TableDragMode.ROW;
import static com.vaadin.ui.themes.ValoTheme.BUTTON_PRIMARY;
import static com.vaadin.ui.themes.ValoTheme.OPTIONGROUP_HORIZONTAL;
import static com.vaadin.ui.themes.ValoTheme.PANEL_BORDERLESS;
import static com.vaadin.ui.themes.ValoTheme.TABLE_SMALL;
import static com.vaadin.ui.themes.ValoTheme.WINDOW_BOTTOM_TOOLBAR;
import static java.util.function.Function.identity;
import static org.hypothesis.data.api.Gender.FEMALE;
import static org.hypothesis.data.api.Gender.MALE;
import static org.hypothesis.data.api.Roles.ROLE_MANAGER;
import static org.hypothesis.data.api.Roles.ROLE_SUPERUSER;
import static org.hypothesis.data.api.Roles.ROLE_USER;
import static org.hypothesis.data.interfaces.FieldConstants.ID;
import static org.hypothesis.data.interfaces.FieldConstants.NAME;
import static org.hypothesis.data.interfaces.FieldConstants.ORDER;
import static org.hypothesis.presenter.WindowState.CREATE;
import static org.hypothesis.presenter.WindowState.MULTIUPDATE;
import static org.hypothesis.presenter.WindowState.UPDATE;
import static org.hypothesis.utility.UserUtility.userHasAnyRole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.LongItemSorter;
import org.hypothesis.data.api.Gender;
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.PackSetDto;
import org.hypothesis.data.dto.RoleDto;
import org.hypothesis.data.dto.UserDto;
import org.hypothesis.data.service.PackSetService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.service.impl.PackSetServiceImpl;
import org.hypothesis.data.service.impl.PermissionServiceImpl;
import org.hypothesis.data.service.impl.RoleServiceImpl;
import org.hypothesis.data.service.impl.UserServiceImpl;
import org.hypothesis.data.validator.RoleValidator;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.BroadcastService;
import org.hypothesis.utility.BirthNumberUtility;
import org.hypothesis.utility.DateUtility;
import org.hypothesis.utility.UIMessageUtility;
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
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
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
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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
	private CheckBox testingSuspendedField;
	private TextField noteField;
	// private Table groupsField;
	// private Table packsField;

	private Table permittedPacks;

	private boolean committed = false;

	private boolean isFirstRoleSelected;

	private boolean initializingFields = true;

	private final Map<String, RoleDto> allRoles;

	public UserWindowVNPresenter(MainEventBus bus) {
		super(bus);

		// groupService = GroupService.newInstance();
		userService = new UserServiceImpl();
		roleService = new RoleServiceImpl();
		permissionService = new PermissionServiceImpl();
		packSetService = new PackSetServiceImpl();

		allRoles = roleService.findAll().stream().collect(Collectors.toMap(RoleDto::getName, identity()));
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
					if (CREATE == state && StringUtils.isNotBlank(passwordField.getValue())) {
						processPeronalNumber(StringUtils.trim(passwordField.getValue()));
					}
				}
			});
		}
	}

	private void processPeronalNumber(String value) {
		List<UserDto> oldUsers = userService.findByPasswordAkaBirthNumberVN(value);
		if (!oldUsers.isEmpty()) {
			if (oldUsers.size() == 1) {
				source = oldUsers.get(0);
				state = UPDATE;
				fillFields();
				Notification.show(Messages.getString("Message.Info.PersonLoaded"));
			} else {
				Notification.show(Messages.getString("Message.Warning.MorePersons", value), WARNING_MESSAGE);
			}
		} else {
			if (!BirthNumberUtility.isValid(value)) {
				Notification.show(Messages.getString("Message.Warning.InvalidBirthNumber", value), WARNING_MESSAGE);
			}
			LocalDate birthDate = BirthNumberUtility.parseDate(value);
			if (birthDate == null) {
				Notification.show(Messages.getString("Message.Warning.CannotGetDateOfBirth"), WARNING_MESSAGE);
			} else {
				birthDateField.setValue(DateUtility.toDate(birthDate));
				switch (BirthNumberUtility.getSexChar(value)) {
				case 'M':
					genderField.setValue(MALE);
					break;
				case 'F':
					genderField.setValue(FEMALE);
					break;
				default:
					Notification.show(Messages.getString("Message.Warning.CannotGetGender"), WARNING_MESSAGE);
					break;
				}
			}

		}
	}

	private void buildRolesField() {
		if (rolesField == null) {
			rolesField = new OptionGroup(Messages.getString("Caption.Field.Role"));
			rolesField.addStyleName(OPTIONGROUP_HORIZONTAL);
			rolesField.setItemCaptionPropertyId(NAME);
			rolesField.setMultiSelect(true);
			rolesField.addValueChangeListener(new ValueChangeListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if (CREATE == state && !isFirstRoleSelected) {
						Set<RoleDto> roles = (Set<RoleDto>) event.getProperty().getValue();
						if (roles.size() == 1
								&& roles.stream().map(RoleDto::getName).anyMatch(name -> name.equals(ROLE_USER))) {
							isFirstRoleSelected = true;
							autoDisableField.setValue(true);
						}
					}
				}
			});

			BeanItemContainer<RoleDto> dataSource = new BeanItemContainer<RoleDto>(RoleDto.class);
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

	private void buildTestingSuspendedField() {
		if (testingSuspendedField == null) {
			testingSuspendedField = new CheckBox(Messages.getString("Caption.Field.TestingSuspended"));
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

			genderField.addItem(MALE);
			genderField.addItem(FEMALE);
			genderField.setItemCaption(MALE, Messages.getString(MALE.getMessageCode()));
			genderField.setItemCaption(FEMALE, Messages.getString(FEMALE.getMessageCode()));
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
			testingDateField.addValueChangeListener(new ValueChangeListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void valueChange(ValueChangeEvent event) {
					if (!initializingFields) {
						Set<RoleDto> roles = (Set<RoleDto>) rolesField.getValue();
						if (roles.size() == 1 && roles.contains(ROLE_USER)) {
							testingSuspendedField.setValue(true);
						}
					}
				}
			});
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

		BeanItemContainer<RoleDto> rolesSource = (BeanItemContainer<RoleDto>) ((AbstractSelect) rolesField)
				.getContainerDataSource();
		rolesSource.addAll(roleService.findAll());
		rolesSource.sort(new Object[] { ID }, new boolean[] { true });

		if (!userHasAnyRole(loggedUser, ROLE_SUPERUSER)) {
			rolesField.select(ROLE_USER);
			rolesField.setEnabled(false);
		} else if (CREATE != state) {
			rolesField.setRequired(true);
			rolesField.setRequiredError(Messages.getString("Message.Error.RoleRequired"));
			rolesField.addValidator(new RoleValidator(source, loggedUser));
		}

		if (!userHasAnyRole(loggedUser, ROLE_SUPERUSER)) {
			rolesField.setItemEnabled(ROLE_SUPERUSER, false);
			rolesField.setItemEnabled(ROLE_MANAGER, false);
			rolesField.setItemEnabled(ROLE_USER, false);
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

		// testing temporarily suspended
		buildTestingSuspendedField();

		// date of testing
		buildTestingDateField();

		if (WindowState.CREATE == state) {
			Set<RoleDto> roles = new HashSet<>();
			roles.add(allRoles.get(ROLE_USER));
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

	@Override
	protected void fillFields() {
		UserDto user = (UserDto) source;

		idField.setValue(user.getId().toString());
		usernameField.setValue(user.getUsername());
		nameField.setValue(user.getName());
		passwordField.setValue(user.getPassword());
		rolesField.setValue(user.getRoles());
		enabledField.setValue(user.getEnabled());
		autoDisableField.setValue(user.isAutoDisable());
		testingSuspendedField.setValue(user.isTestingSuspended());
		noteField.setValue(user.getNote());
		genderField.select(Gender.get(user.getGender()));
		educationField.setValue(user.getEducation());
		birthDateField.setValue(user.getBirthDate());

		testingDateField.setValue(user.getTestingDate());

		initializingFields = false;

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
		testingSuspendedField = null;
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
		panel.setIcon(COG);

		panel.setContent(buildUserPacksForm2());

		return panel;
	}

	private Component buildUserPacksForm2() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		hl.setMargin(true);
		hl.setSpacing(true);

		final Button btnUp = new Button(ARROW_UP);
		final Button btnDown = new Button(ARROW_DOWN);
		final Button btnDelete = new Button(TRASH);

		final Table table = new Table();
		table.setSizeFull();
		table.addStyleName(TABLE_SMALL);
		table.setSelectable(true);

		final BeanItemContainer<PackDto> dataSource = new BeanItemContainer<PackDto>(PackDto.class);

		table.setContainerDataSource(dataSource);

		table.addGeneratedColumn(ORDER, new ColumnGenerator() {
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				Container.Indexed container = (Container.Indexed) source.getContainerDataSource();
				return Integer.toString(container.indexOfId(itemId) + 1);
			}
		});

		table.setVisibleColumns(ORDER, NAME);
		table.setColumnHeaders(Messages.getString("Caption.Field.Order"), Messages.getString("Caption.Field.Name"));
		table.setSortEnabled(false);

		table.setDragMode(ROW);

		table.setDropHandler(new DropHandler() {
			@Override
			public AcceptCriterion getAcceptCriterion() {
				return new And(new SourceIs(table), ALL);
			}

			@Override
			public void drop(DragAndDropEvent event) {
				DataBoundTransferable t = (DataBoundTransferable) event.getTransferable();
				PackDto sourceItemId = (PackDto) t.getItemId();

				AbstractSelectTargetDetails dropData = (AbstractSelectTargetDetails) event.getTargetDetails();

				PackDto targetItemId = (PackDto) dropData.getItemIdOver();

				if (sourceItemId == targetItemId || targetItemId == null) {
					return;
				}

				dataSource.removeItem(sourceItemId);

				if (dropData.getDropLocation() == BOTTOM) {
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
				PackDto pack = (PackDto) table.getValue();
				if (pack != null) {
					List<PackDto> list = dataSource.getItemIds();
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
				PackDto pack = (PackDto) table.getValue();
				if (pack != null) {
					List<PackDto> list = dataSource.getItemIds();
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
				PackDto pack = (PackDto) table.getValue();
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
		if (UPDATE == state) {
			List<PackDto> packs = permissionService.getUserPacksVN(((UserDto) source).getId());
			for (PackDto pack : packs) {
				dataSource.addBean(pack);
			}
		}

		return hl;
	}

	private Component buildPackSets() {
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.setCaption(Messages.getString("Caption.Tab.PackSets"));
		panel.setIcon(BOOK);

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
		table.addStyleName(TABLE_SMALL);
		table.setSelectable(true);

		BeanItemContainer<PackSetDto> dataSource = new BeanItemContainer<PackSetDto>(PackSetDto.class);

		final BeanItemContainer<PackDto> detailSource = new BeanItemContainer<PackDto>(PackDto.class);

		List<PackSetDto> packSets = packSetService.findAll();
		for (PackSetDto packSet : packSets) {
			dataSource.addBean(packSet);
		}
		table.setContainerDataSource(dataSource);
		dataSource.setItemSorter(new LongItemSorter());
		table.setSortEnabled(false);
		table.setSortContainerPropertyId(ID);
		table.sort();

		table.setVisibleColumns(NAME);
		table.setColumnHeaders(Messages.getString("Caption.Field.AvailablePackSets"));

		Table detailTable = new Table();
		detailTable.setSizeFull();
		detailTable.addStyleName(TABLE_SMALL);

		detailTable.setContainerDataSource(detailSource);

		detailTable.setVisibleColumns(NAME);
		detailTable.setColumnHeaders(Messages.getString("Caption.Field.PackSetContent"));
		detailTable.setSortEnabled(false);
		detailTable.setSelectable(true);
		detailTable.setMultiSelect(true);

		hl.addComponent(table);
		hl.addComponent(detailTable);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidth(100.0f, PERCENTAGE);
		buttonLayout.setSpacing(true);

		final Button btnAdd = new Button(Messages.getString("Caption.Button.AddSelectedPackSet"), new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = table.getValue();
				if (itemId != null) {
					addSelectedPackSet((PackSetDto) itemId);
				}

			}
		});
		btnAdd.setEnabled(false);

		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				btnAdd.setEnabled(table.getValue() != null);

				updateDetailContainer(detailSource, (PackSetDto) table.getValue());
				detailTable.setValue(null);
			}
		});

		final Button btnAddSelected = new Button(Messages.getString("Caption.Button.AddSelectedPacks"),
				new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						@SuppressWarnings("unchecked")
						Set<PackDto> items = (Set<PackDto>) detailTable.getValue();
						if (!items.isEmpty()) {
							addSelectedPacks(items);
							detailTable.setValue(null);
						}
					}
				});
		btnAddSelected.setEnabled(false);

		detailTable.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				final Set<?> values = (Set<?>) detailTable.getValue();
				btnAddSelected.setEnabled(!values.isEmpty());
			}
		});

		buttonLayout.addComponents(btnAdd, btnAddSelected);

		vl.addComponent(hl);
		vl.addComponent(buttonLayout);

		vl.setExpandRatio(hl, 1.0f);

		return vl;
	}

	private void addSelectedPackSet(PackSetDto packSet) {
		addSelectedPacks(packSet.getPacks());
	}

	@SuppressWarnings("unchecked")
	private void addSelectedPacks(Collection<PackDto> packs) {
		if (permittedPacks != null && permittedPacks.getContainerDataSource() != null) {
			BeanItemContainer<PackDto> dataSource = (BeanItemContainer<PackDto>) permittedPacks
					.getContainerDataSource();
			for (PackDto pack : packs) {
				if (!dataSource.containsId(pack)) {
					dataSource.addBean(pack);
				}
			}
		}
	}

	private void updateDetailContainer(BeanItemContainer<PackDto> container, PackSetDto packSet) {
		if (container != null && packSet != null) {
			container.removeAllItems();
			for (PackDto pack : packSet.getPacks()) {
				container.addBean(pack);
			}
		}
	}

	private Component buildUserDetail() {
		Panel panel = new Panel();
		panel.setSizeFull();

		panel.setCaption(Messages.getString("Caption.Tab.UserDetails"));
		panel.setIcon(USER);

		panel.setContent(buildUserDetailsForm());

		return panel;
	}

	private Component buildUserDetailsForm() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		if (MULTIUPDATE == state) {
			addInformationLabel(layout);
		}

		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		// ID
		if (UPDATE == state) {
			addField(form, idField);
		}

		if (MULTIUPDATE != state) {
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
		addField(form, testingSuspendedField);

		if (MULTIUPDATE != state) {
			addField(form, birthDateField);
		}

		addField(form, genderField);
		addField(form, educationField);
		addField(form, testingDateField);
		addField(form, noteField);

		Panel panel = new Panel(form);
		panel.setSizeFull();
		panel.addStyleName(PANEL_BORDERLESS);

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
		footer.addStyleName(WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, PERCENTAGE);
		footer.setSpacing(true);

		Button ok = new Button(Messages.getString("Caption.Button.OK"));
		ok.addStyleName(BUTTON_PRIMARY);
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				commitFormWithMessage();
			}
		});
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, TOP_RIGHT);

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
				if (CREATE == state) {
					success = new Notification(Messages.getString("Message.Info.UserAdded"));
				} else if (UPDATE == state) {
					success = new Notification(Messages.getString("Message.Info.UserUpdated"));
				} else {
					success = new Notification(Messages.getString("Message.Info.UsersUpdated"));
				}
				success.setDelayMsec(2000);
				success.setPosition(BOTTOM_CENTER);
				success.show(Page.getCurrent());

				window.close();
			}

		} catch (CommitException e) {
			Notification.show(e.getMessage(), WARNING_MESSAGE);
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

		if (CREATE == state) {
			final UserDto oldUser = userService.findFullByUsernamePassword(usernameField.getValue(),
					passwordField.getValue());
			if (oldUser != null) {
				ConfirmDialog.show(UI.getCurrent(), Messages.getString("Caption.Dialog.ConfirmReplace"),
						Messages.getString("Caption.Confirm.User.OverwriteExisting"),
						Messages.getString("Caption.Button.Confirm"), Messages.getString("Caption.Button.Cancel"),
						new Listener() {
							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									state = UPDATE;
									source = oldUser;
									loggedUser = SessionManager.getLoggedUser2();

									commitFormWithMessage();
								}
							}
						});

				return;
			}
		}

		if (MULTIUPDATE == state) {
			for (UserDto user : (Collection<UserDto>) source) {
				user = saveUser(user);
				if (user != null) {
					bus.post(new MainUIEvent.UserAddedEvent(user));
				}
			}

		} else {
			UserDto user;
			if (CREATE == state) {
				user = new UserDto();
			} else {
				user = (UserDto) source;
			}
			user = saveUser(user);
			if (user != null) {
				bus.post(new MainUIEvent.UserAddedEvent(user));
			}
		}

		committed = true;
	}

	@SuppressWarnings("unchecked")
	private UserDto saveUser(UserDto user) {
		boolean savingLoggedUser = user.equals(loggedUser);

		if (CREATE == state) {
			user.setOwnerId(loggedUser.getId());
		}

		if (MULTIUPDATE != state) {
			user.setUsername(usernameField.getValue());
			user.setPassword(passwordField.getValue());
		}

		if (nameField.isVisible()) {
			user.setName(nameField.getValue());
		}

		if (rolesField.isVisible()) {
			Set<RoleDto> roles = new HashSet<>();
			for (RoleDto role : user.getRoles()) {
				roles.add(role);
			}
			for (RoleDto role : roles) {
				user.getRoles().remove(role);
			}
			roles = (Set<RoleDto>) rolesField.getValue();
			for (RoleDto role : roles) {
				user.getRoles().add(role);
			}
		}

		if (enabledField.isVisible()) {
			user.setEnabled(enabledField.getValue());
		}

		if (autoDisableField.isVisible()) {
			user.setAutoDisable(autoDisableField.getValue());
		}

		if (testingSuspendedField.isVisible()) {
			user.setTestingSuspended(testingSuspendedField.getValue());
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

		user = userService.save(user);

		if (permittedPacks.isVisible()) {
			final Set<PackDto> packs = new HashSet<>();
			for (Object itemId : permittedPacks.getItemIds()) {
				PackDto pack = (PackDto) itemId;
				packs.add(pack);
			}
			permissionService.setUserPermissions(user.getId(), packs, null);
		}

		if (savingLoggedUser && rolesField.isVisible()) {
			Set<RoleDto> oldRoles = loggedUser.getRoles();
			Set<RoleDto> newRoles = user.getRoles();

			SessionManager.setLoggedUser2(userService.getSimpleById(user.getId()));

			if (!oldRoles.equals(newRoles)) {
				// Superuser/Manager -> User degradation
				if (!newRoles.contains(ROLE_MANAGER) && !newRoles.contains(ROLE_SUPERUSER)) {
					bus.post(new MainUIEvent.UserLoggedOutEvent());

					// Superuser -> Manager degradation
				} else if (oldRoles.contains(ROLE_SUPERUSER) && !newRoles.contains(ROLE_SUPERUSER)) {
					bus.post(new MainUIEvent.ProfileUpdatedEvent());
					// TODO: zmena vypisu skupin
				}
			}

			bus.post(new MainUIEvent.UserPacksChangedEvent(user));
		}

		BroadcastService.broadcast(UIMessageUtility.createRefreshUserPacksViewMessage(user.getId()));

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

	public void showWindow(UserDto user) {
		showWindow(UPDATE, user);
	}

	public void showWindow(Collection<UserDto> users) {
		showWindow(MULTIUPDATE, users);
	}
}
