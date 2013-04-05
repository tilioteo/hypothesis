/**
 * 
 */
package org.hypothesis.application.collector.core;

import org.hypothesis.application.collector.events.AbstractComponentEvent;
import org.hypothesis.application.collector.events.ButtonData;
import org.hypothesis.application.collector.events.ButtonEvent;
import org.hypothesis.application.collector.events.ButtonPanelData;
import org.hypothesis.application.collector.events.ButtonPanelEvent;
import org.hypothesis.application.collector.events.Command;
import org.hypothesis.application.collector.events.ImageData;
import org.hypothesis.application.collector.events.ImageEvent;
import org.hypothesis.application.collector.events.RadioPanelData;
import org.hypothesis.application.collector.events.RadioPanelEvent;
import org.hypothesis.application.collector.slide.AbstractBaseAction;
import org.hypothesis.application.collector.slide.HasActions;
import org.hypothesis.application.collector.ui.component.Image;

import com.vaadin.ui.Button;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class CommandFactory {

	public static Command createActionCommand(HasActions actions,
			String actionId) {
		final AbstractBaseAction action = actions != null ? actions.getActions()
				.get(actionId) : null;

		return new Command() {
			public void execute() {
				if (action != null)
					action.execute();
			}
		};
	}

	public static Command createButtonClickEventCommand(Button component,
			SlideManager slideManager) {
		final ButtonEvent event = new ButtonEvent.Click(
		/* ProcessEvents.get(ProcessEvents.ButtonClick), */new ButtonData(
				component, slideManager));

		return createComponentEventCommand(event);
	}

	public static Command createButtonPanelClickEventCommand(
			ButtonPanelData data) {
		final ButtonPanelEvent event = new ButtonPanelEvent.Click(
		/* ProcessEvents.get(ProcessEvents.ButtonClick), */data);

		return createComponentEventCommand(event);
	}

	private static Command createComponentEventCommand(
			final AbstractComponentEvent<?> event) {
		return new Command() {
			public void execute() {
				event.getComponentData().getSlideManager().getEventManager()
						.fireEvent(event);
			}
		};
	}

	public static Command createImageClickEventCommand(ImageData data) {
		final ImageEvent event = new ImageEvent.Click(
		/* ProcessEvents.get(ProcessEvents.ImageClick), */data);

		return createComponentEventCommand(event);
	}

	public static Command createImageLoadEventCommand(Image component,
			SlideManager slideManager) {
		final ImageEvent event = new ImageEvent.Load(
		/* ProcessEvents.get(ProcessEvents.ImageLoad), */new ImageData(
				component, slideManager));

		return createComponentEventCommand(event);
	}

	public static Command createRadioPanelClickEventCommand(RadioPanelData data) {
		final RadioPanelEvent event = new RadioPanelEvent.Click(
		/* ProcessEvents.get(ProcessEvents.ButtonClick), */data);

		return createComponentEventCommand(event);
	}

}
