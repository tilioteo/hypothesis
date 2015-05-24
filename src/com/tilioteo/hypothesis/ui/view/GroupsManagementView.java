package com.tilioteo.hypothesis.ui.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.tilioteo.hypothesis.persistence.GroupManager;
import com.tilioteo.hypothesis.persistence.PermissionManager;
import com.tilioteo.hypothesis.persistence.RoleManager;
import com.tilioteo.hypothesis.ui.window.GroupWindow;
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
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
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

@SuppressWarnings({ "serial", "unchecked" })
public class GroupsManagementView extends VerticalLayout
		implements View, ColumnGenerator, ConfirmDialog.Listener {

	PermissionManager permissionManager;
	GroupManager groupManager;
	//PersistenceManager persistenceManager;
	
	User loggedUser;

	CssLayout buttonGroup;
	Table table;
	
	ConfirmDialog deletionConfirmDialog;
	ConfirmDialog.Listener confirmDialogListener = this;
	
	boolean allGroupsSelected = false; 

	
	public GroupsManagementView() {
		permissionManager = PermissionManager.newInstance();
		groupManager = GroupManager.newInstance();
		//persistenceManager = PersistenceManager.newInstance();
		
		loggedUser = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
		
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

        Label title = new Label(Messages.getString("Caption.Label.GroupsManagement"));
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
            	GroupWindow groupWindow = new GroupWindow();
            	groupWindow.open();
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
            	allGroupsSelected = selectionType.getValue().equals(
            			Messages.getString("Caption.Item.All"));
            	MainEventBus.get().post(new HypothesisEvent.GroupSelectionChangedEvent());
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
            	Collection<Group> groups = getSelectedGroups();

        		GroupWindow groupWindow;
        		if (groups.size() == 1) {
        			groupWindow = new GroupWindow(groups.iterator().next());
        		} else {
        			groupWindow = new GroupWindow(groups);
        		}
            	groupWindow.open();
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
        		String question = allGroupsSelected ?
        				Messages.getString("Caption.Confirm.Group.DeleteAll") :
        				Messages.getString("Caption.Confirm.Group.DeleteSelected");

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
		
		String filename = Messages.getString("Caption.Export.GroupFileName");
		StreamResource resource = new StreamResource(
				source, filename);
		
		return resource;
	}

	protected InputStream getExportFile() {
		try {
			OutputStream output = new ByteArrayOutputStream();
			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);
	
			Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.GroupSheetName"));
	
			int rowNr = 0;
			Row row = sheet.createRow(rowNr++);
			sheet.createFreezePane(0, 1);
			
			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Id"));
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Name"));
	
			for (Iterator<Group> i = getSelectedGroups().iterator(); i.hasNext();) {
				row = sheet.createRow(rowNr++);
				Group group = i.next();
				row.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(group.getId());
				row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(group.getName());
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
		table.setSortContainerPropertyId(FieldConstants.NAME);
		
		BeanContainer<Long, Group> dataSource = new BeanContainer<Long, Group>(Group.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);

		List<Group> groups;
		if (loggedUser.hasRole(RoleManager.ROLE_SUPERUSER)) {
			groups = groupManager.findAll();
		} else {
			groups = groupManager.findOwnerGroups(loggedUser);
		}	
		for (Group group : groups) {
			group = groupManager.merge(group);
			//group = persistenceManager.merge(group);
			dataSource.addBean(group);
		}
		table.setContainerDataSource(dataSource);
		dataSource.setItemSorter(new CaseInsensitiveItemSorter());
		table.sort();

		table.addGeneratedColumn(FieldConstants.USERS, this);
        table.addGeneratedColumn(FieldConstants.AVAILABLE_PACKS, this);

    	table.setVisibleColumns(FieldConstants.ID,
				FieldConstants.NAME,
				FieldConstants.USERS,
				FieldConstants.AVAILABLE_PACKS,
				FieldConstants.NOTE);
        
    	table.setColumnHeaders(Messages.getString("Caption.Field.Id"),
    			Messages.getString("Caption.Field.Name"),
    			Messages.getString("Caption.Field.Users"),
    			Messages.getString("Caption.Field.AvailablePacks"),
    			Messages.getString("Caption.Field.Note"));
        
		table.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
            	MainEventBus.get().post(new HypothesisEvent.GroupSelectionChangedEvent());
            }
        });
        
        table.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                	Group group = ((BeanItem<Group>) event.getItem()).getBean();
                	GroupWindow groupWindow = new GroupWindow(group);
                	groupWindow.open();
                }
            }
        });

        return table;
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		if (columnId.equals(FieldConstants.USERS)) {
			Group group = ((BeanItem<Group>) source.getItem(itemId)).getBean();
			group = groupManager.merge(group);
			//group = persistenceManager.merge(group);
			
			Set<User> users = group.getUsers();
			List<String> sortedUsers = new ArrayList<String>();
			for (User user : users) {
				sortedUsers.add(user.getUsername());
			}
			Collections.sort(sortedUsers);
			Label usersLabel = new Label();
			
			StringBuilder descriptionBuilder = new StringBuilder();
			StringBuilder labelBuilder = new StringBuilder();
			for (String user : sortedUsers) {
				if (descriptionBuilder.length() != 0) {
					descriptionBuilder.append("<br/>");
					labelBuilder.append(", ");
			    }
				descriptionBuilder.append(user);
				labelBuilder.append(user);
			}
			usersLabel.setDescription(descriptionBuilder.toString());
			
			if (users.size() < 5) {
				usersLabel.setValue(labelBuilder.toString());
			} else {
				usersLabel.setValue(Messages.getString("Caption.Label.MultipleUsers", users.size()));
			}
			return usersLabel;
		}
		
		else if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
			Group group = ((BeanItem<Group>) source.getItem(itemId)).getBean();
			
			Set<Pack> packs = permissionManager.getGroupPacks(group);
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
	
	private void deleteGroups() {
		Collection<Group> groups = getSelectedGroups();
        
		for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext(); ) {
			Group group = iterator.next();
			group = groupManager.merge(group);
			Set<User> users = new HashSet<User>();
			for (User user : group.getUsers()) {
				users.add(user);
			}
			
			/*for (User user : users) {
				group.removeUser(user);
			}*/
			
			groupManager.delete(group);
			
			for (User user : users) {
        		if (user != null) {
        			MainEventBus.get().post(new HypothesisEvent.UserGroupsChangedEvent(user));
        		}
			}
			
			table.removeItem(group.getId());
		}
	}
	
	private Collection<Long> getSelectedGroupIds() {
		Collection<Long> userIds;
		if (allGroupsSelected) {
			userIds = (Collection<Long>) table.getItemIds();
		} else {
			userIds = (Collection<Long>) table.getValue();
		}
		return userIds;
	}
	
	private Collection<Group> getSelectedGroups() {
		Collection<Group> groups = new HashSet<Group>();
		for (Long id : getSelectedGroupIds()) {
			groups.add(((BeanItem<Group>) table.getItem(id)).getBean());
		}
		return groups;
	}
	
	@Subscribe
	public void addGroupIntoTable(final HypothesisEvent.GroupAddedEvent event) {
		Group group = event.getGroup();
		if (group != null) {
			BeanContainer<Long, Group> container =
					(BeanContainer<Long, Group>) table.getContainerDataSource();
			
			container.removeItem(group.getId());
			container.addItem(group.getId(), group);
			
			table.sort();
		}
	}
	
	@Subscribe
	public void changeGroupUsers(final HypothesisEvent.GroupUsersChangedEvent event) {
		Group group = event.getGroup();
		BeanContainer<Long, Group> container =
				(BeanContainer<Long, Group>) table.getContainerDataSource();
		
		container.removeItem(group.getId());
		container.addItem(group.getId(), group);
	}
	
	@Subscribe
	public void setToolsEnabled(final HypothesisEvent.GroupSelectionChangedEvent event) {
    	boolean itemsSelected = ((Set<Object>) table.getValue()).size() > 0;
    	boolean toolsEnabled = allGroupsSelected || itemsSelected; 
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
					deleteGroups();
					Notification.show(Messages.getString("Message.Info.GroupsDeleted"));
				
				} catch (Exception e) {
					Notification.show(Messages.getString("Message.Error.GroupsDeletion"),
							e.getMessage(),
							Notification.Type.ERROR_MESSAGE);
				}
			}
		}
	}

}
