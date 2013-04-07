package org.hypothesis.application.manager.ui;

import org.hibernate.HibernateException;
import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.constants.FieldConstants;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.User;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * The class represents personal data editor
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class EditPersonalData extends VerticalLayout {
	private static final long serialVersionUID = -7417353725893080508L;

	private Form form;

	/**
	 * Constructor
	 * 
	 * @param manager
	 *            - manager application
	 */
	public EditPersonalData() {
		setMargin(true);

		// heading
		Label heading = new Label("<h2>"
				+ ApplicationMessages.get().getString(
						Messages.TEXT_EDIT_PERSONAL_DATA_TITLE) + "</h2>");
		heading.setContentMode(Label.CONTENT_XHTML);
		addComponent(heading);

		// form
		setForm();
		addComponent(form);

		// notes
		addComponent(new Label(ApplicationMessages.get().getString(
				Messages.TEXT_EDIT_PERSONAL_DATA_INFO)));
	}

	/**
	 * Called when a Button has been clicked
	 * 
	 * @param event
	 *            - an event containing information about the click
	 */
	@SuppressWarnings("unchecked")
	public void saveButtonClicked(Button.ClickEvent event) {
		try {
			form.commit();

			BeanItem<User> userItem = (BeanItem<User>) form.getItemDataSource();
			User user = userItem.getBean();

			try {
				ManagerApplication.getInstance().getUserGroupManager().addUser(user);
				ManagerApplication.getInstance().refreshCurrentUser(userItem);
				getWindow().showNotification(
						ApplicationMessages.get().getString(
								Messages.INFO_PERSONAL_DATA_SAVED));
			} catch (HibernateException e) {
				getWindow()
						.showNotification(
								ApplicationMessages.get().getString(
										Messages.ERROR_SAVE_FAILED),
								Notification.TYPE_ERROR_MESSAGE);
			}
		} catch (InvalidValueException e) {
			// nevalidni data ve formulari, formular si obsatara prislusne
			// zobrazeni varovani
		}
	}

	/**
	 * Set the user form
	 */
	public void setForm() {
		form = new Form();
		form.setWriteThrough(false);
		form.setInvalidCommitted(false);

		// set form data
		BeanItem<User> userItem = new BeanItem<User>(ManagerApplication.getInstance()
				.getCurrentUser());
		form.setItemDataSource(userItem);
		form.setFormFieldFactory(new UserFieldFactory(ManagerApplication.getInstance()
				.getCurrentUser()));
		form.setVisibleItemProperties(new String[] { FieldConstants.USERNAME,
				FieldConstants.PASSWORD, });

		// form buttons
		((HorizontalLayout) form.getFooter()).setSpacing(true);
		Button saveButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_SAVE));
		saveButton.addListener(Button.ClickEvent.class, this,
				"saveButtonClicked");
		form.getFooter().addComponent(saveButton);
	}

}
