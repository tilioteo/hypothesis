package org.hypothesis.presenter;

import com.vaadin.data.Container;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import org.apache.commons.lang3.StringUtils;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.LongItemSorter;
import org.hypothesis.data.model.*;
import org.hypothesis.data.service.PackSetService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.validator.RoleValidator;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.push.Pushable;
import org.hypothesis.server.Messages;
import org.hypothesis.servlet.Broadcaster;
import org.hypothesis.utility.BirthNumberUtility;
import org.hypothesis.utility.DateUtility;
import org.hypothesis.utility.UIMessageUtility;
import org.vaadin.dialogs.ConfirmDialog;

import java.time.LocalDate;
import java.util.*;

import static com.vaadin.server.FontAwesome.*;
import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.shared.Position.BOTTOM_CENTER;
import static com.vaadin.shared.ui.dd.VerticalDropLocation.BOTTOM;
import static com.vaadin.ui.AbstractSelect.AcceptItem.ALL;
import static com.vaadin.ui.Alignment.TOP_RIGHT;
import static com.vaadin.ui.Notification.Type.WARNING_MESSAGE;
import static com.vaadin.ui.Table.TableDragMode.ROW;
import static com.vaadin.ui.themes.ValoTheme.*;
import static org.hypothesis.data.model.FieldConstants.*;
import static org.hypothesis.data.model.Gender.FEMALE;
import static org.hypothesis.data.model.Gender.MALE;
import static org.hypothesis.data.service.RoleService.*;
import static org.hypothesis.presenter.WindowState.*;

