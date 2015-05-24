package com.tilioteo.hypothesis.ui.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.vaadin.dialogs.ConfirmDialog;

import com.google.common.eventbus.Subscribe;
import com.tilioteo.hypothesis.core.CaseInsensitiveItemSorter;
import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.entity.FieldConstants;
import com.tilioteo.hypothesis.entity.Group;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Role;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.tilioteo.hypothesis.persistence.GroupService;
import com.tilioteo.hypothesis.persistence.PermissionService;
import com.tilioteo.hypothesis.persistence.RoleService;
import com.tilioteo.hypothesis.persistence.UserService;
import com.tilioteo.hypothesis.server.SessionUtils;
import com.tilioteo.hypothesis.ui.window.UserWindow;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({"serial", "unchecked"})
public class UsersManagementView extends VerticalLayout
		implements View, ColumnGenerator, ConfirmDialog.Listener {

	private PermissionService permissionService;
	private UserService userService;
	private GroupService groupService;
	//private PersistenceService persistenceService;
	
	private User loggedUser;

	private CssLayout buttonGroup;
	private Table table;
	
	private ConfirmDialog deletionConfirmDialog;
	private ConfirmDialog.Listener confirmDialogListener = this;
	
	boolean allUsersSelected = false; 
	
	public UsersManagementView() {
		permissionService = PermissionService.newInstance();
		userService = UserService.newInstance();
		groupService = GroupService.newInstance();
		//persistenceService = PersistenceService.newInstance();
		
		loggedUser = SessionUtils.getAttribute(User.class);
		
		MainEventBus.get().register(this);
		
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		addComponent(buildHeader());
		table = buildTable();
		addComponent(table);
		setExpandRatio(table, 1);
	}
	
	private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setSpacing(true);

        Label title = new Label(Messages.getString("Caption.Label.UsersManagement"));
        title.addStyleName("huge");
        header.addComponent(title);
        header.addComponent(buildTools());
        header.setExpandRatio(title, 1);

        return header;
    }
	
	private Component buildTools() {
		HorizontalLayout tools = new HorizontalLayout();
		tools.setSpacing(true);
        
        tools.addComponent(buildAddButton());
        tools.addComponent(buildSelection());
        
        buttonGroup = new CssLayout();
        buttonGroup.addStyleName("v-component-group");
        buttonGroup.addComponent(buildUpdateButton());
        buttonGroup.addComponent(buildDeleteButton());
        buttonGroup.addComponent(buildExportButton());
        buttonGroup.setEnabled(false);
        tools.addComponent(buttonGroup);
        
        return tools;
    }
	
	private Component buildAddButton() {
		final Button addButton = new Button(Messages.getString("Caption.Button.Add"));
		addButton.addStyleName("friendly");
		addButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	if (!loggedUser.hasRole(RoleService.ROLE_SUPERUSER) && 
            			groupService.findOwnerGroups(loggedUser).size() == 0) {
            		Notification.show(Messages.getString("Message.Error.CreateGroup"), Type.WARNING_MESSAGE);
            	} else {
            		UserWindow userWindow = new UserWindow();
            		userWindow.open();
            	}
            }
        });
		return addButton;
	}

	private Component buildSelection() {
        final ComboBox selectionType = new ComboBox();
        selectionType.setTextInputAllowed(false);
        selectionType.setNullSelectionAllowed(false);
        
        selectionType.addItem(Messages.getString("Caption.Item.Selected"));
        selectionType.addItem(Messages.getString("Caption.Item.All"));
        selectionType.select(Messages.getString("Caption.Item.Selected"));

        selectionType.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
            	allUsersSelected = selectionType.getValue().equals(
            			Messages.getString("Caption.Item.All"));
            	MainEventBus.get().post(new HypothesisEvent.UserSelectionChangedEvent());
            }
        });

        return selectionType;
	}

	private Component buildUpdateButton() {
		Button updateButton = new Button(Messages.getString("Caption.Button.Update"));
		updateButton.setClickShortcut(KeyCode.ENTER);
        updateButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
        		Collection<User> users = getSelectedUsers();

        		UserWindow userWindow;
        		if (users.size() == 1) {
        			userWindow = new UserWindow(users.iterator().next());
        		} else {
        			userWindow = new UserWindow(users);
        		}
            	userWindow.open();
            }
        });
		return updateButton;
	}
	
	private Component buildDeleteButton() {
		Button deleteButton = new Button(Messages.getString("Caption.Button.Delete"));
		deleteButton.addStyleName("danger");
		deleteButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
        		String question = allUsersSelected ?
        				Messages.getString("Caption.Confirm.User.DeleteAll") :
        				Messages.getString("Caption.Confirm.User.DeleteSelected");

        		deletionConfirmDialog = ConfirmDialog.show(getUI(),
        				Messages.getString("Caption.Dialog.ConfirmDeletion"),
        				question,
        				Messages.getString("Caption.Button.Confirm"),
        				Messages.getString("Caption.Button.Cancel"),
        				confirmDialogListener);
            }
        });
		return deleteButton;
	}

	private Component buildExportButton() {
		Button exportButton = new Button(Messages.getString("Caption.Button.Export"));
		Resource exportResource = getExportResource();
		FileDownloader fileDownloader = new FileDownloader(exportResource);
        fileDownloader.extend(exportButton);
		return exportButton;
	}
	
	private Resource getExportResource() {
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
			@Override
			public InputStream getStream() {
				return getExportFile();
    		}
		};
		
		String filename = Messages.getString("Caption.Export.UserFileName");
		StreamResource resource = new StreamResource(
				source, filename);
		
		return resource;
	}

	protected InputStream getExportFile() {
		try {
			OutputStream output = new ByteArrayOutputStream();
			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);
			
			Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.UserSheetName"));
	
			int rowNr = 0;
			Row row = sheet.createRow(rowNr++);
			sheet.createFreezePane(0, 1);
			
			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Id"));
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Name"));
			row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Password"));
	
			for (Iterator<User> i = getSelectedUsers().iterator(); i.hasNext();) {
				row = sheet.createRow(rowNr++);
				User user = i.next();
				row.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(user.getId());
				row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(user.getUsername());
				row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(user.getPassword());
			}
			workbook.write(output);
			workbook.close();
			output.close();

			return new ByteArrayInputStream(
					((ByteArrayOutputStream) output).toByteArray());
	
		} catch (IOException e) {
			Notification.show(Messages.getString("Message.Error.ExportCreateFile"),
					e.getMessage(), Type.ERROR_MESSAGE);
		}
		
		return null;
	}
	
	private Table buildTable() {
		table = new Table();
		table.setSizeFull();
		table.addStyleName("small");
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
			//user = persistenceService.merge(user);
			dataSource.addBean(user);
		}
		table.setContainerDataSource(dataSource);
		dataSource.setItemSorter(new CaseInsensitiveItemSorter());
		table.sort();

		table.addGeneratedColumn(FieldConstants.ROLES, this);
        table.addGeneratedColumn(FieldConstants.GROUPS, this);
        table.addGeneratedColumn(FieldConstants.ENABLED, this);
		table.addGeneratedColumn(FieldConstants.EXPIRE_DATE, this);
		table.addGeneratedColumn(FieldConstants.AVAILABLE_PACKS, this);

		table.setColumnCollapsed(FieldConstants.PASSWORD, true);
    	table.setColumnCollapsed(FieldConstants.ENABLED, true);
    	table.setColumnCollapsed(FieldConstants.EXPIRE_DATE, true);

    	table.setVisibleColumns(FieldConstants.ID,
    			FieldConstants.USERNAME,
    			FieldConstants.PASSWORD,
    			FieldConstants.ROLES,
    			FieldConstants.GROUPS,
    			FieldConstants.AVAILABLE_PACKS,
    			FieldConstants.ENABLED,
    			FieldConstants.EXPIRE_DATE,
    			FieldConstants.NOTE);

    	table.setColumnHeaders(Messages.getString("Caption.Field.Id"),
    			Messages.getString("Caption.Field.Username"),
    			Messages.getString("Caption.Field.Password"),
    			Messages.getString("Caption.Field.Role"),
    			Messages.getString("Caption.Field.Groups"),
    			Messages.getString("Caption.Field.AvailablePacks"),
    			Messages.getString("Caption.Field.Enabled"),
    			Messages.getString("Caption.Field.ExpireDate"),
    			Messages.getString("Caption.Field.Note"));
        
		table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
            	MainEventBus.get().post(new HypothesisEvent.UserSelectionChangedEvent());
            }
        });
        
        table.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                	User user = ((BeanItem<User>) event.getItem()).getBean();
                	UserWindow userWindow = new UserWindow(user);
                	userWindow.open();
                }
            }
        });

        return table;
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		if (columnId.equals(FieldConstants.ROLES)) {
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();
			user = userService.merge(user);
			//user = persistenceService.merge(user);
			
			Set<Role> roles = user.getRoles();
			List<String> sortedRoles = new ArrayList<String>();
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
		
		else if (columnId.equals(FieldConstants.GROUPS)) {
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();
			user = userService.merge(user);
			//user = persistenceService.merge(user);
			
			Set<Group> groups = user.getGroups();
			List<String> sortedGroups = new ArrayList<String>();
			for (Group group : groups) {
				sortedGroups.add(group.getName());
			}
			Collections.sort(sortedGroups);
			Label groupsLabel = new Label();
			
			StringBuilder descriptionBuilder = new StringBuilder();
			StringBuilder labelBuilder = new StringBuilder();
			for (String group : sortedGroups) {
				if (descriptionBuilder.length() != 0) {
					descriptionBuilder.append("<br/>");
					labelBuilder.append(", ");
			    }
				descriptionBuilder.append(group);
				labelBuilder.append(group);
			}
			groupsLabel.setDescription(descriptionBuilder.toString());
			
			if (groups.size() < 5) {
				groupsLabel.setValue(labelBuilder.toString());
			} else {
				groupsLabel.setValue(Messages.getString("Caption.Label.MultipleGroups", groups.size()));
			}
			
			return groupsLabel;
		}
		
		else if (columnId.equals(FieldConstants.ENABLED)) {
			Boolean enabled = (Boolean) source.getItem(itemId).getItemProperty(columnId).getValue();
			if (enabled) {
				return new Label(FontAwesome.CHECK.getHtml(), ContentMode.HTML);
			} else {
				return new Label(FontAwesome.TIMES.getHtml(), ContentMode.HTML);
			}
		}

		else if (columnId.equals(FieldConstants.EXPIRE_DATE)) {
			Property<?> property = source.getItem(itemId).getItemProperty(columnId);
			if (property != null) {
				Date expireDate = (Date) property.getValue();
				if (expireDate == null) {
					return null;
				} else {
					DateFormat date = new SimpleDateFormat(Messages.getString("Format.Date"));
					return new Label(date.format(expireDate));
				}
			} else {
				return null;
			}
		}

		else if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();
			
			Set<Pack> packs = permissionService.findUserPacks2(user, false);
			List<String> sortedPacks = new ArrayList<String>();
			List<String> sortedPackDescs = new ArrayList<String>();
			for (Pack pack : packs) {
				sortedPacks.add(Messages.getString(
						"Caption.Item.PackLabel", pack.getName(), pack.getId()));
				sortedPackDescs.add(Messages.getString("Caption.Item.PackDescription",
						pack.getName(), pack.getId(), pack.getDescription()));
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
	
	private void deleteUsers() {
		Collection<User> users = getSelectedUsers();
        
		checkDeletionPermission(loggedUser, users);
		checkSuperuserLeft(loggedUser);
		
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext(); ) {
			User user = iterator.next();
			//user = userService.merge(user);

			userService.delete(user);
			
			for (Group group : user.getGroups()) {
				if (group != null) {
					MainEventBus.get().post(new HypothesisEvent.GroupUsersChangedEvent(group));
				}
			}
			
			table.removeItem(user.getId());
		}
		
		if (users.contains(loggedUser)) {
			MainEventBus.get().post(new HypothesisEvent.UserLoggedOutEvent());
		}
	}
	
	private void checkDeletionPermission(User currentUser,
			Collection<User> users) {
		String exceptionMessage = Messages.getString("Message.Error.SuperuserDelete");
		
		if (currentUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			return;
		}
		
		if (allUsersSelected) {
        	throw new UnsupportedOperationException(exceptionMessage);
        }
		
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext(); ) {
			User user = iterator.next();
			if (user.hasRole(RoleService.ROLE_SUPERUSER)) {
				throw new UnsupportedOperationException(exceptionMessage);
			}
		}
	}

	private void checkSuperuserLeft(User currentUser) {
		String exceptionMessage = Messages.getString("Message.Error.SuperuserLeft");
		
        if (allUsersSelected) {
        	throw new UnsupportedOperationException(exceptionMessage);
        }

        if (currentUser.hasRole(RoleService.ROLE_SUPERUSER)) {
			boolean superuserLeft = false;
			for (Iterator<Long> iterator = ((Collection<Long>)
					table.getItemIds()).iterator(); iterator.hasNext(); ) {
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

	private Collection<Long> getSelectedUserIds() {
		Collection<Long> userIds;
		if (allUsersSelected) {
			userIds = (Collection<Long>) table.getItemIds();
		} else {
			userIds = (Collection<Long>) table.getValue();
		}
		return userIds;
	}
	
	private Collection<User> getSelectedUsers() {
		Collection<User> users = new HashSet<User>();
		for (Long id : getSelectedUserIds()) {
			users.add(((BeanItem<User>) table.getItem(id)).getBean());
		}
		return users;
	}
	
	@Subscribe
	public void addUserIntoTable(final HypothesisEvent.UserAddedEvent event) {
		User user = event.getUser();
		BeanContainer<Long, User> container =
				(BeanContainer<Long, User>) table.getContainerDataSource();
		
		container.removeItem(user.getId());
		container.addItem(user.getId(), user);
		
		table.sort();
	}
	
	@Subscribe
	public void changeUserGroups(final HypothesisEvent.UserGroupsChangedEvent event) {
		User user = event.getUser();
		BeanContainer<Long, User> container =
				(BeanContainer<Long, User>) table.getContainerDataSource();
		
		container.removeItem(user.getId());
		container.addItem(user.getId(), user);
	}
	
	@Subscribe
	public void setToolsEnabled(final HypothesisEvent.UserSelectionChangedEvent event) {
    	boolean itemsSelected = ((Set<Object>) table.getValue()).size() > 0;
    	boolean toolsEnabled = allUsersSelected || itemsSelected; 
    	buttonGroup.setEnabled(toolsEnabled);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			if (dialog.equals(deletionConfirmDialog)) {
				try {
					deleteUsers();
					Notification.show(Messages.getString("Message.Info.UsersDeleted"));
				
				} catch (Exception e) {
					Notification.show(Messages.getString("Message.Error.UsersDeletion"),
							e.getMessage(), Notification.Type.ERROR_MESSAGE);
				}
			}
		}
	}
}
