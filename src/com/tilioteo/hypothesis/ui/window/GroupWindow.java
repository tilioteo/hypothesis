package com.tilioteo.hypothesis.ui.window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.tilioteo.hypothesis.core.CaseInsensitiveItemSorter;
import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.entity.FieldConstants;
import com.tilioteo.hypothesis.entity.Group;
import com.tilioteo.hypothesis.entity.GroupPermission;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.tilioteo.hypothesis.persistence.GroupManager;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.PersistenceManager;
import com.tilioteo.hypothesis.persistence.RoleManager;
import com.tilioteo.hypothesis.persistence.UserManager;
import com.tilioteo.hypothesis.ui.form.GroupFormFields;
import com.tilioteo.hypothesis.ui.form.validator.GroupNameValidator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings({ "serial", "unchecked" })
public class GroupWindow extends Window {
	
	private enum State { CREATE, UPDATE, MULTIUPDATE };

	private Object source = null;
	private State state; 
	
	private User loggedUser;
	private GroupFormFields groupForm;
	private GroupManager groupManager;
	private UserManager userManager;
	private PermissionManager permissionManager;
	
	private PersistenceManager persistenceManager;
	
	private TabSheet tabSheet;
	private Component groupDetailsTab;

	private ArrayList<AbstractField<?>> formFields;
	
	
	public GroupWindow() {
		source = null;
		state = State.CREATE;
    }
	
	public GroupWindow(Group group) {
		source = group;
		state = State.UPDATE;
	}
	
	public GroupWindow(Collection<Group> groups) {
		source = groups;
		state = State.MULTIUPDATE;
	}
	
	public void open() {
        MainEventBus.get().post(new HypothesisEvent.CloseOpenWindowsEvent());
        UI.getCurrent().addWindow(this);
        this.focus();

        loggedUser = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
        groupManager = GroupManager.newInstance();
        userManager = UserManager.newInstance();
        permissionManager = PermissionManager.newInstance();
        groupForm = new GroupFormFields();
        
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
        	fillFields((Group) source);
        }
        