/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class UserWindowVNPresenter extends AbstractWindowPresenter implements Broadcaster, Pushable {

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

    private Table permittedPacks;

    private boolean committed = false;

    private boolean isFirstRoleSelected;

    private boolean initializingFields = true;

    public UserWindowVNPresenter() {
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

            passwordField.addBlurListener(e -> {
                if (CREATE == state && StringUtils.isNotBlank(passwordField.getValue())) {
                    processPeronalNumber(StringUtils.trim(passwordField.getValue()));
                }
            });
        }
    }

    private void processPeronalNumber(String value) {
        List<User> oldUsers = userService.findByPasswordAkaBirthNumber(value);
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
            rolesField.addValueChangeListener(e -> {
                if (CREATE == state && !isFirstRoleSelected) {
                    Set<Role> roles = (Set<Role>) e.getProperty().getValue();
                    if (roles.size() == 1 && roles.contains(ROLE_USER)) {
                        isFirstRoleSelected = true;
                        autoDisableField.setValue(true);
                    }
                }
            });

            BeanItemContainer<Role> dataSource = new BeanItemContainer<>(Role.class);
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
            testingDateField.addValueChangeListener(e -> {
                if (!initializingFields) {
                    Set<Role> roles = (Set<Role>) rolesField.getValue();
                    if (roles.size() == 1 && roles.contains(ROLE_USER)) {
                        testingSuspendedField.setValue(true);
                    }
                }
            });
        }
    }

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
        rolesSource.sort(new Object[]{ID}, new boolean[]{true});

        if (!loggedUser.hasRole(ROLE_SUPERUSER)) {
            rolesField.select(ROLE_USER);
            rolesField.setEnabled(false);
        } else if (CREATE != state) {
            rolesField.setRequired(true);
            rolesField.setRequiredError(Messages.getString("Message.Error.RoleRequired"));
            rolesField.addValidator(new RoleValidator(source, loggedUser));
        }

        if (!loggedUser.hasRole(ROLE_SUPERUSER)) {
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
            Set<Role> roles = new HashSet<>();
            roles.add(ROLE_USER);
            rolesField.setValue(roles);

            testingDateField.setValue(DateUtility.toDate(LocalDate.now()));
        }
    }

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
        testingSuspendedField.setValue(user.isTestingSuspended());
        noteField.setValue(user.getNote());
        genderField.select(Gender.get(user.getGender()));
        educationField.setValue(user.getEducation());
        birthDateField.setValue(user.getBirthDate());

        testingDateField.setValue(user.getTestingDate());

        initializingFields = false;
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

        layout.addComponent(buildPackSets());
        layout.addComponent(buildUserPacks2());

        return layout;
    }

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

        final BeanItemContainer<Pack> dataSource = new BeanItemContainer<>(Pack.class);

        table.setContainerDataSource(dataSource);

        table.addGeneratedColumn(ORDER, (source, itemId, columnId) -> {
            Container.Indexed container = (Container.Indexed) source.getContainerDataSource();
            return Integer.toString(container.indexOfId(itemId) + 1);
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
                Pack sourceItemId = (Pack) t.getItemId();

                AbstractSelectTargetDetails dropData = (AbstractSelectTargetDetails) event.getTargetDetails();

                Pack targetItemId = (Pack) dropData.getItemIdOver();

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

        btnUp.addClickListener(e -> {
            Pack pack = (Pack) table.getValue();
            if (pack != null) {
                List<Pack> list = dataSource.getItemIds();
                int idx = list.indexOf(pack);
                if (idx > 0) {
                    dataSource.removeItem(pack);
                    dataSource.addItemAt(idx - 1, pack);
                }
            }
        });

        btnDown.addClickListener(e -> {
            Pack pack = (Pack) table.getValue();
            if (pack != null) {
                List<Pack> list = dataSource.getItemIds();
                int idx = list.indexOf(pack);
                if (idx < list.size() - 1) {
                    dataSource.removeItem(pack);
                    dataSource.addItemAt(idx + 1, pack);
                }
            }
        });

        btnDelete.addClickListener(e -> {
            Pack pack = (Pack) table.getValue();
            if (pack != null) {
                dataSource.removeItem(pack);
            }
        });

        buttonLayout.addComponent(btnUp);
        buttonLayout.addComponent(btnDown);
        buttonLayout.addComponent(btnDelete);

        hl.addComponent(buttonLayout);

        permittedPacks = table;

        // fill table
        if (UPDATE == state) {
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

        BeanItemContainer<PackSet> dataSource = new BeanItemContainer<>(PackSet.class);

        final BeanItemContainer<Pack> detailSource = new BeanItemContainer<>(Pack.class);

        List<PackSet> packSets = packSetService.findAll();
        for (PackSet packSet : packSets) {
            packSet = packSetService.merge(packSet);
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

        final Button btnAdd = new Button(Messages.getString("Caption.Button.AddSelectedPackSet"), e -> {
            Object itemId = table.getValue();
            if (itemId != null) {
                addSelectedPackSet((PackSet) itemId);
            }

        });
        btnAdd.setEnabled(false);

        table.addValueChangeListener(e -> {
            btnAdd.setEnabled(table.getValue() != null);

            updateDetailContainer(detailSource, (PackSet) table.getValue());
            detailTable.setValue(null);
        });

        final Button btnAddSelected = new Button(Messages.getString("Caption.Button.AddSelectedPacks"),
                e -> {
                    @SuppressWarnings("unchecked")
                    Set<Pack> items = (Set<Pack>) detailTable.getValue();
                    if (!items.isEmpty()) {
                        addSelectedPacks(items);
                        detailTable.setValue(null);
                    }
                });
        btnAddSelected.setEnabled(false);

        detailTable.addValueChangeListener(e -> {
            final Set<?> values = (Set<?>) detailTable.getValue();
            btnAddSelected.setEnabled(!values.isEmpty());
        });

        buttonLayout.addComponents(btnAdd, btnAddSelected);

        vl.addComponent(hl);
        vl.addComponent(buttonLayout);

        vl.setExpandRatio(hl, 1.0f);

        return vl;
    }

    private void addSelectedPackSet(PackSet packSet) {
        packSet = packSetService.merge(packSet);

        addSelectedPacks(packSet.getPacks());
    }

    @SuppressWarnings("unchecked")
    private void addSelectedPacks(Collection<Pack> packs) {
        if (permittedPacks != null && permittedPacks.getContainerDataSource() != null) {
            BeanItemContainer<Pack> dataSource = (BeanItemContainer<Pack>) permittedPacks.getContainerDataSource();
            for (Pack pack : packs) {
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

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, PERCENTAGE);
        footer.setSpacing(true);

        Button ok = new Button(Messages.getString("Caption.Button.OK"));
        ok.addStyleName(BUTTON_PRIMARY);
        ok.addClickListener(e -> commitFormWithMessage());
        ok.focus();
        footer.addComponent(ok);
        footer.setComponentAlignment(ok, TOP_RIGHT);

        Button cancel = new Button(Messages.getString("Caption.Button.Cancel"));
        cancel.addClickListener(e -> window.close());
        footer.addComponent(cancel);

        return footer;
    }

    protected void commitFormWithMessage() {
        try {
            commitForm();

            if (committed) {
                Notification success;
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
                throw new CommitException(e.getMessage());
            }
        }

        if (CREATE == state) {
            final User oldUser = userService.findByUsernamePassword(usernameField.getValue(), passwordField.getValue());
            if (oldUser != null) {
                ConfirmDialog.show(UI.getCurrent(), Messages.getString("Caption.Dialog.ConfirmReplace"),
                        Messages.getString("Caption.Confirm.User.OverwriteExisting"),
                        Messages.getString("Caption.Button.Confirm"), Messages.getString("Caption.Button.Cancel"),
                        dialog -> {
                            if (dialog.isConfirmed()) {
                                state = UPDATE;
                                source = oldUser;
                                loggedUser = SessionManager.getLoggedUser();

                                commitFormWithMessage();
                            }
                        });

                return;
            }
        }

        if (MULTIUPDATE == state) {
            for (User user : (Collection<User>) source) {
                user = saveUser(user);
                if (user != null) {
                    getBus().post(new MainUIEvent.UserAddedEvent(user));
                }
            }

        } else {
            User user;
            if (CREATE == state) {
                user = new User();
            } else {
                user = (User) source;
            }
            user = saveUser(user);
            if (user != null) {
                getBus().post(new MainUIEvent.UserAddedEvent(user));
            }
        }

        committed = true;
    }

    @SuppressWarnings("unchecked")
    private User saveUser(User user) {
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
            Set<Role> roles = new HashSet<>(user.getRoles());
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
                if (!newRoles.contains(ROLE_MANAGER) && !newRoles.contains(ROLE_SUPERUSER)) {
                    getBus().post(new MainUIEvent.UserLoggedOutEvent());

                    // Superuser -> Manager degradation
                } else if (oldRoles.contains(ROLE_SUPERUSER) && !newRoles.contains(ROLE_SUPERUSER)) {
                    getBus().post(new MainUIEvent.ProfileUpdatedEvent());
                    // TODO: zmena vypisu skupin
                }
            }

            getBus().post(new MainUIEvent.UserPacksChangedEvent(user));
        }
        final Long userId = user.getId();
        broadcast(UIMessageUtility.createRefreshUserPacksViewMessage(userId));

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
    }

    public void showWindow(User user) {
        showWindow(UPDATE, user);
    }

    public void showWindow(Collection<User> users) {
        showWindow(MULTIUPDATE, users);
    }
}
