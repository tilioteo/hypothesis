/**
 * 
 */
package com.tilioteo.hypothesis.ui.view;

import java.util.Iterator;
import java.util.List;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.model.CanSetUrl;
import com.tilioteo.hypothesis.model.PacksModel;
import com.tilioteo.hypothesis.ui.DeployJava;
import com.tilioteo.hypothesis.ui.DeployJava.JavaCheckedEvent;
import com.tilioteo.hypothesis.ui.DeployJava.JavaCheckedListener;
import com.tilioteo.hypothesis.ui.DeployJava.JavaInfoPanel;
import com.tilioteo.hypothesis.ui.PackPanel;
import com.tilioteo.hypothesis.ui.PackPanel.StartEvent;
import com.tilioteo.hypothesis.ui.PackPanel.StartListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
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
public class PublicPacksView extends HorizontalLayout implements View, StartListener {
	
	protected PacksModel packsModel = new PacksModel();
	private VerticalLayout mainLayout;
	private JavaInfoPanel javaInfoPanel;
	private Panel mainPanel;
	
	private JavaCheckedListener javaCheckedListener = new JavaCheckedListener() {
		@Override
		public void javaChecked(JavaCheckedEvent event) {
			updateChildren(event.getResult());
		}

	};
	
	public PublicPacksView() {
		initListeners();
		
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
		
		javaInfoPanel = buildJavaInfoPanel();
		layout.addComponent(javaInfoPanel);
		
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

	private JavaInfoPanel buildJavaInfoPanel() {
		JavaInfoPanel panel = new JavaInfoPanel("1.7.0+");
		panel.setWidth(100.0f, Unit.PERCENTAGE);
		panel.addStyleName("hidden");
		
		return panel;
	}

	private VerticalLayout buildVerticalPane() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(150.0f, Unit.PIXELS);
		layout.setHeight(100.0f, Unit.PERCENTAGE);
		layout.addStyleName("color2");

		return layout;
	}

	private void initListeners() {
		addAttachListener(new AttachListener() {
			@Override
			public void attach(AttachEvent event) {
				DeployJava.get(getUI()).addJavaCheckedListener(javaCheckedListener);
			}
		});
		
		addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				DeployJava.get(getUI()).removeJavaCheckedListener(javaCheckedListener);
			}
		});
	}

	protected List<Pack> getPacks() {
		return packsModel.getPublicPacks();
		//packsModel.getPackByHash(hash)
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshMainPanel();

		if (javaInfoPanel != null && javaInfoPanel.isVisible() && !javaInfoPanel.isJavaOk()) {
			try {
				javaInfoPanel.checkJavaVersion();
			} catch (Throwable e) {}
		}
	}

	private void refreshMainPanel() {
		mainLayout.removeAllComponents();
		
		List<Pack> packs = getPacks();
		if (packs != null && !packs.isEmpty()) {
			buildPackControls(packs);
		} else {
			buildEmptyInfo();
		}
		markAsDirty();
	}

	private void buildEmptyInfo() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		
		Label label = new Label("There are no packs to run test.");
		label.addStyleName(ValoTheme.LABEL_LIGHT);
		label.addStyleName(ValoTheme.LABEL_LARGE);
		label.setWidthUndefined();
		
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		
		mainPanel.setContent(layout);
	}

	private void buildPackControls(List<Pack> packs) {
		for (Pack pack : packs) {
			PackPanel packPanel = new PackPanel(pack);
			packPanel.addStartListener(this);
			packPanel.setHeight(135.0f, Unit.PIXELS);
			
			packPanel.setJavaInstalled(javaInfoPanel.isJavaOk());
			
			mainLayout.addComponent(packPanel);
		}
	}

	private void updateChildren(boolean javaInstalled) {
		if (mainLayout != null) {
			Iterator<Component> iterator = mainLayout.iterator();
			for (;iterator.hasNext();) {
				Component component = iterator.next();
				if (component instanceof PackPanel) {
					((PackPanel)component).setJavaInstalled(javaInstalled);
				}
			}
		}
	}

	@Override
	public void start(StartEvent event) {
		if (event.isPrimary()) {
			packsModel.startFeaturedTest(event.getPack());
		} else if (event.getSource() instanceof CanSetUrl) {
			packsModel.startLegacyTest((CanSetUrl)event.getSource(), event.getPack());
		}
	}

}
