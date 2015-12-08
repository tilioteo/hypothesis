/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import org.vaadin.button.ui.FullscreenButton;
import org.vaadin.special.shared.ui.timer.TimerState.Direction;
import org.vaadin.special.ui.Timer.StopEvent;

import com.tilioteo.hypothesis.interfaces.Command;
import com.tilioteo.hypothesis.interfaces.ProcessView;
import com.tilioteo.hypothesis.slide.ui.Timer;
import com.tilioteo.hypothesis.slide.ui.TimerLabel;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class TestBeginScreen extends VerticalLayout implements ProcessView, ClickListener {

	private boolean fullscreen;
	private int countDown;
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

		timer.addStopListener(new Timer.StopListener() {
			@Override
			public void stop(StopEvent event) {
				nextCommand();
			}
		});

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
