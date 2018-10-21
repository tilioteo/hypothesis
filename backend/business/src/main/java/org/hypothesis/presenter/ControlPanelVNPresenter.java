package org.hypothesis.presenter;

import org.hypothesis.interfaces.ControlPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.view.ControlView;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ControlPanelVNPresenter extends AbstractMainBusPresenter implements ControlPresenter {

	private VerticalLayout content;
	private PopupDateField dateField;

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public View createView() {
		return new ControlView(this);
	}

	@Override
	public Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setSpacing(true);

		Label title = new Label(Messages.getString("Caption.Label.ControlPanel"));
		title.addStyleName("huge");
		header.addComponent(title);
		// header.addComponent(buildTools());
		header.setExpandRatio(title, 1);

		return header;
	}

	@Override
	public Component buildControl() {
		content = new VerticalLayout();
		content.setSizeFull();
		content.setSpacing(true);

		content.addComponent(buildForm());

		// testSelection = new VerticalLayout();
		// testSelection.setSizeFull();
		// content.addComponent(testSelection);
		// content.setExpandRatio(testSelection, 1);
		//
		// Label infoLabel = new
		// Label(Messages.getString("Caption.Label.ChoosePack"));
		// infoLabel.setSizeUndefined();
		// testSelection.addComponent(infoLabel);
		// testSelection.setComponentAlignment(infoLabel,
		// Alignment.MIDDLE_CENTER);

		return content;
	}

	private Component buildForm() {
		HorizontalLayout form = new HorizontalLayout();
		form.setWidth("100%");

		dateField = new PopupDateField();
		dateField.setResolution(Resolution.DAY);
		dateField.setDateFormat(Messages.getString("Format.DateTime"));
		dateField.setInputPrompt(Messages.getString("Caption.Field.DateOfTesting"));
		dateField.setImmediate(true);
		dateField.setValidationVisible(false);
		form.addComponent(dateField);
		
		Button enableButton = new Button(Messages.getString("Caption.Button.EnableTesting"));
		enableButton.addStyleName(ValoTheme.BUTTON_SMALL);
		form.addComponent(enableButton);
		
		Button disableButton = new Button(Messages.getString("Caption.Button.DisableTesting"));
		disableButton.addStyleName(ValoTheme.BUTTON_SMALL);
		form.addComponent(disableButton);

		return form;
	}

}