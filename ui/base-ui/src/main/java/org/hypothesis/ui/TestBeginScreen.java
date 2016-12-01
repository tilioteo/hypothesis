/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.hypothesis.interfaces.Command;
import org.hypothesis.interfaces.ProcessView;
import org.hypothesis.slide.ui.Timer;
import org.hypothesis.slide.ui.TimerLabel;
import org.vaadin.button.ui.FullscreenButton;
import org.vaadin.special.shared.ui.timer.TimerState.Direction;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TestBeginScreen extends VerticalLayout implements ProcessView, ClickListener {

	private final boolean fullscreen;
	private final int countDown;
	private boolean clicked = false;

	private HorizontalLayout headingLayout;
	private HorizontalLayout controlLayout;

	private Label infoLabel = null;
	private String infoLabelCaption = "infoLabelCaption";

	private Button controlButton = null;
	private String controlButtonCaption = "controlButtonCaption";

	private Command nextCommand = null;

	public TestBeginScreen(boolean switchFullscreen, int countDownSeconds) {
		super();
		this.fullscreen = switchFullscreen;
		this.countDown = countDownSeconds;

		setSizeFull();

		buildHeading();
		buildControls();
	}

	private void buildHeading() {
		headingLayout = new HorizontalLayout();
		headingLayout.setSizeFull();
		addComponent(headingLayout);
		setExpandRatio(headingLayout, 0.5f);

		infoLabel = new Label(infoLabelCaption);
		infoLabel.addStyleName(ValoTheme.LABEL_H2);
		infoLabel.setWidth(null);
		headingLayout.addComponent(infoLabel);
		headingLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);
	}

	private void buildControls() {
		controlLayout = new HorizontalLayout();
		controlLayout.setSizeFull();

		addComponent(controlLayout);
		setExpandRatio(controlLayout, 1.0f);

		controlButton = createButton();
		controlLayout.addComponent(controlButton);
		controlLayout.setComponentAlignment(controlButton, Alignment.MIDDLE_CENTER);
		controlButton.focus();
	}

	private Button createButton() {
		final Button button;
		if (fullscreen) {
			button = new FullscreenButton();
		} else {
			button = new Button();
		}

		button.setCaption(controlButtonCaption);
		button.addStyleName(ValoTheme.BUTTON_HUGE);
		button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		button.addClickListener(this);

		return button;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button button = event.getButton();
		if (!clicked) {
			clicked = true;
			button.setEnabled(false);

			if (countDown > 0) {
				buildCountdownTimer();
			} else {
				nextCommand();
			}
		}
	}

	private void buildCountdownTimer() {
		removeComponent(headingLayout);
		controlLayout.removeAllComponents();

		// countdown timer
		Timer timer = new Timer();
		timer.setDirection(Direction.DOWN);

		timer.addStopListener(e -> nextCommand());

		TimerLabel timerLabel = new TimerLabel();
		timerLabel.setTimer(timer);
		timerLabel.setTimeFormat("s.S");
		timerLabel.setWidth(null);
		timerLabel.addStyleName(ValoTheme.LABEL_COLORED);
		timerLabel.addStyleName(ValoTheme.LABEL_LARGE);

		controlLayout.addComponent(timerLabel);
		controlLayout.setComponentAlignment(timerLabel, Alignment.MIDDLE_CENTER);

		UI ui = getUI();
		if (ui instanceof HypothesisUI) {
			((HypothesisUI) ui).addTimer(timer);
			timer.start(countDown * 1000);
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
