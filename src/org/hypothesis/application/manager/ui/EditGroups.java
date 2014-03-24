package org.hypothesis.application.manager.ui;

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

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.Group;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.User;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

/**
 * The class represents groups management view
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class EditGroups extends VerticalLayout implements ColumnGenerator,
		Window.CloseListener {
	private static final long serialVersionUID = 2993020202674836228L;

	private Table groupsTable = ManagerApplication.getInstance().getGroupsTable();
	private NativeSelect groupsSelect;

	/**
	 * Constructor
	 */
	public EditGroups() {
		setMargin(true);

		// heading
		Label heading = new Label("<h2>"
				+ ApplicationMessages.get().getString(Messages.TEXT_EDIT_GROUPS_TITLE)
				+ "</h2>");
		heading.setContentMode(Label.CONTENT_XHTML);
		addComponent(heading);

		// main layout
		VerticalLayout content = new VerticalLayout();
		content.setWidth("100%");
		content.setSpacing(true);
		addComponent(content);

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setSpacing(true);
		content.addComponent(buttonBar);

		setGroupsTable();
		content.addComponent(groupsTable);

		Button addButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_ADD));
		addButton.addListener(Button.ClickEvent.class, this, "addButtonClick");

		groupsSelect = new NativeSelect();
		groupsSelect.addItem(ApplicationMessages.get()
				.getString(Messages.TEXT_SELECTED));
		groupsSelect.addItem(ApplicationMessages.get().getString(Messages.TEXT_ALL));
		groupsSelect.setNullSelectionAllowed(false);
		groupsSelect.setValue(ApplicationMessages.get().getString(
				Messages.TEXT_SELECTED));
		groupsSelect.setImmediate(true);

		Button updateButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_UPDATE));
		updateButton.addListener(Button.ClickEvent.class, this,
				"updateButtonClick");

		Button deleteButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_DELETE));
		deleteButton.addListener(Button.ClickEvent.class, this,
				"deleteButtonClick");

		Button exportButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_EXPORT));
		exportButton.addListener(Button.ClickEvent.class, this,
				"exportButtonClick");

		buttonBar.addComponent(addButton);
		buttonBar.addComponent(groupsSelect);
		buttonBar.addComponent(updateButton);
		buttonBar.addComponent(deleteButton);
		buttonBar.addComponent(exportButton);
	}

	public void addButtonClick(Button.ClickEvent event) {
		GroupWindow groupWindow = new GroupWindow();
		this.getWindow().addWindow(groupWindow);
		groupWindow.addListener(this);
	}

	public void deleteButtonClick(Button.ClickEvent event) {
		if (isEmptySelection()) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(
							Messages.TEXT_NO_GROUPS_SELECTED));
			return;
		}

		String question = groupsSelect.getValue().equals(
				ApplicationMessages.get().getString(Messages.TEXT_ALL)) ? ApplicationMessages
				.get().getString(Messages.TEXT_DELETE_ALL_GROUPS) : ApplicationMessages
				.get().getString(Messages.TEXT_DELETE_SELECTED_GROUPS);

		ConfirmDialog.show(getWindow(),
				ApplicationMessages.get().getString(Messages.TEXT_DELETE_CONFIRM),
				question, ApplicationMessages.get().getString(Messages.TEXT_YES),
				ApplicationMessages.get().getString(Messages.TEXT_NO),
				new ConfirmDialog.Listener() {
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							deleteGroups(getSelectedIds());
						}
					}
				});
	}

	@SuppressWarnings("unchecked")
	private void deleteGroups(Collection<Long> ids) {
		try {
			for (Long id : ids) {
				Group group = ((BeanItem<Group>) groupsTable.getItem(id))
						.getBean();
				ManagerApplication.getInstance().getPermissionManager().deleteGroupPermissions(group);
				ManagerApplication.getInstance().getUserGroupManager().deleteGroup(group);
				if (!group.getUsers().isEmpty()) {
					for (User user : group.getUsers()) {
						if (ManagerApplication.getInstance().getUsersSource()
								.getItem(user.getId()) != null) {
							ManagerApplication.getInstance().getUsersSource()
									.getItem(user.getId()).getBean()
									.removeGroup(group);
						}
					}
					ManagerApplication.getInstance().getUsersTable().sort();
				}
				groupsTable.removeItem(id);
			}
			if (ids.size() > 1) {
				getWindow().showNotification(
						ApplicationMessages.get().getString(
								Messages.INFO_GROUPS_DELETED));
			} else {
				getWindow().showNotification(
						ApplicationMessages.get()
								.getString(Messages.INFO_GROUP_DELETED));
			}
		} catch (HibernateException e) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(Messages.ERROR_DELETE_FAILED),
					Notification.TYPE_WARNING_MESSAGE);
		}
	}

	@SuppressWarnings("unchecked")
	public void exportButtonClick(Button.ClickEvent event) {
		if (isEmptySelection()) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(
							Messages.TEXT_NO_GROUPS_SELECTED));
			return;
		}

		try {
			OutputStream output = new ByteArrayOutputStream();
			WritableWorkbook workbook = Workbook.createWorkbook(output);

			WritableSheet sheet = workbook.createSheet(ApplicationMessages.get()
					.getString(Messages.TEXT_EXPORT_GROUPS_SHEET_NAME), 0);

			sheet.addCell(new jxl.write.Label(0, 0, ApplicationMessages.get()
					.getString(Messages.TEXT_ID)));
			sheet.addCell(new jxl.write.Label(1, 0, ApplicationMessages.get()
					.getString(Messages.TEXT_NAME)));

			int row = 1;
			for (Iterator<Long> i = (Iterator<Long>) groupsTable.getItemIds()
					.iterator(); i.hasNext();) {
				Long id = i.next();
				if (groupsTable.isSelected(id)
						|| groupsSelect.getValue().equals(
								ApplicationMessages.get().getString(Messages.TEXT_ALL))) {
					Group group = ((BeanItem<Group>) groupsTable.getItem(id))
							.getBean();
					sheet.addCell(new jxl.write.Number(0, row, group.getId()));
					sheet.addCell(new jxl.write.Label(1, row, group.getName()));
					row++;
				}
			}
			workbook.write();
			workbook.close();

			final InputStream input = new ByteArrayInputStream(
					((ByteArrayOutputStream) output).toByteArray());
			StreamResource.StreamSource streamSource = new StreamResource.StreamSource() {
				private static final long serialVersionUID = -5733377807459934501L;

				public InputStream getStream() {
					return input;
				}
			};

			String filename = ApplicationMessages.get().getString(
					Messages.TEXT_EXPORT_GROUPS_FILE_NAME);
			StreamResource resource = new StreamResource(streamSource,
					filename, ManagerApplication.getInstance());
			resource.getStream().setParameter("Content-Disposition",
					"attachment;filename=\"" + filename + "\"");
			ManagerApplication.getInstance().getMainWindow().open(resource);
		} catch (IOException e) {
			ManagerApplication.getInstance()
					.getMainWindow()
					.showError(
							ApplicationMessages.get().getString(
									Messages.ERROR_EXPORT_CANNOT_CREATE_FILE));
		} catch (RowsExceededException e) {
			ManagerApplication.getInstance()
					.getMainWindow()
					.showError(
							ApplicationMessages.get().getString(
									Messages.ERROR_EXPORT_ROWS_LIMIT_EXCEEDED));
		} catch (WriteException e) {
			ManagerApplication.getInstance()
					.getMainWindow()
					.showError(
							ApplicationMessages.get().getString(
									Messages.ERROR_EXPORT_CANNOT_WRITE_TO_FILE));
		}
	}

	/**
	 * Called by Table when a cell in a generated column needs to be generated.
	 * 
	 * @param source
	 *            - the source Table
	 * @param itemId
	 *            - the itemId (aka rowId) for the of the cell to be generated
	 * @param columnId
	 *            - the id for the generated column (as specified in
	 *            addGeneratedColumn)
	 */
	@SuppressWarnings("unchecked")
	public Component generateCell(Table source, Object itemId, Object columnId) {
		if (columnId.equals(FieldConstants.USERS)) {
			Set<User> users = (Set<User>) source.getItem(itemId)
					.getItemProperty("users").getValue();
			List<String> sortedUsers = new ArrayList<String>();
			for (User user : users) {
				sortedUsers.add(user.getUsername());
			}
			Collections.sort(sortedUsers);

			Label usersLabel = new Label();
			usersLabel.setDescription(StringUtils.join(sortedUsers, "<br/>"));
			if (users.size() == 0) {
				usersLabel.setValue(null);
			} else if (users.size() < 5) {
				usersLabel.setValue(StringUtils.join(sortedUsers, ", "));
			} else {
				usersLabel.setValue(String.format(
						ApplicationMessages.get().getString(
								Messages.TEXT_TOTAL_USERS_FMT), users.size()));
			}
			return usersLabel;
		}

		else if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
			Group group = ((BeanItem<Group>) source.getItem(itemId)).getBean();
			Set<Pack> packs = ManagerApplication.getInstance().getPermissionManager()
					.getGroupPacks(group);
			List<String> sortedPacks = new ArrayList<String>();
			List<String> sortedPackDescs = new ArrayList<String>();
			for (Pack pack : packs) {
				sortedPacks.add(String.format("%s (%d)", pack.getName(),
						pack.getId()));
				sortedPackDescs.add(String.format("%s (%d) - %s",
						pack.getName(), pack.getId(), pack.getDescription()));
			}
			Collections.sort(sortedPacks);
			Collections.sort(sortedPackDescs);

			Label packsLabel = new Label();
			packsLabel.setDescription(StringUtils
					.join(sortedPackDescs, "<br/>"));
			if (packs.size() == 0) {
				packsLabel.setValue(null);
			} else if (packs.size() < 5) {
				packsLabel.setValue(StringUtils.join(sortedPacks, ", "));
			} else {
				packsLabel.setValue(String.format(
						ApplicationMessages.get().getString(
								Messages.TEXT_TOTAL_PACKS_FMT), packs.size()));
			}
			return packsLabel;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Collection<Long> getSelectedIds() {
		if (groupsSelect.getValue().equals(
				ApplicationMessages.get().getString(Messages.TEXT_ALL))) {
			return (Collection<Long>) groupsTable.getItemIds();
		} else {
			return (Collection<Long>) groupsTable.getValue();
		}
	}

	@SuppressWarnings("unchecked")
	private Boolean isEmptySelection() {
		if (groupsSelect.getValue().equals(
				ApplicationMessages.get().getString(Messages.TEXT_ALL))) {
			return false;
		} else {
			return ((Set<Long>) groupsTable.getValue()).size() == 0;
		}
	}

	/**
	 * Set the table of groups
	 */
	public void setGroupsTable() {
		groupsTable = ManagerApplication.getInstance().getGroupsTable();

		groupsTable.setSelectable(true);
		groupsTable.setMultiSelect(true);
		groupsTable.setImmediate(true);
		groupsTable.setWidth("100%");
		groupsTable.setNullSelectionAllowed(true);
		groupsTable.setColumnCollapsingAllowed(true);
		groupsTable.setSortContainerPropertyId(FieldConstants.NAME);

		groupsTable.addGeneratedColumn(FieldConstants.USERS, this);
		groupsTable.addGeneratedColumn(FieldConstants.AVAILABLE_PACKS, this);

		groupsTable.setVisibleColumns(new String[] { FieldConstants.ID,
				FieldConstants.NAME, FieldConstants.USERS,
				FieldConstants.AVAILABLE_PACKS, FieldConstants.NOTE, });
		groupsTable.setColumnHeaders(new String[] {
				ApplicationMessages.get().getString(Messages.TEXT_ID),
				ApplicationMessages.get().getString(Messages.TEXT_NAME),
				ApplicationMessages.get().getString(Messages.TEXT_USER),
				ApplicationMessages.get().getString(Messages.TEXT_ENABLED_PACKS),
				ApplicationMessages.get().getString(Messages.TEXT_NOTE), });
	}

	@SuppressWarnings("unchecked")
	public void updateButtonClick(Button.ClickEvent event) {
		if (isEmptySelection()) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(
							Messages.TEXT_NO_GROUPS_SELECTED));
		} else {
			Set<Group> groups = new HashSet<Group>();
			for (Iterator<Long> i = (Iterator<Long>) groupsTable.getItemIds()
					.iterator(); i.hasNext();) {
				Long id = i.next();
				if (groupsTable.isSelected(id)
						|| groupsSelect.getValue().equals(
								ApplicationMessages.get().getString(Messages.TEXT_ALL))) {
					groups.add(((BeanItem<Group>) groupsTable.getItem(id))
							.getBean());
				}
			}
			GroupWindow groupWindow = new GroupWindow(groups);
			this.getWindow().addWindow(groupWindow);
			groupWindow.addListener(this);
		}
	}

	public void windowClose(CloseEvent event) {
		GroupWindow groupWindow = ((GroupWindow) event.getWindow());

		if (groupWindow.isSaved()) {
			if (groupWindow.isNewGroup()) {
				Group group = groupWindow.getGroup();
				ManagerApplication.getInstance().getGroupsSource().addBean(group);
				groupsTable.setValue(null);
				groupsTable.select(group.getId());
				groupsTable.setCurrentPageFirstItemId(group.getId());
				getWindow().showNotification(
						ApplicationMessages.get()
								.getString(Messages.INFO_GROUP_CREATED));
			} else {
				getWindow().showNotification(
						ApplicationMessages.get().getString(Messages.INFO_GROUP_SAVED));
			}

			groupsTable.sort();
			ManagerApplication.getInstance().getUsersTable().sort();
		}

		groupWindow = null;
	}

}
