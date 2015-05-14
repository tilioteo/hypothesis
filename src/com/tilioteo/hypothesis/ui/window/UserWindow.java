package com.tilioteo.hypothesis.ui.window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.tilioteo.hypothesis.core.CaseInsensitiveItemSorter;
import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.entity.FieldConstants;
import com.tilioteo.hypothesis.entity.Group;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Role;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.entity.UserPermission;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.tilioteo.hypothesis.persistence.GroupManager;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.PersistenceManager;
import com.tilioteo.hypothesis.persistence.RoleManager;
import com.tilioteo.hypothesis.persistence.UserManager;
import com.tilioteo.hypothesis.ui.form.UserFormFields;
import com.tilioteo.hypothesis.ui.form.validator.RoleValidator;
import com.tilioteo.hypothesis.ui.form.validator.UsernameValidator;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings({ "serial", "unchecked" })
public class UserWindow extends Window {

	private enum State { CREATE, UPDATE, MULTIUPDATE };

	private Object source = null;
	private State state; 
	
	private User loggedUser;
	private UserFormFields userForm;
	private UserManager userManager;
	private GroupManager groupManager;
	private RoleManager roleManager;
	private PermissionManager permissionManager;
	
	private PersistenceManager persistenceManager;
	
	private TabSheet tabSheet;
	private Component userDetailsTab;
	
	private ArrayList<AbstractField<?>> formFields;
	
	private Boolean generateNames = false;

	
	public UserWindow() {
		source = null;
		state = State.CREATE;
    }
	
	public UserWindow(User user) {
		source = user;
		state = State.UPDATE;
	}
	
	public UserWindow(Collection<User> users) {
		source = users;
		state = State.MULTIUPDATE;
	}
	
	public void open() {
        MainEventBus.get().post(new HypothesisEvent.CloseOpenWindowsEvent());
        UI.getCurrent().addWindow(this);
        this.focus();

        loggedUser = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
        userManager = UserManager.newInstance();
        groupManager = GroupManager.newInstance();
        roleManager = RoleManager.newInstance();
        permissionManager = PermissionManager.newInstance();
        userForm = new UserFormFields();
        
        persistenceManager = PersistenceManager.newInstance();

        center();
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setWidth(50, Unit.PERCENTAGE);
        setHeight(80.0f, Unit.PERCENTAGE);
        setModal(true);
		
        initFields();
        if (state.equals(State.UPDATE)) {
        	fillFields((User) source);
        }
        
        buildContent();
	}
	
	private void initFields() {
		formFields = new ArrayList<AbstractField<?>>();
		
		// ID
		userForm.buildIdField();
        
		// username
		AbstractField<String> userField = userForm.buildUsernameField();
		if (state.equals(State.CREATE)) {
			userField.addValidator(new UsernameValidator(null));
		} else if (state.equals(State.UPDATE)) {
			userField.addValidator(
					new UsernameValidator(((User) source).getId()));		
		}
		
		// username generator
		userForm.buildGeneratedGroupField();
		userForm.buildGeneratedCountField();
		
		// password
		userForm.buildPasswordField();
		
		// roles
		AbstractSelect rolesField = userForm.buildRolesField();
		
		BeanItemContainer<Role> rolesSource = (BeanItemContainer<Role>)
				((AbstractSelect) rolesField).getContainerDataSource();
		rolesSource.addAll(roleManager.findAll());
		rolesSource.sort(
				new Object[] { FieldConstants.ID },
				new boolean[] { true });
        
		if (!loggedUser.hasRole(RoleManager.ROLE_SUPERUSER)) {
			rolesField.select(RoleManager.ROLE_USER);
			rolesField.setEnabled(false);
		} else if (!state.equals(State.CREATE)) {
			rolesField.setRequired(true);
			rolesField.setRequiredError(
					Messages.getString("Message.Error.RoleRequired"));
			rolesField.addValidator(new RoleValidator(source, loggedUser));
		}
		
		// enabled
		userForm.buildEnabledField();
		
		// expire date
		userForm.buildExpireDateField();
        
		// note
        userForm.buildNoteField();
        
        // groups
		Collection<Group> groups;

		if (loggedUser.hasRole(RoleManager.ROLE_SUPERUSER)) {
			groups = groupManager.findAll();
		} else {
			groups = groupManager.findOwnerGroups(loggedUser);
		}
		
		if (!groups.isEmpty()) {
	        AbstractField<?> groupsField = userForm.buildGroupsField(
	        		!loggedUser.hasRole(RoleManager.ROLE_SUPERUSER));

	        for (Group group : groups) {
				Table table = (Table) groupsField;
				table.addItem(group);
				Item row = table.getItem(group);
				row.getItemProperty(FieldConstants.NAME).
						setValue(group.getName());
			}
			
			((IndexedContainer) ((Table) groupsField).
					getContainerDataSource()).setItemSorter(
							new CaseInsensitiveItemSorter());
			((Table) groupsField).sort(
					new Object[] { FieldConstants.NAME },
					new boolean[] { true });
		}

		// packs
		AbstractField<?> packsField = userForm.buildPacksField();
		
		Collection<Pack> packs;
		if (loggedUser.hasRole(RoleManager.ROLE_SUPERUSER)) {
			packs = permissionManager.findAllPacks();
		} else {
			packs = permissionManager.findUserPacks2(loggedUser, false);
		}
		
		// TODO: upozornit, pokud nema uzivatel pristupne zadne packy?

		for (Pack pack : packs) {
			Table table = (Table) packsField;
			table.addItem(pack);
			Item row = table.getItem(pack);
			row.getItemProperty(FieldConstants.NAME).setValue(pack.getName());
		}
		
		((IndexedContainer) ((Table) packsField).
				getContainerDataSource()).setItemSorter(
						new CaseInsensitiveItemSorter());
		((Table) packsField).sort(
				new Object[] { FieldConstants.NAME },
				new boolean[] { true });
	}
	
