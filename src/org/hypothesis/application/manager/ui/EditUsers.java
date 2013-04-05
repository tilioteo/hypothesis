package org.hypothesis.application.manager.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.core.FieldConstants;
import org.hypothesis.core.UserGroupManager;
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
 * The class represents users management view
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class EditUsers extends VerticalLayout implements ColumnGenerator,
		Window.CloseListener {
	private static final long serialVersionUID = -5976377276626210606L;

	private Table usersTable;
	private NativeSelect usersSelect;

	/**
	 * Constructor
	 */
	public EditUsers() {
		setMargin(true);

		// heading
		Label heading = new Label("<h2>"
				+ ApplicationMessages.get().getString(Messages.TEXT_EDIT_USERS_TITLE)
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

		setUsersTable();
		content.addComponent(usersTable);

		Button addButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_ADD));
		addButton.addListener(Button.ClickEvent.class, this, "addButtonClick");

		usersSelect = new NativeSelect();
		usersSelect
				.addItem(ApplicationMessages.get().getString(Messages.TEXT_SELECTED));
		usersSelect.addItem(ApplicationMessages.get().getString(Messages.TEXT_ALL));
		usersSelect.setNullSelectionAllowed(false);
		usersSelect.setValue(ApplicationMessages.get()
				.getString(Messages.TEXT_SELECTED));
		usersSelect.setImmediate(true);

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
		buttonBar.addComponent(usersSelect);
		buttonBar.addComponent(updateButton);
		buttonBar.addComponent(deleteButton);
		buttonBar.addComponent(exportButton);
	}

	public void addButtonClick(Button.ClickEvent event) {
		if (!ManagerApplication.getInstance().isCurrentUserInRole(
				UserGroupManager.ROLE_SUPERUSER)
				&& ManagerApplication.getInstance().getGroupsSource().size() == 0) {
			getWindow().showNotification(
					ApplicationMessages.get().getString(
							Messages.WARN_CREATE_GROUP_FIRST),
					Notification.TYPE_WARNING_MESSAGE);
		} else {
			UserWindow userWindow = new UserWindow();
			this.getWindow().addWindow(userWindow);
			userWindow.addListener(this);
		}
	}

	public void deleteButtonClick(Button.ClickEvent event) {
		if (isEmptySelection()) {
			getWindow().showNotification(
					ApplicationMessages.get()
							.getString(Messages.TEXT_NO_USERS_SELECTED));
			return;
		}

		String question = usersSelect.getValue().equals(
				ApplicationMessages.get().getString(Messages.TEXT_ALL)) ? ApplicationMessages
				.get().getString(Messages.TEXT_DELETE_ALL_USERS) : ApplicationMessages
				.get().getString(Messages.TEXT_DELETE_SELECTED_USERS);

		ConfirmDialog.show(getWindow(),
				ApplicationMessages.get().getString(Messages.TEXT_DELETE_CONFIRM),
				question, ApplicationMessages.get().getString(Messages.TEXT_YES),
				ApplicationMessages.get().getString(Messages.TEXT_NO),
				new ConfirmDialog.Listener() {
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							deleteUsers(getSelectedIds());
						}
					}
				});
	}

	@SuppressWarnings("unchecked")
	private void deleteUsers(Collection<Long> ids) {
		if (ManagerApplication.getInstance().isCurrentUserInRole(
				UserGroupManager.ROLE_SUPERUSER)) {
			boolean suLeft = false;
			for (Iterator<?> i = usersTable.getItemIds().iterator(); i
					.hasNext();) {
				Long id = (Long) i.next();
				if (!usersTable.isSelected(id)
						&& ManagerApplication
								.getInstance()
								.isUserInRole(
										((BeanItem<User>) usersTable
												.getItem(id)).getBean(),
										UserGroupManager.ROLE_SUPERUSER)) {
					suLeft = true;
					break;
				}
			}
			if (!suLeft) {
				getWindow().showNotification(
						ApplicationMessages.get().getString(
								Messages.WARN_AT_LEAST_ONE_SU),
						Notification.TYPE_WARNING_MESSAGE);
				return;
			}
		} else {
			for (Long id : ids) {
				User user = ((BeanItem<User>) usersTable.getItem(id)).getBean();
				if (user.getRoles().contains(UserGroupManager.ROLE_SUPERUSER)) {
					getWindow().showNotification(
							ApplicationMessages.get().getString(
									Messages.WARN_ONLY_SU_CAN_DELETE_SU),
							Notification.TYPE_WARNING_MESSAGE);
					return;
				}
			}
		}

		try {
			for (Long id : ids) {
				User user = ((BeanItem<User>) usersTable.getItem(id)).getBean();
				ManagerApplication.getInstance().getPermitionManager()
						.deleteUserPermitions(user);
				ManagerApplication.getInstance().getUserGroupManager().deleteUser(user);
				if (!user.getGroups().isEmpty()) {
					for (Group group : user.getGroups()) {
						if (ManagerApplication.getInstance().getGroupsSource()
								.getItem(group.getId()) != null) {
							ManagerApplication.getInstance().getGroupsSource()
									.getItem(group.getId()).getBean()
									.removeUser(user);
						}
					}
					ManagerApplication.getInstance().getGroupsTable().sort();
				}
				usersTable.removeItem(id);
			}
			if (ids.size() > 1) {
				getWindow().showNotification(
						ApplicationMessages.get()
								.getString(Messages.INFO_USERS_DELETED));
			} else {
				getWindow()
						.showNotification(
								ApplicationMessages.get().getString(
										Messages.INFO_USER_DELETED));
			}
			if (ids.contains(ManagerApplication.getInstance().getCurrentUser().getId())) {
				ManagerApplication.getInstance().closeApplication();
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
					ApplicationMessages.get()
							.getString(Messages.TEXT_NO_USERS_SELECTED));
			return;
		}

		try {
			OutputStream output = new ByteArrayOutputStream();
			WritableWorkbook workbook = Workbook.createWorkbook(output);

			WritableSheet sheet = workbook.createSheet(ApplicationMessages.get()
					.getString(Messages.TEXT_EXPORT_USERS_SHEET_NAME), 0);

			sheet.addCell(new jxl.write.Label(0, 0, ApplicationMessages.get()
					.getString(Messages.TEXT_ID)));
			sheet.addCell(new jxl.write.Label(1, 0, ApplicationMessages.get()
					.getString(Messages.TEXT_NAME)));
			sheet.addCell(new jxl.write.Label(2, 0, ApplicationMessages.get()
					.getString(Messages.TEXT_LABEL_PASSWORD)));
			/*
			 * sheet.addCell(new jxl.write.Label(3, 0,
			 * ApplicationMessages.get().getString(Messages.TEXT_ACTIVE)));
			 * sheet.addCell(new jxl.write.Label(4, 0,
			 * ApplicationMessages.get().getString(Messages.TEXT_EXPIRE_DATE)));
			 * sheet.addCell(new jxl.write.Label(5, 0,
			 * ApplicationMessages.get().getString(Messages.TEXT_NOTE)));
			 * sheet.addCell(new jxl.write.Label(6, 0,
			 * ApplicationMessages.get().getString(Messages.TEXT_ROLES)));
			 * sheet.addCell(new jxl.write.Label(7, 0,
			 * ApplicationMessages.get().getString(Messages.TEXT_GROUPS)));
			 */

			int row = 1;
			for (Iterator<Long> i = (Iterator<Long>) usersTable.getItemIds()
					.iterator(); i.hasNext();) {
				Long id = i.next();
				if (usersTable.isSelected(id)
						|| usersSelect.getValue().equals(
								ApplicationMessages.get().getString(Messages.TEXT_ALL))) {
					User user = ((BeanItem<User>) usersTable.getItem(id))
							.getBean();

					sheet.addCell(new jxl.write.Number(0, row, user.getId()));
					sheet.addCell(new jxl.write.Label(1, row, user
							.getUsername()));
					sheet.addCell(new jxl.write.Label(2, row, user
							.getPassword()));
					/*
					 * sheet.addCell(new jxl.write.Label(3, row,
					 * ApplicationMessages.get(). getString(user.getEnabled() ?
					 * Messages.TEXT_YES : Messages.TEXT_NO))); if
					 * (user.getExpireDate() != null) { sheet.addCell(new
					 * jxl.write.DateTime(4, row, user.getExpireDate())); }
					 * sheet.addCell(new jxl.write.Label(5, row,
					 * user.getNote())); sheet.addCell(new jxl.write.Label(6,
					 * row, user.getRoles().toString())); sheet.addCell(new
					 * jxl.write.Label(7, row, user.getGroups().toString()));
					 */
					row++;
				}
			}
			workbook.write();
			workbook.close();

			final InputStream input = new ByteArrayInputStream(
					((ByteArrayOutputStream) output).toByteArray());
			StreamResource.StreamSource streamSource = new StreamResource.StreamSource() {
				private static final long serialVersionUID = 2733554956404151531L;

				public InputStream getStream() {
					return input;
				}
			};

			String filename = ApplicationMessages.get().getString(
					Messages.TEXT_EXPORT_USERS_FILE_NAME);
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
		if (columnId.equals(FieldConstants.ROLES)) {
			String roles = source.getItem(itemId).getItemProperty("roles")
					.getValue().toString();
			return new Label(roles.substring(1, roles.length() - 1));
		}

		else if (columnId.equals(FieldConstants.GROUPS)) {
			Set<Group> groups = (Set<Group>) source.getItem(itemId)
					.getItemProperty("groups").getValue();
			List<String> sortedGroups = new ArrayList<String>();
			for (Group group : groups) {
				sortedGroups.add(group.getName());
			}
			Collections.sort(sortedGroups);
			Label groupsLabel = new Label();
			groupsLabel.setDescription(StringUtils.join(sortedGroups, "<br/>"));
			if (groups.size() == 0) {
				groupsLabel.setValue("");
			} else if (groups.size() < 5) {
				groupsLabel.setValue(StringUtils.join(sortedGroups, ", "));
			} else {
				groupsLabel
						.setValue(String.format(
								ApplicationMessages.get().getString(
										Messages.TEXT_TOTAL_GROUPS_FMT),
								groups.size()));
			}
			return groupsLabel;
		}

		else if (columnId.equals(FieldConstants.ENABLED)) {
			Boolean enabled = (Boolean) source.getItem(itemId)
					.getItemProperty("enabled").getValue();
			if (enabled) {
				return new Label(ApplicationMessages.get().getString(Messages.TEXT_YES));
			} else {
				return new Label(ApplicationMessages.get().getString(Messages.TEXT_NO));
			}
		}

		else if (columnId.equals(FieldConstants.EXPIRE_DATE)) {
			Date expireDate = (Date) source.getItem(itemId)
					.getItemProperty("expireDate").getValue();
			if (expireDate == null) {
				return null;
			} else {
				return new Label(
						new SimpleDateFormat(ApplicationMessages.get().getString(
								Messages.PATTERN_DATE_FORMAT))
								.format(expireDate));
			}
		}

		else if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
			User user = ((BeanItem<User>) source.getItem(itemId)).getBean();
			Set<Pack> packs = ManagerApplication.getInstance().getPermitionManager()
					.findUserPacks2(user, false);
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
		if (usersSelect.getValue().equals(
				ApplicationMessages.get().getString(Messages.TEXT_ALL))) {
			return (Collection<Long>) usersTable.getItemIds();
		} else {
			return (Collection<Long>) usersTable.getValue();
		}
	}

	@SuppressWarnings("unchecked")
	private Boolean isEmptySelection() {
		if (usersSelect.getValue().equals(
				ApplicationMessages.get().getString(Messages.TEXT_ALL))) {
			return false;
		} else {
			return ((Set<Long>) usersTable.getValue()).size() == 0;
		}
	}

	/**
	 * Set the table of users
	 */
	public void setUsersTable() {
		usersTable = ManagerApplication.getInstance().getUsersTable();

		usersTable.setSelectable(true);
		usersTable.setMultiSelect(true);
		usersTable.setImmediate(true);
		usersTable.setWidth("100%");
		usersTable.setNullSelectionAllowed(true);
		usersTable.setColumnCollapsingAllowed(true);
		usersTable.setSortContainerPropertyId(FieldConstants.USERNAME);

		usersTable.addGeneratedColumn(FieldConstants.ROLES, this);
		usersTable.addGeneratedColumn(FieldConstants.GROUPS, this);
		usersTable.addGeneratedColumn(FieldConstants.ENABLED, this);
		usersTable.addGeneratedColumn(FieldConstants.EXPIRE_DATE, this);
		usersTable.addGeneratedColumn(FieldConstants.AVAILABLE_PACKS, this);

		usersTable.setVisibleColumns(new String[] { FieldConstants.ID,
				FieldConstants.USERNAME, FieldConstants.PASSWORD,
				FieldConstants.ROLES, FieldConstants.GROUPS,
				FieldConstants.AVAILABLE_PACKS, FieldConstants.ENABLED,
				FieldConstants.EXPIRE_DATE, FieldConstants.NOTE, });
		usersTable.setColumnHeaders(new String[] {
				ApplicationMessages.get().getString(Messages.TEXT_ID),
				ApplicationMessages.get().getString(Messages.TEXT_NAME),
				ApplicationMessages.get().getString(Messages.TEXT_LABEL_PASSWORD),
				ApplicationMessages.get().getString(Messages.TEXT_ROLES),
				ApplicationMessages.get().getString(Messages.TEXT_GROUPS),
				ApplicationMessages.get().getString(Messages.TEXT_ENABLED_PACKS),
				ApplicationMessages.get().getString(Messages.TEXT_ACTIVE),
				ApplicationMessages.get().getString(Messages.TEXT_EXPIRE_DATE),
				ApplicationMessages.get().getString(Messages.TEXT_NOTE), });
		try {
			usersTable.setColumnCollapsed(FieldConstants.PASSWORD, true);
			usersTable.setColumnCollapsed(FieldConstants.ENABLED, true);
			usersTable.setColumnCollapsed(FieldConstants.EXPIRE_DATE, true);
		} catch (IllegalStateException e) {
			System.err.println("Columns collapsing not allowed");
		}
	}

	@SuppressWarnings("unchecked")
	public void updateButtonClick(Button.ClickEvent event) {
		if (isEmptySelection()) {
			getWindow().showNotification(
					ApplicationMessages.get()
							.getString(Messages.TEXT_NO_USERS_SELECTED));
		} else {
			Set<User> users = new HashSet<User>();
			for (Iterator<Long> i = (Iterator<Long>) usersTable.getItemIds()
					.iterator(); i.hasNext();) {
				Long id = i.next();
				if (usersTable.isSelected(id)
						|| usersSelect.getValue().equals(
								ApplicationMessages.get().getString(Messages.TEXT_ALL))) {
					users.add(((BeanItem<User>) usersTable.getItem(id))
							.getBean());
				}
			}
			UserWindow userWindow = new UserWindow(users);
			this.getWindow().addWindow(userWindow);
			userWindow.addListener(this);
		}
	}

	public void windowClose(CloseEvent event) {
		UserWindow userWindow = ((UserWindow) event.getWindow());

		if (userWindow.isSaved()) {
			if (userWindow.isNewUser()) {
				if (userWindow.getUsersCount() > 1) {
					getWindow().showNotification(
							ApplicationMessages.get().getString(
									Messages.INFO_USERS_CREATED));
				} else {
					getWindow().showNotification(
							ApplicationMessages.get().getString(
									Messages.INFO_USER_CREATED));
				}
			} else if (userWindow.isCurrentUserRoleChanged()) {
				getWindow().showNotification(
						ApplicationMessages.get().getString(Messages.INFO_USER_SAVED),
						ApplicationMessages.get().getString(
								Messages.WARN_USER_ROLE_CHANGED));
			} else {
				getWindow().showNotification(
						ApplicationMessages.get().getString(Messages.INFO_USER_SAVED));
			}

			usersTable.sort();
			ManagerApplication.getInstance().getGroupsTable().sort();
		}

		userWindow = null;
	}
}
