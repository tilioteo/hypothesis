/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.shared.ui.timer.TimerState.Direction;
import com.tilioteo.hypothesis.ui.Timer.StopEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PreparedTestContent extends VerticalLayout {
	
	private Label heading;

	public PreparedTestContent(final ProcessUI ui, final Command nextCommand) {
		super();
		setSizeFull();
		
		heading = new Label("<h2>"
				+ Messages.getString("Caption.TestReady")
				//+ ApplicationMessages.get().getString(Messages.TEXT_TEST_FINISHED)
				+ "</h2>");
		heading.setContentMode(ContentMode.HTML);
		heading.setWidth(null);
		addComponent(heading);
		setComponentAlignment(heading, Alignment.MIDDLE_CENTER);
		
		final Button button;
		if (ui.isFullscreen()) {
			button = new FsButton();
		} else {
			button = new Button();
		}
		
		button.setCaption(Messages.getString("Caption.Run")
				//ApplicationMessages.get().getString(Messages.TEXT_BUTTON_CLOSE_TEST)
				);
		button.addStyleName("big default");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				removeComponent(heading);
				// if not fullscreen requested, process test directly
				if (!ui.isFullscreen()) {
					ui.clearContent(ui.isAnimated(), nextCommand);
				} else {
					// countdown for fullscreen initialization
					Timer timer = new Timer();
					timer.setDirection(Direction.DOWN);
					
					timer.addStopListener(new Timer.StopListener() {
						@Override
						public void stop(StopEvent event) {
							ui.clearContent(ui.isAnimated(), nextCommand);
						}
					});
					
					ui.addTimer(timer);
					
					TimerLabel timerLabel = new TimerLabel();
					timerLabel.setTimer(timer);
					timerLabel.setTimeFormat("s.S");
					timerLabel.setWidth(null);
				
					replaceComponent(button, timerLabel);
					setComponentAlignment(timerLabel, Alignment.MIDDLE_CENTER);
					timer.start(5000);
				}
			}
		});
		
		addComponent(button);
		setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		button.focus();
	}
}