        buildContent();
    }
	
	private void initFields() {
		formFields = new ArrayList<AbstractField<?>>();
		
		// ID
		groupForm.buildIdField();
        
		// name
		groupForm.buildNameField();
		if (state.equals(State.CREATE)) {
			groupForm.getNameField().addValidator(
					new GroupNameValidator(null));
		} else if (state.equals(State.UPDATE)) {
			groupForm.getNameField().addValidator(
					new GroupNameValidator(((Group) source).getId()));		
		}
        
		// note
        groupForm.buildNoteField();
        
        // users
		Collection<User> users;

		if (loggedUser.hasRole(RoleManager.ROLE_SUPERUSER)) {
			users = userManager.findAll();
		} else {
			users = userManager.findOwnerUsers(loggedUser);
		}
		
		if (!users.isEmpty()) {
	        AbstractField<?> usersField = groupForm.buildUsersField(
					!loggedUser.hasRole(RoleManager.ROLE_SUPERUSER));

	        for (User user : users) {
				Table table = (Table) usersField;
				table.addItem(user);
				Item row = table.getItem(user);
				row.getItemProperty(FieldConstants.USERNAME).
						setValue(user.getUsername());
			}
			
			((IndexedContainer) ((Table) usersField).
					getContainerDataSource()).setItemSorter(
							new CaseInsensitiveItemSorter());
			((Table) usersField).sort(
					new Object[] { FieldConstants.USERNAME },
					new boolean[] { true });
		}
		
		// enabled packs
		AbstractField<?> packsField = groupForm.buildPacksField();
		
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

	private void fillFields(Group group) {
		//group = groupManager.merge(group);
		group = persistenceManager.merge(group);
		
		groupForm.getIdField().setValue(group.getId().toString());
		groupForm.getNameField().setValue(group.getName());
		groupForm.getNoteField().setValue(group.getNote());
		
		// users
		if (groupForm.getUsersField() != null) {
			Set<User> users;
			
			if (state.equals(State.UPDATE)) {
				users = group.getUsers();
			} else {
				users = new HashSet<User>();
			}
			
			AbstractSelect usersField = ((AbstractSelect) groupForm.getUsersField());
			for (Object itemId : usersField.getItemIds()) {
				Item row = usersField.getItem(itemId);
				User user = (User) itemId;
	
				if (users.contains(user)) {
					row.getItemProperty(FieldConstants.SELECTED).setValue(true);
				} else {
					row.getItemProperty(FieldConstants.SELECTED).setValue(false);
				}
			}
		}
		
		// packs
		Set<Pack> packs;
		
		if (state.equals(State.UPDATE)) {
			packs = permissionManager.getGroupPacks(group);
		} else {
			packs = new HashSet<Pack>();
		}
		
		for (Object itemId : groupForm.getPacksField().getItemIds()) {
			Item row = groupForm.getPacksField().getItem(itemId);
			Pack pack = (Pack) itemId;

			if (packs.contains(pack)) {
				row.getItemProperty(FieldConstants.SELECTED).setValue(true);
			} else {
				row.getItemProperty(FieldConstants.SELECTED).setValue(false);
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
        panel.addStyleName("borderless");
        panel.setContent(buildGroupDetailsForm());
		tab.addComponent(panel);
		
		groupDetailsTab = tab;
		
	    return tab;
	}
	
	private Component buildGroupDetailsForm() {
		VerticalLayout layout = new VerticalLayout();
		
        if (state.equals(State.MULTIUPDATE)) {
			addInformationLabel(layout);
		}
        
		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);

		if (state.equals(State.UPDATE)) {
			addField(form, groupForm.getIdField());
        }
        
        if (!(state.equals(State.MULTIUPDATE))) {
			addField(form, groupForm.getNameField());
		}
		
		addField(form, groupForm.getNoteField());

		if (groupForm.getUsersField() != null) {
			addField(form, groupForm.getUsersField());
		} else {
			// TODO: upozornit, ze uzivatel nema zadne uzivatele?
		}
		
		layout.addComponent(form);
	    
		return layout;
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
        panel.addStyleName("borderless");
        panel.setContent(buildGroupPacksForm());
		tab.addComponent(panel);

	    return tab;
	}

	private Component buildGroupPacksForm() {
		VerticalLayout layout = new VerticalLayout();
		
        if (state.equals(State.MULTIUPDATE)) {
			addInformationLabel(layout);
		}
        
		GridLayout form = new GridLayout();
		form.setColumns(2);
		form.setMargin(true);
		form.setSpacing(true);
		
		addField(form, groupForm.getPacksField());
		
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
                	if (state.equals(State.CREATE)) {
                		success = new Notification(Messages.getString("Message.Info.GroupAdded"));
                	} else if (state.equals(State.UPDATE)) {
                		success = new Notification(Messages.getString("Message.Info.GroupUpdated"));
                	} else {
                		success = new Notification(Messages.getString("Message.Info.GroupsUpdated"));               		
                	}
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
				tabSheet.setSelectedTab(groupDetailsTab);
				throw new CommitException(e.getMessage());
			}
		}
        
        if (state.equals(State.MULTIUPDATE)) {
        	for (Group group : (Collection<Group>) source) {
        		group = saveGroup(group);
                MainEventBus.get().post(
                		new HypothesisEvent.GroupAddedEvent(group));
        	}
        
        } else {
        	Group group;
        	if (state.equals(State.CREATE)) {
        		group = new Group();
        	} else {
        		group = (Group) source;
        	}
            group = saveGroup(group);
            MainEventBus.get().post(new HypothesisEvent.GroupAddedEvent(group));
        }
	}

	private Group saveGroup(Group group) {
		
		if (state.equals(State.CREATE)) {
        	group.setOwnerId(loggedUser.getId());
        } else {
    		//group = groupManager.merge(group);
    		group = persistenceManager.merge(group);
        }
		
		if (!(state.equals(State.MULTIUPDATE))) {
			group.setName(groupForm.getNameField().getValue());
		}
		
		if (groupForm.getNoteField().isVisible()) {
			group.setNote(groupForm.getNoteField().getValue());
		}
		
		if (groupForm.getUsersField() != null && 
				groupForm.getUsersField().isVisible() && 
				groupForm.getUsersField().isEnabled()) {
			AbstractSelect usersField =
					(AbstractSelect) groupForm.getUsersField();
			
			for (Object itemId : usersField.getItemIds()) {
				Item item = usersField.getItem(itemId);
				User user = (User) itemId;
				Boolean selected = (Boolean) item.getItemProperty(
						FieldConstants.SELECTED).getValue();

				if (selected == null) {
					if (state.equals(State.MULTIUPDATE)) {
						group.removeUser(user);
					}
				} else if (selected.equals(true)) {
					group.addUser(user);
				} else if (selected.equals(false)) {
					group.removeUser(user);
				}

				MainEventBus.get().post(new HypothesisEvent.
						UserGroupsChangedEvent(user));
			}
		}
		
		group = groupManager.add(group);
		
		if (groupForm.getPacksField().isVisible()) {
	        permissionManager.deleteGroupPermissions(group);

			for (Object itemId : groupForm.getPacksField().getItemIds()) {
				Item item = groupForm.getPacksField().getItem(itemId);
				Pack pack = (Pack) itemId;
				Boolean selected = (Boolean) item.getItemProperty(
						FieldConstants.SELECTED).getValue();
				
				if (selected != null && selected.equals(true)) {
					permissionManager.addGroupPermission(
							new GroupPermission(group, pack));
				}
			}
		}
		
		return group;
	}

	private void addInformationLabel(AbstractOrderedLayout layout) {
    	Label info = new Label(Messages.getString("Caption.Label.ChooseFields"));
    	info.setSizeUndefined();
		layout.addComponent(info);
		layout.setComponentAlignment(info, Alignment.TOP_CENTER);		
	}

	private void addField(GridLayout form, final Component field) {
		if (state.equals(State.MULTIUPDATE)) {
			final CheckBox enabler = new CheckBox(field.getCaption());
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
		groupForm.getIdField().setValidationVisible(visible);
		groupForm.getNameField().setValidationVisible(visible);
		groupForm.getNoteField().setValidationVisible(visible);
		if (groupForm.getUsersField() != null) {
			groupForm.getUsersField().setValidationVisible(visible);
		}
		groupForm.getPacksField().setValidationVisible(visible);
	}

}
