/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import org.vaadin.button.ui.OpenPopupButton;
import org.vaadin.button.ui.OpenPopupButton.WindowClosedListener;

import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PackPanel extends Panel {

	private VerticalLayout controlLayout;
	private Label descriptionLabel;

	private OpenPopupButton legacyButton;
	private String legacyButtonCaption = "legacyButtonCaption";
	private ClickListener legacyButtonClickListener = null;
	private WindowClosedListener legacyButtonWindowClosedListener = null;

	private DetachListener uiDetachListener = null;

	public PackPanel() {
		initPanel();
	}

	private void initPanel() {
		setContent(buildLayout());
	}

	private HorizontalLayout buildLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);

		Panel leftPanel = buildDescriptionPanel();
		layout.addComponent(leftPanel);
		layout.setExpandRatio(leftPanel, 1.0f);

		layout.addComponent(buildControlPanel());

		return layout;
	}

	private Panel buildDescriptionPanel() {
		Panel panel = new Panel();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.setSizeFull();

		descriptionLabel = buildDescription();
		panel.setContent(descriptionLabel);

		return panel;
	}

	private Label buildDescription() {
		Label label = new Label();
		label.setSizeFull();

		return label;
	}

	private Panel buildControlPanel() {
		Panel panel = new Panel();
		panel.setWidth(250.0f, Unit.PIXELS);
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		panel.setContent(layout);

		controlLayout = new VerticalLayout();
		controlLayout.setSizeFull();
		controlLayout.addStyleName("controls");
		layout.addComponent(controlLayout);
		layout.setComponentAlignment(controlLayout, Alignment.MIDDLE_CENTER);

		setBaseControls();

		return panel;
	}

	private void setBaseControls() {
		setBaseComponentStyle();

		CssLayout layout = new CssLayout();
		layout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		layout.addComponent(buildLegacyButton());
		controlLayout.addComponent(layout);
		controlLayout.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
	}

	private Button buildLegacyButton() {
		final OpenPopupButton button = new OpenPopupButton(legacyButtonCaption);
		button.addAttachListener(new AttachListener() {
			@Override
			public void attach(AttachEvent event) {
				uiDetachListener = new DetachListener() {
					@Override
					public void detach(DetachEvent event) {
						button.setEnabled(false);
					}
				};
				button.getUI().addDetachListener(uiDetachListener);
			}
		});

		button.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				button.getUI().removeDetachListener(uiDetachListener);
				uiDetachListener = null;
			}
		});

		if (legacyButtonClickListener != null) {
			button.addClickListener(legacyButtonClickListener);
		}

		if (legacyButtonWindowClosedListener != null) {
			button.addWindowClosedListener(legacyButtonWindowClosedListener);
		}

		legacyButton = button;
		return button;
	}

	private void setBaseComponentStyle() {
		removeStyleName("packpanel-nojava");
		addStyleName("packpanel");
	}

	public void setDescriptionPropertyDataSource(Property<?> newDataSource) {
		descriptionLabel.setPropertyDataSource(newDataSource);
	}

	public void setLegacyButtonCaption(String caption) {
		this.legacyButtonCaption = caption;
		if (legacyButton != null) {
			legacyButton.setCaption(caption);
		}
	}

	public void setLegacyButtonClickListener(ClickListener clickListener) {
		if (this.legacyButtonClickListener != null) {
			if (legacyButton != null) {
				legacyButton.removeClickListener(this.legacyButtonClickListener);
			}
		}

		this.legacyButtonClickListener = clickListener;
		if (legacyButton != null) {
			legacyButton.addClickListener(clickListener);
		}
	}

	public void setLegacyButtonWindowClosedListener(WindowClosedListener windowClosedListener) {
		if (this.legacyButtonWindowClosedListener != null) {
			if (legacyButton != null) {
				legacyButton.removeWindowClosedListener(this.legacyButtonWindowClosedListener);
			}
		}

		this.legacyButtonWindowClosedListener = windowClosedListener;
		if (legacyButton != null) {
			legacyButton.addWindowClosedListener(windowClosedListener);
		}
	}

	public OpenPopupButton getLegacyButton() {
		return legacyButton;
	}

}
