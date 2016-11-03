/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.hypothesis.interfaces.Command;
import org.hypothesis.interfaces.ProcessView;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TestEndScreen extends VerticalLayout implements ProcessView, ClickListener {

	private boolean clicked = false;

	private Label infoLabel = null;
	private String infoLabelCaption = "infoLabelCaption";

	private Button controlButton = null;
	private String controlButtonCaption = "controlButtonCaption";

	private Command nextCommand = null;

	public TestEndScreen() {
		super();
		setSizeFull();

		buildHeading();
		buildControls();
	}

	private void buildHeading() {
		infoLabel = new Label(infoLabelCaption);
		infoLabel.addStyleName(ValoTheme.LABEL_H2);
		infoLabel.setWidth(null);

		addComponent(infoLabel);
		setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);
	}

	private void buildControls() {
		controlButton = new Button(controlButtonCaption);
		controlButton.addStyleName(ValoTheme.BUTTON_HUGE);
		controlButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		controlButton.addClickListener(this);

		addComponent(controlButton);
		setComponentAlignment(controlButton, Alignment.MIDDLE_CENTER);
		controlButton.focus();
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button button = event.getButton();
		if (!clicked) {
			clicked = true;
			button.setEnabled(false);
			nextCommand();
		}
	}

	private void nextCommand() {
		Command.Executor.execute(nextCommand);
	}

	public void setInfoLabelCaption(String caption) {
		this.infoLabelCaption = caption;
		if (infoLabel != null) {
			infoLabel.setValue(caption);
		}
	}

	public void setControlButtonCaption(String caption) {
		this.controlButtonCaption = caption;
		if (controlButton != null) {
			controlButton.setCaption(caption);
		}
	}

	public void setNextCommand(Command command) {
		this.nextCommand = command;
	}
}
