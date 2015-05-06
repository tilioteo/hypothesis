/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import org.vaadin.button.ui.OpenPopupButton;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.event.HypothesisEvent.StartFeaturedTestEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.StartLegacyTestEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PackPanel extends Panel {

	protected Pack pack;
	protected BeanItem<Pack> beanItem;
	protected boolean javaRequired;
	protected boolean javaInstalled = false;
	
	private VerticalLayout controlLayout;
	
	public PackPanel(Pack pack) {
		this.pack = pack; 
		beanItem = new BeanItem<Pack>(pack);
		javaRequired = pack.isJavaRequired();
		
		initPanel();
	}
	
	private void initPanel() {
		setCaption(pack.getName());
		setIcon(FontAwesome.ARCHIVE);
		
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

		panel.setContent(buildDescription());

		return panel;
	}

	private Label buildDescription() {
		Label label = new Label();
		label.setPropertyDataSource(beanItem.getItemProperty("description"));
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
		
		updateByJavaStatus();

		return panel;
	}

	private void updateByJavaStatus() {
		controlLayout.removeAllComponents();
		
		if (javaRequired) {
			setJavaRequiredControls();
		} else {
			setBaseControls();
		}
	}

	private void setJavaRequiredControls() {
		if (javaInstalled) {
			buildJavaOnlyControls();
		} else {
			buildNoJavaControls();
		}
	}

	private void buildNoJavaControls() {
		setNoJavaComponentStyle();
		
		Label label = buildNoJavaLabel();
		controlLayout.addComponent(label);
		controlLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
	}

	private void buildJavaOnlyControls() {
		setBaseComponentStyle();
		
		Label startInfoLabel = new Label(Messages.getString("Caption.Pack.ControlTopSingle"));
		startInfoLabel.setWidthUndefined();
		startInfoLabel.addStyleName(ValoTheme.LABEL_COLORED);
		controlLayout.addComponent(startInfoLabel);
		controlLayout.setComponentAlignment(startInfoLabel, Alignment.MIDDLE_CENTER);
		
		Button button = buildFeaturedButton(javaInstalled);
		controlLayout.addComponent(button);
		controlLayout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		
		Label modeLabel = new Label(Messages.getString("Caption.Pack.ControlBottomSingle"));
		modeLabel.setWidthUndefined();
		modeLabel.addStyleName(ValoTheme.LABEL_COLORED);
		controlLayout.addComponent(modeLabel);
		controlLayout.setComponentAlignment(modeLabel, Alignment.MIDDLE_CENTER);
	}

	private Button buildFeaturedButton(boolean enabled) {
		Button button = new Button(Messages.getString("Caption.Button.StartFeatured"));
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		if (enabled) {
			button.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					MainEventBus.get().post(new StartFeaturedTestEvent(pack));
				}
			});
		} else {
			button.setEnabled(enabled);
		}
		
		return button;
	}

	private Label buildNoJavaLabel() {
		Label label = new Label(Messages.getString("Caption.Pack.NoJava"));
		label.setWidth(100.0f, Unit.PERCENTAGE);
		label.addStyleName("red");

		return label;
	}

	private void setBaseControls() {
		setBaseComponentStyle();

		Label startInfoLabel = new Label(Messages.getString("Caption.Pack.ControlTop"));
		startInfoLabel.setWidthUndefined();
		startInfoLabel.addStyleName(ValoTheme.LABEL_COLORED);
		controlLayout.addComponent(startInfoLabel);
		controlLayout.setComponentAlignment(startInfoLabel, Alignment.MIDDLE_CENTER);

		CssLayout layout = new CssLayout();
		layout.addStyleName("v-component-group");
		layout.addComponent(buildFeaturedButton(javaInstalled));
		layout.addComponent(buildLegacyButton());
		controlLayout.addComponent(layout);
		controlLayout.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);

		Label modeLabel = new Label(Messages.getString("Caption.Pack.ControlBottom"));
		modeLabel.setWidthUndefined();
		modeLabel.addStyleName(ValoTheme.LABEL_COLORED);
		controlLayout.addComponent(modeLabel);
		controlLayout.setComponentAlignment(modeLabel, Alignment.MIDDLE_CENTER);
	}

	private Button buildLegacyButton() {
		final OpenPopupButton button = new OpenPopupButton(Messages.getString("Caption.Button.StartLegacy"));
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				MainEventBus.get().post(new StartLegacyTestEvent(pack, button));
			}
		});
		
		return button;
	}

	private void setBaseComponentStyle() {
		removeStyleName("packpanel-nojava");
		addStyleName("packpanel");
	}

	private void setNoJavaComponentStyle() {
		removeStyleName("packpanel");
		addStyleName("packpanel-nojava");
	}

	public void setJavaInstalled(boolean javaInstalled) {
		if (this.javaInstalled != javaInstalled) {
			this.javaInstalled = javaInstalled;
			
			updateByJavaStatus();
		}
	}

}
