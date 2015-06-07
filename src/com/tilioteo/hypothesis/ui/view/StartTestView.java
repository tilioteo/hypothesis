/**
 * 
 */
package com.tilioteo.hypothesis.ui.view;

import org.vaadin.button.ui.FullscreenButton;
import org.vaadin.special.shared.ui.timer.TimerState.Direction;
import org.vaadin.special.ui.Timer.StopEvent;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.event.HypothesisEvent;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.slide.ui.Timer;
import com.tilioteo.hypothesis.slide.ui.TimerLabel;
import com.tilioteo.hypothesis.ui.HUI;
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
public class StartTestView extends VerticalLayout implements ProcessView, ClickListener {

	private boolean fullscreen;
	private int countDown;
	private boolean clicked = false;
	
	private HorizontalLayout headingLayout;
	private HorizontalLayout controlLayout;

	public StartTestView(boolean switchFullscreen, int countDownSeconds) {
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
		
		Label label = new Label(Messages.getString("Message.Info.TestReady"));
		label.addStyleName(ValoTheme.LABEL_H2);
		label.setWidth(null);
		headingLayout.addComponent(label);
		headingLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
	}

	private void buildControls() {
		controlLayout = new HorizontalLayout();
		controlLayout.setSizeFull();
		
		addComponent(controlLayout);
		setExpandRatio(controlLayout, 1.0f);
		
		Button button = createButton();
		controlLayout.addComponent(button);
		controlLayout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		button.focus();
	}

	private Button createButton() {
		final Button button;
		if (fullscreen) {
			button = new FullscreenButton();
		} else {
			button = new Button();
		}
		
		button.setCaption(Messages.getString("Caption.Button.Run"));
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
				postEndEvent();
			}
		}
	}

	private void buildCountdownTimer() {
		removeComponent(headingLayout);
		controlLayout.removeAllComponents();
		
		//countdown timer
		Timer timer = new Timer();
		timer.setDirection(Direction.DOWN);

		timer.addStopListener(new Timer.StopListener() {
			@Override
			public void stop(StopEvent event) {
				postEndEvent();
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
		if (ui instanceof HUI) {
			((HUI)ui).addTimer(timer);
			timer.start(countDown * 1000);
		}
	}

	private void postEndEvent() {
		ProcessEventBus.get(getUI()).post(new HypothesisEvent.ProcessViewEndEvent(this));
	}

}
