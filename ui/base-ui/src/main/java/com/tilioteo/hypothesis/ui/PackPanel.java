/**
 * 
 */
package com.tilioteo.hypothesis.ui;

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
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PackPanel extends Panel {

	protected boolean javaRequired = true;
	protected boolean javaInstalled = false;
	
	private boolean isSingle = false;
	
	private VerticalLayout controlLayout;
	private Label descriptionLabel;
	
	private Label startInfoLabel = null;
	private String startInfoLabelCaption = "startInfoLabelCaption";
	private String startInfoSingleLabelCaption = "startInfoSingleLabelCaption";
	
	private Label modeLabel = null;
	private String modeLabelCaption = "modeLabelCaption";
	private String modeSingleLabelCaption = "modeSingleLabelCaption";
	
	private Label noJavaLabel = null;
	private String noJavaLabelCaption = "noJavaLabelCaption";
	
	private Button featuredButton = null;
	private String featuredButtonCaption = "featuredButtonCaption";
	private ClickListener featuredButtonClickListener = null;
	
	private OpenPopupButton legacyButton = null;
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
		
		noJavaLabel = buildNoJavaLabel();
		controlLayout.addComponent(noJavaLabel);
		controlLayout.setComponentAlignment(noJavaLabel, Alignment.MIDDLE_CENTER);
	}

	private void buildJavaOnlyControls() {
		setBaseComponentStyle();
		
		startInfoLabel = new Label(startInfoSingleLabelCaption);
		startInfoLabel.setWidthUndefined();
		startInfoLabel.addStyleName(ValoTheme.LABEL_COLORED);
		controlLayout.addComponent(startInfoLabel);
		controlLayout.setComponentAlignment(startInfoLabel, Alignment.MIDDLE_CENTER);
		
		featuredButton = buildFeaturedButton(javaInstalled);
		controlLayout.addComponent(featuredButton);
		controlLayout.setComponentAlignment(featuredButton, Alignment.MIDDLE_CENTER);
		
		modeLabel = new Label(modeSingleLabelCaption);
		modeLabel.setWidthUndefined();
		modeLabel.addStyleName(ValoTheme.LABEL_COLORED);
		controlLayout.addComponent(modeLabel);
		controlLayout.setComponentAlignment(modeLabel, Alignment.MIDDLE_CENTER);
		
		isSingle = true;
	}

	private Button buildFeaturedButton(boolean enabled) {
		Button button = new Button(featuredButtonCaption);
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		if (enabled && featuredButtonClickListener != null) {
			button.addClickListener(featuredButtonClickListener);
			/*
			button.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					MainEventBus.get().post(new MainUIEvent.StartFeaturedTestEvent(getNoGuestUser(), pack));
				}
			});*/
		} else {
			button.setEnabled(enabled);
		}
		
		return button;
	}
	
	private Label buildNoJavaLabel() {
		Label label = new Label(noJavaLabelCaption);
		label.setWidth(100.0f, Unit.PERCENTAGE);
		label.addStyleName("red");

		return label;
	}

	private void setBaseControls() {
		setBaseComponentStyle();

		Label startInfoLabel = new Label(startInfoLabelCaption);
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

		Label modeLabel = new Label(modeLabelCaption);
		modeLabel.setWidthUndefined();
		modeLabel.addStyleName(ValoTheme.LABEL_COLORED);
		controlLayout.addComponent(modeLabel);
		controlLayout.setComponentAlignment(modeLabel, Alignment.MIDDLE_CENTER);
		
		isSingle = false;
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

	
	public void setJavaRequired(boolean javaRequired) {
		if (this.javaRequired != javaRequired) {
			this.javaRequired = javaRequired;
		
			updateByJavaStatus();
		}
	}
	
	public void setDescriptionPropertyDataSource(Property<?> newDataSource) {
		descriptionLabel.setPropertyDataSource(newDataSource);
	}
	
	public void setStartInfoCaption(String caption) {
		this.startInfoLabelCaption = caption;
		if (startInfoLabel != null && !isSingle) {
			startInfoLabel.setCaption(caption);
		}
	}
	
	public void setStartInfoSingleCaption(String caption) {
		this.startInfoSingleLabelCaption = caption;
		if (startInfoLabel != null && isSingle) {
			startInfoLabel.setCaption(caption);
		}
	}
	
	public void setModeCaption(String caption) {
		this.modeLabelCaption = caption;
		if (modeLabel != null && !isSingle) {
			modeLabel.setCaption(caption);
		}
	}
	
	public void setModeSingleCaption(String caption) {
		this.modeSingleLabelCaption = caption;
		if (modeSingleLabelCaption != null && isSingle) {
			modeLabel.setCaption(caption);
		}
	}
	
	public void setNoJavaCaption(String caption) {
		this.noJavaLabelCaption = caption;
		if (noJavaLabel != null) {
			noJavaLabel.setCaption(caption);
		}
	}
	
	public void setFeaturedButtonCaption(String caption) {
		this.featuredButtonCaption = caption;
		if (featuredButton != null) {
			featuredButton.setCaption(caption);
		}
	}
	
	public void setFeaturedButtonClickListener(ClickListener clickListener) {
		if (this.featuredButtonClickListener != null) {
			if (featuredButton != null) {
				featuredButton.removeClickListener(this.featuredButtonClickListener);
			}
		}
		
		this.featuredButtonClickListener = clickListener;
		if (featuredButton != null && featuredButton.isEnabled()) {
			featuredButton.addClickListener(clickListener);
		}
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
}
