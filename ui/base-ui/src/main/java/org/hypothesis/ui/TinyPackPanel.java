package org.hypothesis.ui;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static com.vaadin.ui.themes.ValoTheme.LABEL_SMALL;

import com.vaadin.data.Property;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TinyPackPanel extends Panel {

	private Label descriptionLabel;

	public TinyPackPanel() {
		setWidth(100, PIXELS);
		setHeight(100, PERCENTAGE);

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		setContent(layout);

		descriptionLabel = new Label();
		descriptionLabel.setSizeFull();
		descriptionLabel.addStyleName(LABEL_SMALL);
		layout.addComponent(descriptionLabel);
	}

	public void setDescriptionPropertyDataSource(Property<?> newDataSource) {
		descriptionLabel.setPropertyDataSource(newDataSource);
	}

}
