/**
 * 
 */
package com.tilioteo.hypothesis.ui.view;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.event.CloseTestEvent;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class FinishTestView extends VerticalLayout implements ProcessView, ClickListener {

	private boolean clicked = false;

	public FinishTestView() {
		super();
		setSizeFull();

		buildHeading();
		buildControls();
	}

	private void buildHeading() {
		Label label = new Label(Messages.getString("Message.Info.TestFinished"));
		label.addStyleName(ValoTheme.LABEL_H2);
		label.setWidth(null);
		
		addComponent(label);
		setComponentAlignment(label, Alignment.MIDDLE_CENTER);
	}

	private void buildControls() {
		Button button = new Button(Messages.getString("Caption.Button.Close"));
		button.addStyleName(ValoTheme.BUTTON_HUGE);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.addClickListener(this);
		
		addComponent(button);
		setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		button.focus();
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button button = event.getButton();
		if (!clicked) {
			clicked = true;
			button.setEnabled(false);
			postEndEvent();
		}
	}

	private void postEndEvent() {
		ProcessEventBus.get(getUI()).post(new CloseTestEvent());
	}

}
