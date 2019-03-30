package org.hypothesis.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TinyPackPanel extends Panel {

	private Label descriptionLabel;

	public TinyPackPanel() {
		setWidth(100f, Unit.PIXELS);
		// setHeight(20f, Unit.PIXELS);
		setHeight(100f, Unit.PERCENTAGE);

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		setContent(layout);

		descriptionLabel = new Label();
		layout.addComponent(descriptionLabel);
	}

	public void setDescriptionPropertyDataSource(Property<?> newDataSource) {
		descriptionLabel.setPropertyDataSource(newDataSource);
	}

}
