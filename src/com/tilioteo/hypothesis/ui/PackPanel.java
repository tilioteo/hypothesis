/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Test;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
public class PackPanel extends Panel implements ClickListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean collapsed = false;
	
	private VerticalLayout risedLayout = new VerticalLayout();
	private VerticalLayout collapsedLayout = new VerticalLayout();
	private Button startButton;
	
	private BeanItem<Pack> beanItem;
	
	public PackPanel(Pack pack) {
		beanItem = new BeanItem<Pack>(pack);
		
		setWidth("100%");
		
		initRisedLayout();
		initCollapsedLayout();
		
		collapse();
	}
	
	private void initRisedLayout() {
		Label nameLabel = new Label();
		nameLabel.setPropertyDataSource(beanItem.getItemProperty("name"));
		nameLabel.setWidth("100%");
		nameLabel.setHeight("20px");
		risedLayout.addComponent(nameLabel);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		risedLayout.addComponent(horizontalLayout);
		risedLayout.setExpandRatio(horizontalLayout, 1.0f);
		
		Panel leftPanel = new Panel();
		leftPanel.addStyleName(Runo.PANEL_LIGHT);
		leftPanel.setHeight("100%");
		leftPanel.setWidth("130px");
		
		horizontalLayout.addComponent(leftPanel);
		
		Panel mainPanel = new Panel();
		mainPanel.setSizeFull();
		mainPanel.addStyleName(Runo.PANEL_LIGHT);
		
		VerticalLayout panelLayout = new VerticalLayout();
		mainPanel.setContent(panelLayout);
		Label descrLabel = new Label();
		descrLabel.setPropertyDataSource(beanItem.getItemProperty("description"));
		descrLabel.setWidth("100%");
		descrLabel.setHeight("100px");
		panelLayout.addComponent(descrLabel);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSizeFull();
		panelLayout.addComponent(buttonLayout);
		
		startButton = new Button("Start test");
		buttonLayout.addComponent(startButton);
		startButton.addClickListener(this);
		
		horizontalLayout.addComponent(mainPanel);
		horizontalLayout.setExpandRatio(mainPanel, 1.0f);
	}
	
	private void initCollapsedLayout() {
		Label nameLabel = new Label();
		nameLabel.setPropertyDataSource(beanItem.getItemProperty("name"));
		nameLabel.setWidth("100%");
		nameLabel.setHeight("20px");
		collapsedLayout.addComponent(nameLabel);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		collapsedLayout.addComponent(horizontalLayout);
		collapsedLayout.setExpandRatio(horizontalLayout, 1.0f);
		
		Panel leftPanel = new Panel();
		leftPanel.addStyleName(Runo.PANEL_LIGHT);
		leftPanel.setHeight("100%");
		leftPanel.setWidth("130px");
		horizontalLayout.addComponent(leftPanel);
		
		Panel mainPanel = new Panel();
		mainPanel.setSizeFull();
		mainPanel.addStyleName(Runo.PANEL_LIGHT);
		
		VerticalLayout panelLayout = new VerticalLayout();
		mainPanel.setContent(panelLayout);
		Label descrLabel = new Label();
		descrLabel.setPropertyDataSource(beanItem.getItemProperty("description"));
		descrLabel.setWidth("100%");
		descrLabel.setHeight("30px");
		panelLayout.addComponent(descrLabel);

		horizontalLayout.addComponent(mainPanel);
		horizontalLayout.setExpandRatio(mainPanel, 1.0f);
	}
	
	public void collapse() {
		if (!collapsed) {
			
			setHeight(-1, Unit.PIXELS);
			setContent(collapsedLayout);
			collapsed = true;
		}
	}

	public void rise() {
		if (collapsed) {
			
			setHeight("150px");
			setContent(risedLayout);
			collapsed = false;
		}
	}
	
	public boolean isCollapsed() {
		return collapsed;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		fireEvent(new StartEvent(this, beanItem.getBean()));
		
		collapse();
	}
	
	@SuppressWarnings("serial")
	public class StartEvent extends Component.Event {

		public static final String EVENT_ID = "start";
		
		private Pack pack;

		public StartEvent(Component source, Pack pack) {
			super(source);
			
			this.pack = pack;
		}
		
		public Pack getPack() {
			return pack;
		}
		
	}
	
	public interface StartListener extends Serializable {

		public static final Method START_TEST_METHOD = ReflectTools
				.findMethod(StartListener.class, "start", StartEvent.class);

		/**
		 * Called when a {@link Test} has to be started. A reference to the
		 * pack is given by {@link StartEvent#getPack()}.
		 * 
		 * @param event
		 *            An event containing information about the pack.
		 */
		public void start(StartEvent event);

	}
	
	public void addStartListener(StartListener listener) {
		addListener(StartEvent.EVENT_ID, StartEvent.class, listener, StartListener.START_TEST_METHOD);
	}
	
	public void removeStartListener(StartListener listener) {
		removeListener(StartEvent.EVENT_ID, StartEvent.class, listener);
	}
}
