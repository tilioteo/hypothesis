/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.processing.Command;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class FinishTestContent extends VerticalLayout {
	
	public FinishTestContent(final Command nextCommand) {
		super();
		setSizeFull();
		
		Label heading = new Label("<h2>"
				+ Messages.getString("Message.Info.TestFinished")
				+ "</h2>");
		heading.setContentMode(ContentMode.HTML);
		heading.setWidth(null);
		addComponent(heading);
		setComponentAlignment(heading, Alignment.MIDDLE_CENTER);
		
		Button button = new Button(Messages.getString("Caption.Button.Close"));
		button.addStyleName("big default");
		if (nextCommand != null) {
			button.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					//Command.Executor.execute(nextCommand);
					Command.Executor.execute(nextCommand);
				}
			});
		}
		
		addComponent(button);
		setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		button.focus();
	}

}