	private void fillFields(User user) {
		//user = userManager.merge(user);
		user = persistenceManager.merge(user);

		userForm.getIdField().setValue(user.getId().toString());
		userForm.getUsernameField().setValue(user.getUsername());
		userForm.getPasswordField().setValue(user.getPassword());
		userForm.getRolesField().setValue(user.getRoles());
		userForm.getEnabledField().setValue(user.getEnabled());
		userForm.getExpireDateField().setValue(user.getExpireDate());
		userForm.getNoteField().setValue(user.getNote());
		
		// groups
		if (userForm.getGroupsField() != null) {
			Set<Group> groups;
			
			if (state.equals(State.UPDATE)) {
				groups = user.getGroups();
			} else {
				groups = new HashSet<Group>();
			}
			
			AbstractSelect groupsField = 
					((AbstractSelect) userForm.getGroupsField());
			for (Object itemId : groupsField.getItemIds()) {
				Item row = groupsField.getItem(itemId);
				Group group = (Group) itemId;
	
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
		
		if (state.equals(State.UPDATE)) {
			enabledPacks = permissionManager.getUserPacks(user, true, null);
			disabledPacks = permissionManager.getUserPacks(user, false, null);
		} else {
			enabledPacks = new HashSet<Pack>();
			disabledPacks = new HashSet<Pack>();
		}
		
		for (Object itemId : userForm.getPacksField().getItemIds()) {
			Item row = userForm.getPacksField().getItem(itemId);
			Pack pack = (Pack) itemId;

			if (enabledPacks.contains(pack)) {
				row.getItemProperty(FieldConstants.TEST_STATE).setValue(true);
			} else if (disabledPacks.contains(pack)) {
				row.getItemProperty(FieldConstants.TEST_STATE).setValue(false);
			} else {
				row.getItemProperty(FieldConstants.TEST_STATE).setValue(null);
			}
		}	
	}
	
	private void buildContent() {
		VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        setContent(content);
        
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
        panel.addStyleName("borderless");
        panel.setContent(buildUserDetailsForm());
		tab.addComponent(panel);
		
		userDetailsTab = tab;
	    
	    return tab;
	}
	
	private Component buildUserDetailsForm() {
		VerticalLayout layout = new VerticalLayout();
		
        if (state.equals(State.MULTIUPDATE)) {
        	addInformationLabel(layout);
		}
        
		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		// ID
		if (state.equals(State.UPDATE)) {
			addField(form, userForm.getIdField());
        }
        
		// username
		final AbstractField<String> usernameField =
				userForm.getUsernameField(); 

		if (state.equals(State.CREATE)) {
			final VerticalLayout nameLayout = new VerticalLayout();
			nameLayout.setCaption(usernameField.getCaption());
			nameLayout.setSpacing(true);
	    	
			final HorizontalLayout generatedNameLayout = new HorizontalLayout();
			generatedNameLayout.addComponent(userForm.getGeneratedGroupField());
			generatedNameLayout.addComponent(new Label("-"));
			generatedNameLayout.addComponent(userForm.getGeneratedCountField());
			generatedNameLayout.addComponent(new Label("-XXXX"));
			generatedNameLayout.setVisible(false);
			addField(form, generatedNameLayout);
			
			final CheckBox nameFieldSwitch = new CheckBox(
					Messages.getString("Caption.Button.GenerateName"));
			nameFieldSwitch.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					boolean generate = nameFieldSwitch.getValue(); 
					generatedNameLayout.setVisible(generate);
					usernameField.setVisible(!generate);
					userForm.getPasswordField().setEnabled(!generate);
					generateNames = generate;
					
					if (generate) {
						formFields.add(0, userForm.getGeneratedCountField());
						formFields.add(0, userForm.getGeneratedGroupField());
						formFields.remove(usernameField);
					} else {
						formFields.remove(userForm.getGeneratedGroupField());
						formFields.remove(userForm.getGeneratedCountField());
						formFields.add(0, usernameField);						
					}
				}
			});
			
			nameLayout.addComponent(usernameField);
			nameLayout.addComponent(generatedNameLayout);
			nameLayout.addComponent(nameFieldSwitch);
			
			addField(form, nameLayout);
			formFields.add((AbstractField<?>) usernameField);
			usernameField.setCaption(null);
		
		} else if (state.equals(State.UPDATE)) {
			addField(form, usernameField);
		}

