/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.util.ReflectTools;


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
	
	private CssLayout controlLayout;
	
	public PackPanel(Pack pack) {
		this.pack = pack; 
		beanItem = new BeanItem<Pack>(pack);
		javaRequired = pack.isJavaRequired();
		
		initPanel();
	}
	
	private void initPanel() {
		setCaption(pack.getName());
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		horizontalLayout.setMargin(true);
		setContent(horizontalLayout);
		
		Panel leftPanel = new Panel();
		leftPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		leftPanel.setSizeFull();
		Label descrLabel = new Label();
		descrLabel.setPropertyDataSource(beanItem.getItemProperty("description"));
		descrLabel.setSizeFull();
		leftPanel.setContent(descrLabel);
		
		horizontalLayout.addComponent(leftPanel);
		horizontalLayout.setExpandRatio(leftPanel, 1.0f);
		
		Panel buttonPanel = new Panel();
		//buttonPanel.setHeight(100.0f, Unit.PERCENTAGE);
		buttonPanel.setWidth(250.0f, Unit.PIXELS);
		buttonPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		horizontalLayout.addComponent(buttonPanel);
		
		VerticalLayout panelLayout = new VerticalLayout();
		buttonPanel.setContent(panelLayout);
		/*HorizontalLayout hl1 = new HorizontalLayout();
		hl1.setSizeFull();
		panelLayout.addComponent(hl1);
		panelLayout.setExpandRatio(hl1, 1.0f);*/
		
		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.setWidth(100.0f, Unit.PERCENTAGE);
		hl2.setHeight(50.0f, Unit.PIXELS);
		panelLayout.addComponent(hl2);
		
		controlLayout = new CssLayout();
		controlLayout.addStyleName("controls");
		hl2.addComponent(controlLayout);
		hl2.setComponentAlignment(controlLayout, Alignment.MIDDLE_CENTER);
		
		javaStatusChanged();

		/*HorizontalLayout hl3 = new HorizontalLayout();
		hl3.setSizeFull();
		panelLayout.addComponent(hl3);
		panelLayout.setExpandRatio(hl3, 1.0f);*/
	}
	
	
	
	public void setJavaInstalled(boolean javaInstalled) {
		if (this.javaInstalled != javaInstalled) {
			this.javaInstalled = javaInstalled;
			
			javaStatusChanged();
		}
	}

	private void javaStatusChanged() {
		controlLayout.removeAllComponents();
		
		if (javaRequired) {

			if (javaInstalled) {
				removeStyleName("packpanel-nojava");
				addStyleName("packpanel");

				//controlLayout.addStyleName("v-component-group");
				controlLayout.removeStyleName("v-component-group");
				Button startPrimaryButton = new Button(Messages.getString("Caption.StartTest"));
				startPrimaryButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
				startPrimaryButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						fireEvent(new StartEvent(PackPanel.this, pack, true));
					}
				});
				controlLayout.addComponent(startPrimaryButton);
			} else {
				removeStyleName("packpanel");
				addStyleName("packpanel-nojava");

				controlLayout.removeStyleName("v-component-group");
				Label label = new Label("Cannot run when Java not installed!");
				label.addStyleName("red");
				controlLayout.addComponent(label);
			}
		} else {
			removeStyleName("packpanel-nojava");
			addStyleName("packpanel");

			controlLayout.addStyleName("v-component-group");
			Button startPrimaryButton = new Button(Messages.getString("Caption.StartTest"));
			startPrimaryButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			//startPrimaryButton.addClickListener(this);
			startPrimaryButton.setEnabled(javaInstalled);
			if (javaInstalled) {
				startPrimaryButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						fireEvent(new StartEvent(PackPanel.this, pack, true));
					}
				});
			}
			controlLayout.addComponent(startPrimaryButton);
		
			final OpenPopupButton startSecondaryButton = new OpenPopupButton("Open");
			startSecondaryButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					fireEvent(new StartEvent(startSecondaryButton, pack, false));
				}
			});
			controlLayout.addComponent(startSecondaryButton);
		}
	}

	public class StartEvent extends Component.Event {

		public static final String EVENT_ID = "start";
		
		private Pack pack;
		private boolean primary = false;

		public StartEvent(Component source, Pack pack, boolean primary) {
			super(source);
			this.pack = pack;
			this.primary = primary;
		}
		
		public Pack getPack() {
			return pack;
		}
		
		public boolean isPrimary() {
			return primary;
		}
		
	}
	
	public interface StartListener extends Serializable {

		public static final Method START_TEST_METHOD = ReflectTools
				.findMethod(StartListener.class, "start", StartEvent.class);

		/**
		 * Called when a {@link SimpleTest} has to be started. A reference to the
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
