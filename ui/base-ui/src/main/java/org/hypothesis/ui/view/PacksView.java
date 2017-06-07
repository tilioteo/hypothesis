/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.view;

import java.util.Iterator;

import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.ui.PackPanel;
import org.vaadin.jre.ui.DeployJava;
import org.vaadin.jre.ui.DeployJava.JavaCheckedEvent;
import org.vaadin.jre.ui.DeployJava.JavaCheckedListener;
import org.vaadin.jre.ui.DeployJava.JavaInfoPanel;

import org.hypothesis.slide.ui.Mask;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
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
public class PacksView extends HorizontalLayout implements View {

	private final PacksPresenter presenter;

	private VerticalLayout mainLayout;
	private JavaInfoPanel javaInfoPanel;
	private Panel mainPanel;

	private Mask mask = null;
	private boolean isMasked = false;

	private boolean isEmptyInfo = false;

	private Label emptyInfoLabel = null;
	private String emptyInfoCaption = "emptyInfoCaption";

	private String checkingJavaInfoCaption = "checkingJavaInfoCaption";
	private String javaInstalledCaption = "javaInstalledCaption";
	private String javaNotInstalledCaption = "javaNotInstalledCaption";
	private String javaInstallLinkCaption = "javaInstallLinkCaption";

//	private final JavaCheckedListener javaCheckedListener = new JavaCheckedListener() {
//		@Override
//		public void javaChecked(JavaCheckedEvent event) {
//			updateJavaInstalled(event.getResult());
//		}
//
//	};

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

		panel.setCheckInfoText(checkingJavaInfoCaption);
		panel.setJavaInstalledText(javaInstalledCaption);
		panel.setJavaNotInstalledText(javaNotInstalledCaption);
		panel.setInstallLinkText(javaInstallLinkCaption);

		return panel;
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

		if (javaInfoPanel != null && javaInfoPanel.isVisible() && !javaInfoPanel.isJavaOk()) {
			try {
				javaInfoPanel.checkJavaVersion();
			} catch (Throwable e) {
			}
		}
	}

//	private void updateJavaInstalled(boolean javaInstalled) {
//		if (!javaInstalled) {
//			javaInfoPanel.removeStyleName("hidden");
//		}
//
//		if (mainLayout != null) {
//			Iterator<Component> iterator = mainLayout.iterator();
//			for (; iterator.hasNext();) {
//				Component component = iterator.next();
//				if (component instanceof PackPanel) {
//					((PackPanel) component).setJavaInstalled(javaInstalled);
//				}
//			}
//		}
//	}

	@Override
	public void attach() {
		super.attach();

		presenter.attach();

//		DeployJava.get(getUI()).addJavaCheckedListener(javaCheckedListener);
	}

	@Override
	public void detach() {
//		DeployJava.get(getUI()).removeJavaCheckedListener(javaCheckedListener);

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
//			panel.setJavaInstalled(javaInfoPanel.isJavaOk());

			mainLayout.addComponent(panel);
		}
	}

	public void setEmptyInfoCaption(String caption) {
		this.emptyInfoCaption = caption;

		if (emptyInfoLabel != null) {
			emptyInfoLabel.setCaption(caption);
		}
	}

	public void setCheckingJavaInfo(String caption) {
		this.checkingJavaInfoCaption = caption;
		if (javaInfoPanel != null) {
			javaInfoPanel.setCheckInfoText(caption);
		}
	}

	public void setJavaInstalledCaption(String caption) {
		this.javaInstalledCaption = caption;
		if (javaInfoPanel != null) {
			javaInfoPanel.setJavaInstalledText(caption);
		}
	}

	public void setJavaNotInstalledCaption(String caption) {
		this.javaNotInstalledCaption = caption;
		if (javaInfoPanel != null) {
			javaInfoPanel.setJavaNotInstalledText(caption);
		}
	}

	public void setJavaInstalLinkCaption(String caption) {
		this.javaInstallLinkCaption = caption;
		if (javaInfoPanel != null) {
			javaInfoPanel.setInstallLinkText(caption);
		}
	}

}