		if (!(state.equals(State.MULTIUPDATE))) {
			addField(form, userForm.getPasswordField());
		}
        
        addField(form, userForm.getRolesField());
        addField(form, userForm.getEnabledField());
        addField(form, userForm.getExpireDateField());
		addField(form, userForm.getNoteField());

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
		
		if (userForm.getGroupsField() != null) {
	        if (state.equals(State.MULTIUPDATE)) {
	        	addInformationLabel(layout);
			}
	        
			GridLayout form = new GridLayout();
			form.setColumns(2);
			form.setMargin(true);
			form.setSpacing(true);
			
			addField(form, userForm.getGroupsField());
			
			layout.addComponent(form);
		
		} else {
			Label label = new Label(
					Messages.getString("Caption.Item.UserNoGroups"));
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
		
        if (state.equals(State.MULTIUPDATE)) {
        	addInformationLabel(layout);
		}
        
		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);
		
		addField(form, userForm.getPacksField());
		
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

                	String message;
                	
                	if (state.equals(State.CREATE)) {
                		message = Messages.getString("Message.Info.UserAdded");
                	} else if (state.equals(State.UPDATE)) {
                		message = Messages.getString("Message.Info.UserUpdated");
                	} else {
                		message = Messages.getString("Message.Info.UsersUpdated");               		
                	}
                	Notification success = new Notification(message);
                    success.setDelayMsec(2000);
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());

                    close();
                
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
				close();
			}
        });
        footer.addComponent(cancel);
        
        return footer;
	}
	
	protected void commitForm() throws CommitException {
		for (AbstractField<?> field : formFields) {
			try {
				if (field.isEnabled()) {
					field.validate();
				}
			} catch (InvalidValueException e) {
				field.focus();
				setValidationVisible(true);
				tabSheet.setSelectedTab(userDetailsTab);
				throw new CommitException(e.getMessage());
			}
		}
        
        if (state.equals(State.MULTIUPDATE)) {
        	for (User user : (Collection<User>) source) {
        		user = saveUser(user, true);
                MainEventBus.get().post(
                		new HypothesisEvent.UserAddedEvent(user));
        	}
        	
        } else if (generateNames) {
    		String usernameGroup = (String) userForm.
    				getGeneratedGroupField().getValue();
    		int count = Integer.valueOf((String) userForm.
    				getGeneratedCountField().getValue());

    		for (int i = 1; i <= count; i++) {
        		User user = new User();
        		user.setUsername(String.format("%s-%03d-%s",
        				usernameGroup, i,
        				generateString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 4)));
        		user.setPassword(generateString(
        				"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
        				8));
        		user = saveUser(user, false);
        	}
        
        } else {
        	User user;
        	if (state.equals(State.CREATE)) {
        		user = new User();
        	} else {
        		user = (User) source;
        	}
            user = saveUser(user, true);
            MainEventBus.get().post(
            		new HypothesisEvent.UserAddedEvent(user));
        }
	}

	private User saveUser(User user, boolean includeGenerableFields) {
		boolean savingLoggedUser = user.equals(loggedUser);

		if (state.equals(State.CREATE)) {
        	user.setOwnerId(loggedUser.getId());
		} else {
			//user = userManager.merge(user);
			user = persistenceManager.merge(user);
		}
	
		if (includeGenerableFields) {
			if (!(state.equals(State.MULTIUPDATE))) {
				user.setUsername(userForm.getUsernameField().getValue());
				user.setPassword(userForm.getPasswordField().getValue());
			}
		}

		if (userForm.getRolesField().isVisible()) {
			Set<Role> roles = new HashSet<Role>();
			for (Role role : user.getRoles()) {
				roles.add(role);
			}
			for (Role role : roles) {
				user.removeRole(role);
			}
			roles = (Set<Role>) userForm.getRolesField().getValue();
			for (Role role : roles) {
				user.addRole(role);
			}
		}
		
		if (userForm.getEnabledField().isVisible()) {
			user.setEnabled(userForm.getEnabledField().getValue());
		}
		
		if (userForm.getExpireDateField().isVisible()) {
			user.setExpireDate(userForm.getExpireDateField().getValue());
		}
		
		if (userForm.getNoteField().isVisible()) {
			user.setNote(userForm.getNoteField().getValue());
		}
		
		if (userForm.getGroupsField() != null &&
				userForm.getGroupsField().isVisible() &&
				userForm.getGroupsField().isEnabled()) {
			AbstractSelect groupsField =
					(AbstractSelect) userForm.getGroupsField();
			
			for (Object itemId : groupsField.getItemIds()) {
				Item item = groupsField.getItem(itemId);
				Group group = (Group) itemId;
				Boolean selected = (Boolean) item.getItemProperty(
						FieldConstants.SELECTED).getValue();
				
				if (selected == null) {
					if (state.equals(State.MULTIUPDATE)) {
						user.removeGroup(group);
					}
				} else if (selected.equals(true)) {
					user.addGroup(group);
				} else if (selected.equals(false)) {
					user.removeGroup(group);
				}

				MainEventBus.get().post(new HypothesisEvent.
						GroupUsersChangedEvent(group));
			}
		}
		
		user = userManager.add(user);
		
		if (userForm.getPacksField().isVisible()) {
	        permissionManager.deleteUserPermissions(user);

			for (Object itemId : userForm.getPacksField().getItemIds()) {
				Item item = userForm.getPacksField().getItem(itemId);
				Pack pack = (Pack) itemId;
				Boolean testState = (Boolean) item.getItemProperty(
						FieldConstants.TEST_STATE).getValue();
				
				if (testState != null) {
					permissionManager.addUserPermission(
							new UserPermission(user, pack, testState));
				}
			}
        }
		
		if (savingLoggedUser && userForm.getRolesField().isVisible()) {
			Set<Role> oldRoles = loggedUser.getRoles();
			Set<Role> newRoles = user.getRoles();

			if (!oldRoles.equals(newRoles)) {
				// Superuser/Manager -> User degradation
				if (!newRoles.contains(RoleManager.ROLE_MANAGER)
						&& !newRoles.contains(RoleManager.ROLE_SUPERUSER)) {
					MainEventBus.get().post(
			        		new HypothesisEvent.UserLoggedOutEvent());

				// Superuser -> Manager degradation
				} else if (oldRoles.contains(RoleManager.ROLE_SUPERUSER)
						&& !newRoles.contains(RoleManager.ROLE_SUPERUSER)) {
					MainEventBus.get().post(
							new HypothesisEvent.ProfileUpdatedEvent());
					// TODO: zmena vypisu skupin
				}
			}	
		}
		
		return user;
	}

	private void addInformationLabel(AbstractOrderedLayout layout) {
    	Label info = new Label(Messages.getString("Caption.Label.ChooseFields"));
    	info.setSizeUndefined();
		layout.addComponent(info);
		layout.setComponentAlignment(info, Alignment.TOP_CENTER);		
	}

	private void addField(GridLayout form, final Component field) {
		if (state.equals(State.MULTIUPDATE)) {
			final CheckBox enabler = new CheckBox();
			enabler.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(Property.ValueChangeEvent event) {
					field.setVisible(enabler.getValue());

					if (AbstractField.class.isAssignableFrom(field.getClass())) {
						if (enabler.getValue()) {
							formFields.add((AbstractField<?>) field);
						} else {
							formFields.remove((AbstractField<?>) field);
						}
					}
				}
			});
			field.setVisible(false);
			form.addComponent(enabler);
			form.setComponentAlignment(enabler, Alignment.TOP_LEFT);
		} else {
			if (AbstractField.class.isAssignableFrom(field.getClass())) {
				formFields.add((AbstractField<?>) field);
			}			
			Label caption = new Label(field.getCaption());
			caption.setSizeUndefined();
			form.addComponent(caption);
			form.setComponentAlignment(caption, Alignment.TOP_LEFT);			
		}

		field.setCaption(null);
		form.addComponent(field);
		form.setComponentAlignment(field, Alignment.TOP_LEFT);
	}
	
	private void setValidationVisible(boolean visible) {
		userForm.getIdField().setValidationVisible(visible);
		userForm.getUsernameField().setValidationVisible(visible);
		userForm.getGeneratedGroupField().setValidationVisible(visible);
		userForm.getGeneratedCountField().setValidationVisible(visible);
		userForm.getPasswordField().setValidationVisible(visible);
		userForm.getRolesField().setValidationVisible(visible);
		userForm.getEnabledField().setValidationVisible(visible);
		userForm.getExpireDateField().setValidationVisible(visible);
		userForm.getNoteField().setValidationVisible(visible);
		if (userForm.getGroupsField() != null) {
			userForm.getGroupsField().setValidationVisible(visible);
		}
		userForm.getPacksField().setValidationVisible(visible);
	}
	
	private String generateString(String characters, int length) {
		Random rng = new Random();
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

}
