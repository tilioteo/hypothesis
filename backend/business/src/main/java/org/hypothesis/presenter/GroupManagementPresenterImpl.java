/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.cdi.NormalViewScoped;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hypothesis.business.SessionManager;
import org.hypothesis.common.IntSequence;
import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.interfaces.GroupService;
import org.hypothesis.data.interfaces.PermissionService;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.RoleServiceImpl;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.interfaces.GroupManagementPresenter;
import org.hypothesis.interfaces.GroupWindowPresenter;
import org.hypothesis.server.Messages;
import org.vaadin.dialogs.ConfirmDialog;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.io.*;
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
@NormalViewScoped
public class GroupManagementPresenterImpl extends AbstractManagementPresenter implements GroupManagementPresenter {

	@Inject
	private PermissionService permissionService;
	@Inject
	private GroupService groupService;

	@Inject
	private GroupWindowPresenter groupWindowPresenter;

	@Inject
	private Event<MainUIEvent> mainEvent;

	@Override
	public Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setSpacing(true);

		Label title = new Label(Messages.getString("Caption.Label.GroupsManagement"));
		title.addStyleName(ValoTheme.LABEL_HUGE);
		header.addComponent(title);
		header.addComponent(buildTools());
		header.setExpandRatio(title, 1);

		return header;
	}

	@Override
	protected Button buildAddButton() {
		final Button addButton = new Button(Messages.getString("Caption.Button.Add"));
		addButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		addButton.addClickListener(e -> groupWindowPresenter.showWindow());

		return addButton;
	}

	@Override
	protected ComboBox buildSelection() {
		final ComboBox selectionType = new ComboBox();
		selectionType.setTextInputAllowed(false);
		selectionType.setNullSelectionAllowed(false);

		selectionType.addItem(Messages.getString("Caption.Item.Selected"));
		selectionType.addItem(Messages.getString("Caption.Item.All"));
		selectionType.select(Messages.getString("Caption.Item.Selected"));

		selectionType.addValueChangeListener(e -> {
				allSelected = selectionType.getValue().equals(Messages.getString("Caption.Item.All"));
				mainEvent.fire(new MainUIEvent.GroupSelectionChangedEvent());
		});

		return selectionType;
	}

	@Override
	protected Button buildUpdateButton() {
		Button updateButton = new Button(Messages.getString("Caption.Button.Update"));
		updateButton.setClickShortcut(KeyCode.ENTER);
		updateButton.addClickListener(e -> {
				Collection<Group> groups = getSelectedGroups();

				if (groups.size() == 1) {
					groupWindowPresenter.showWindow(groups.iterator().next());
				} else {
					groupWindowPresenter.showWindow(groups);
				}
		});
		return updateButton;
	}

	@Override
	protected Button buildDeleteButton() {
		Button deleteButton = new Button(Messages.getString("Caption.Button.Delete"));
		deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
		deleteButton.addClickListener(e -> {
				String question = allSelected ? Messages.getString("Caption.Confirm.Group.DeleteAll")
						: Messages.getString("Caption.Confirm.Group.DeleteSelected");

				deletionConfirmDialog = ConfirmDialog.show(UI.getCurrent(),
						Messages.getString("Caption.Dialog.ConfirmDeletion"), question,
						Messages.getString("Caption.Button.Confirm"), Messages.getString("Caption.Button.Cancel"),
						GroupManagementPresenterImpl.this);
		});
		return deleteButton;
	}

	@Override
	protected Resource getExportResource() {
		StreamResource.StreamSource source = () -> getExportFile();

		return new StreamResource(source, Messages.getString("Caption.Export.GroupFileName"));
	}

	private InputStream getExportFile() {
		try {
			OutputStream output = new ByteArrayOutputStream();
			SXSSFWorkbook workbook = new SXSSFWorkbook(-1);

			Sheet sheet = workbook.createSheet(Messages.getString("Caption.Export.GroupSheetName"));

			final IntSequence seq = new IntSequence();
			Row row = sheet.createRow(seq.next());
			sheet.createFreezePane(0, 1);

			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Id"));
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(Messages.getString("Caption.Field.Name"));

			getSelectedGroups().forEach(e -> {
				Row r = sheet.createRow(seq.next());
				r.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(e.getId());
				r.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(e.getName());
			});

			workbook.write(output);
			workbook.close();
			output.close();

			return new ByteArrayInputStream(((ByteArrayOutputStream) output).toByteArray());

		} catch (IOException e) {
			Notification.show(Messages.getString("Message.Error.ExportCreateFile"), e.getMessage(), Type.ERROR_MESSAGE);
		}

		return null;
	}

	private void deleteGroups() {
		Collection<Group> groups = getSelectedGroups();

		for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
			Group group = iterator.next();
			group = groupService.merge(group);

			/*
			 * for (User user : users) { group.removeUser(user); }
			 */

			groupService.delete(group);

			group.getUsers().stream().filter(Objects::nonNull)
					.forEach(e -> mainEvent.fire(new MainUIEvent.UserGroupsChangedEvent(e)));

			table.removeItem(group.getId());
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Long> getSelectedGroupIds() {
		Collection<Long> userIds;
		if (allSelected) {
			userIds = (Collection<Long>) table.getItemIds();
		} else {
			userIds = (Collection<Long>) table.getValue();
		}
		return userIds;
	}

	@SuppressWarnings("unchecked")
	private Collection<Group> getSelectedGroups() {
		return getSelectedGroupIds().stream().map(m -> ((BeanItem<Group>) table.getItem(m)).getBean())
				.collect(Collectors.toSet());
		}

	@Override
	public Table buildTable() {
		table = new Table();
		table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setColumnCollapsingAllowed(true);
		table.setSortContainerPropertyId(FieldConstants.NAME);

		BeanContainer<Long, Group> dataSource = new BeanContainer<>(Group.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);

		User user = SessionManager.getLoggedUser();
		List<Group> groups;
		if (user.hasRole(RoleServiceImpl.ROLE_SUPERUSER)) {
			groups = groupService.findAll();
		} else {
			groups = groupService.findOwnerGroups(user);
		}
		groups.forEach(e -> {
			Group group = groupService.merge(e);
			dataSource.addBean(group);
		});
		table.setContainerDataSource(dataSource);
		dataSource.setItemSorter(new CaseInsensitiveItemSorter());
		table.sort();

		table.addGeneratedColumn(FieldConstants.USERS, this);
		table.addGeneratedColumn(FieldConstants.AVAILABLE_PACKS, this);

		table.setVisibleColumns(FieldConstants.ID, FieldConstants.NAME, FieldConstants.USERS,
				FieldConstants.AVAILABLE_PACKS, FieldConstants.NOTE);

		table.setColumnHeaders(Messages.getString("Caption.Field.Id"), Messages.getString("Caption.Field.Name"),
				Messages.getString("Caption.Field.Users"), Messages.getString("Caption.Field.AvailablePacks"),
				Messages.getString("Caption.Field.Note"));

		table.addValueChangeListener(e -> mainEvent.fire(new MainUIEvent.GroupSelectionChangedEvent()));

		table.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
			@SuppressWarnings("unchecked")
				Group group = ((BeanItem<Group>) e.getItem()).getBean();
					groupWindowPresenter.showWindow(group);
				}
		});

		return table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		if (columnId.equals(FieldConstants.USERS)) {
			Group group = ((BeanItem<Group>) source.getItem(itemId)).getBean();
			group = groupService.merge(group);

			Set<User> users = group.getUsers();
			// FIXME sort in stream
			List<String> sortedUsers = new ArrayList<>();
			users.stream().map(User::getUsername).forEach(sortedUsers::add);

			Collections.sort(sortedUsers);
			Label usersLabel = new Label();

			usersLabel.setDescription(sortedUsers.stream().collect(Collectors.joining("<br/>")));

			if (users.size() < 5) {
				usersLabel.setValue(sortedUsers.stream().collect(Collectors.joining(", ")));
			} else {
				usersLabel.setValue(Messages.getString("Caption.Label.MultipleUsers", users.size()));
			}
			return usersLabel;
		}

		else if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
			Group group = ((BeanItem<Group>) source.getItem(itemId)).getBean();

			Set<Pack> packs = permissionService.getGroupPacks(group);
			List<String> sortedPacks = new ArrayList<>();
			List<String> sortedPackDescs = new ArrayList<>();
			packs.forEach(e -> {
				sortedPacks.add(Messages.getString("Caption.Item.PackLabel", e.getName(), e.getId()));
				sortedPackDescs.add(
						Messages.getString("Caption.Item.PackDescription", e.getName(), e.getId(), e.getDescription()));
			});
			Collections.sort(sortedPacks);
			Collections.sort(sortedPackDescs);

			StringBuilder descriptionBuilder = new StringBuilder();
			descriptionBuilder.append("<ul>");
			descriptionBuilder.append(sortedPackDescs.stream().collect(Collectors.joining("", "<li>", "</li>")));
			descriptionBuilder.append("</ul>");

			Label packsLabel = new Label();
			packsLabel.setDescription(descriptionBuilder.toString());

			if (packs.size() < 5) {
				packsLabel.setValue(sortedPacks.stream().collect(Collectors.joining(", ")));
			} else {
				packsLabel.setValue(Messages.getString("Caption.Label.MultiplePacks", packs.size()));
			}

			return packsLabel;
		}

		return null;
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed() && dialog.equals(deletionConfirmDialog)) {
			try {
				deleteGroups();
				Notification.show(Messages.getString("Message.Info.GroupsDeleted"));

			} catch (Exception e) {
				Notification.show(Messages.getString("Message.Error.GroupsDeletion"), e.getMessage(),
						Notification.Type.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Make ui changes when new group added
	 * 
	 * @param event
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void addGroupIntoTable(@Observes final MainUIEvent.GroupAddedEvent event) {
		Group group = event.getGroup();
		if (group != null) {
			BeanContainer<Long, Group> container = (BeanContainer<Long, Group>) table.getContainerDataSource();

			container.removeItem(group.getId());
			container.addItem(group.getId(), group);

			table.sort();
		}
	}

	/**
	 * Make ui changes when group user changed
	 * 
	 * @param event
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void changeGroupUsers(@Observes final MainUIEvent.GroupUsersChangedEvent event) {
		Group group = event.getGroup();
		BeanContainer<Long, Group> container = (BeanContainer<Long, Group>) table.getContainerDataSource();

		container.removeItem(group.getId());
		container.addItem(group.getId(), group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.presenter.GroupManagementPresenter#setToolsEnabled(org.
	 * hypothesis.event.interfaces.MainUIEvent.GroupSelectionChangedEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setToolsEnabled(@Observes final MainUIEvent.GroupSelectionChangedEvent event) {
		boolean itemsSelected = !((Set<Object>) table.getValue()).isEmpty();
		boolean toolsEnabled = allSelected || itemsSelected;
		buttonGroup.setEnabled(toolsEnabled);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		// nop
	}
}
