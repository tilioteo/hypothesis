/**
 * 
 */
package org.hypothesis.application.collector.slide;

import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.evaluable.Evaluable;
import org.hypothesis.application.collector.events.FinishSlideEvent;
import org.hypothesis.application.collector.xml.SlideXmlConstants;
import org.hypothesis.common.Strings;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class SlideCommand implements Evaluable {

	private SlideManager slideManager;
	private String command;

	public SlideCommand(SlideManager slideManager, String command) {
		this.slideManager = slideManager;
		this.command = command;
	}

	public void evaluate() {
		if (!Strings.isNullOrEmpty(command)) {
			if (command.equalsIgnoreCase(SlideXmlConstants.FINISH)) {
				slideManager.getEventManager().fireEvent(
						new FinishSlideEvent(slideManager.current()));
			}
		}
		// TODO implement next slide commands
	}

	public void setVariables(VariableMap variables) {
		// nothing
	}

	public void updateVariables(VariableMap variables) {
		// nothing
	}

}
