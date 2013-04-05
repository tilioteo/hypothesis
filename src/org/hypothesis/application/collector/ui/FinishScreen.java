/**
 * 
 */
package org.hypothesis.application.collector.ui;

import org.hypothesis.application.collector.events.CloseTestEvent;
import org.hypothesis.application.collector.events.ProcessEventManager;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class FinishScreen extends VerticalLayout implements ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3441619223085220244L;

	private Test test;

	private Button closeButton;

	public FinishScreen(Test test) {
		this.test = test;

		// set layout
		this.setSpacing(true);
		this.setMargin(true);

		// heading
		Label heading = new Label("<h2>"
				+ ApplicationMessages.get().getString(Messages.TEXT_TEST_FINISHED)
				+ "</h2>");
		heading.setContentMode(Label.CONTENT_XHTML);
		addComponent(heading);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		addComponent(horizontalLayout);

		closeButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_CLOSE_TEST));
		closeButton.addStyleName("big default");
		closeButton.addListener(this);
		horizontalLayout.addComponent(closeButton);
	}

	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		if (source == closeButton) {
			ProcessEventManager.get(getApplication()).fireEvent(
					new CloseTestEvent(test));
		}
	}

}
