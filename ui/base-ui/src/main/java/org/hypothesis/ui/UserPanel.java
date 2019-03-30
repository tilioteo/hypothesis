package org.hypothesis.ui;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

import org.hypothesis.data.model.User;

import com.vaadin.data.Property;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class UserPanel extends Panel {

	private final User user;
	private Panel packsPanel;

	private Label positionLabel;
	private Label addressLabel;
	private Label nameLabel;
	private Label surnameLabel;
	private Label statusLabel;
	private Label messageLabel;

	public UserPanel(User user) {
		this.user = user;
		initPanel();
	}

	public User getUser() {
		return user;
	}

	private void initPanel() {
		setContent(buildLayout());
		setWidth(100, PERCENTAGE);
		setHeight(100, PIXELS);
	}

	private HorizontalLayout buildLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		// layout.setMargin(true);
		// layout.setSpacing(true);

		VerticalLayout placeLayout = buildPlaceLayout();
		layout.addComponent(placeLayout);
		layout.setExpandRatio(placeLayout, 0.14f);

		VerticalLayout nameLayout = buildNameLayout();
		layout.addComponent(nameLayout);
		layout.setExpandRatio(nameLayout, 0.13f);

		VerticalLayout stateLayout = buildStateLayout();
		layout.addComponent(stateLayout);
		layout.setExpandRatio(stateLayout, 0.13f);

		VerticalLayout packsLayout = buildPacksLayout();
		layout.addComponent(packsLayout);
		layout.setExpandRatio(packsLayout, 1.0f);

		return layout;
	}

	private VerticalLayout buildPlaceLayout() {
		VerticalLayout layout = createFullsizedVerticalLayout();

		positionLabel = createFullsizedLabel();
		addressLabel = createFullsizedLabel();

		layout.addComponent(positionLabel);
		layout.addComponent(addressLabel);

		return layout;
	}

	private VerticalLayout createFullsizedVerticalLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		return layout;
	}

	private Label createFullsizedLabel() {
		Label label = new Label();
		label.setSizeFull();

		return label;
	}

	private VerticalLayout buildNameLayout() {
		VerticalLayout layout = createFullsizedVerticalLayout();

		nameLabel = createFullsizedLabel();
		surnameLabel = createFullsizedLabel();

		layout.addComponent(nameLabel);
		layout.addComponent(surnameLabel);

		return layout;
	}

	private VerticalLayout buildStateLayout() {
		VerticalLayout layout = createFullsizedVerticalLayout();

		statusLabel = createFullsizedLabel();
		Label emptyLabel = createFullsizedLabel();

		layout.addComponent(statusLabel);
		layout.addComponent(emptyLabel);

		return layout;
	}

	private VerticalLayout buildPacksLayout() {
		VerticalLayout layout = createFullsizedVerticalLayout();

		packsPanel = createPacksPanel();
		messageLabel = createFullsizedLabel();

		layout.addComponent(packsPanel);
		layout.addComponent(messageLabel);

		return layout;
	}

	private Panel createPacksPanel() {
		Panel panel = new Panel();
		HorizontalLayout layout = new HorizontalLayout();
		layout.setHeight(100f, PERCENTAGE);
		panel.setContent(layout);

		return panel;
	}

	public void setPositionPropertyDataSource(Property<?> newDataSource) {
		positionLabel.setPropertyDataSource(newDataSource);
	}

	public void setAddressPropertyDataSource(Property<?> newDataSource) {
		addressLabel.setPropertyDataSource(newDataSource);
	}

	public void setNamePropertyDataSource(Property<?> newDataSource) {
		nameLabel.setPropertyDataSource(newDataSource);
	}

	public void setSurnamePropertyDataSource(Property<?> newDataSource) {
		surnameLabel.setPropertyDataSource(newDataSource);
	}

	public void setStatusPropertyDataSource(Property<?> newDataSource) {
		statusLabel.setPropertyDataSource(newDataSource);
	}

	public void setMessagePropertyDataSource(Property<?> newDataSource) {
		messageLabel.setPropertyDataSource(newDataSource);
	}

	public Panel getPacksPanel() {
		return packsPanel;
	}

}
