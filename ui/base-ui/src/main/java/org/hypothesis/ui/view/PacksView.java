/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.view;

import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.slide.ui.Mask;
import org.hypothesis.ui.PackPanel;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
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
public class PacksView extends HorizontalLayout implements UIView {

	private final PacksPresenter presenter;

	private VerticalLayout mainLayout;
	private Panel mainPanel;

	private Mask mask = null;
	private boolean isMasked = false;

	private boolean isEmptyInfo = false;

	private Label emptyInfoLabel = null;
	private String emptyInfoCaption = "emptyInfoCaption";

	public PacksView(PacksPresenter presenter) {
		this.presenter = presenter;

		setSizeFull();

		Panel contentPanel = buildContentPanel();
		addComponent(contentPanel);
		setExpandRatio(contentPanel, 1.0f);
		addComponent(buildVerticalPane());

	}

	private Panel buildContentPanel() {
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		panel.setContent(layout);

		mainPanel = buildMainPanel();
		layout.addComponent(mainPanel);
		layout.setExpandRatio(mainPanel, 1.0f);

		return panel;
	}

	private Panel buildMainPanel() {
		Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

		mainLayout = buildMainLayout();
		panel.setContent(mainLayout);

		return panel;
	}

	private VerticalLayout buildMainLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setHeightUndefined();
		layout.setWidth(100.0f, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);

		return layout;
	}

	private VerticalLayout buildVerticalPane() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(150.0f, Unit.PIXELS);
		layout.setHeight(100.0f, Unit.PERCENTAGE);
		layout.addStyleName("color2");

		return layout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		presenter.enter(event);
	}

	@Override
	public void attach() {
		super.attach();

		presenter.attach();
	}

	@Override
	public void detach() {
		presenter.detach();

		super.detach();
	}

	public void mask() {
		if (null == mask) {
			mask = Mask.addToComponent(mainPanel);
			mask.setColor("rgba(128,128,128,0.3)");
		}
		if (!isMasked) {
			mask.show();
			isMasked = true;
		}
	}

	public void unmask() {
		if (mask != null && isMasked) {
			mask.hide();
			isMasked = false;
		}
	}

	public void clearMainLayout() {
		mainLayout.removeAllComponents();
		isEmptyInfo = false;
	}

	public void setEmptyInfo() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		emptyInfoLabel = new Label(emptyInfoCaption, ContentMode.HTML);
		emptyInfoLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		emptyInfoLabel.addStyleName(ValoTheme.LABEL_LARGE);
		emptyInfoLabel.setWidthUndefined();

		layout.addComponent(emptyInfoLabel);
		layout.setComponentAlignment(emptyInfoLabel, Alignment.MIDDLE_CENTER);

		mainPanel.setContent(layout);

		isEmptyInfo = true;
	}

	public void addPackPanel(PackPanel panel) {
		if (panel != null) {
			if (isEmptyInfo) {
				clearMainLayout();
			}

			panel.setHeight(150.0f, Unit.PIXELS);

			mainLayout.addComponent(panel);
		}
	}

	public void setEmptyInfoCaption(String caption) {
		this.emptyInfoCaption = caption;

		if (emptyInfoLabel != null) {
			emptyInfoLabel.setCaption(caption);
		}
	}

}
