/**
 * 
 */
package com.tilioteo.hypothesis.ui.view;

import com.tilioteo.hypothesis.ui.PackPanel;
import com.tilioteo.hypothesis.ui.Timer;
import com.tilioteo.hypothesis.ui.TimerLabel;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author kamil
 *
 */
public class PacksView extends HypothesisView {
	
	public PacksView() {
		setSizeFull();
		
		
		/*VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		addComponent(verticalLayout);
		
		Panel topPanel = new Panel();
		topPanel.setWidth("100%");
		topPanel.setHeight("100px");
		verticalLayout.addComponent(topPanel);
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		verticalLayout.addComponent(horizontalLayout);
		verticalLayout.setExpandRatio(horizontalLayout, 1.0f);
		
		Panel leftPanel = new Panel();
		//leftPanel.setWidth("20%");
		//leftPanel.setHeight("100%");
		leftPanel.setSizeFull();
		horizontalLayout.addComponent(leftPanel);
		horizontalLayout.setExpandRatio(leftPanel, 0.2f);
		
		Panel mainPanel = new Panel();
		mainPanel.setSizeFull();
		horizontalLayout.addComponent(mainPanel);
		horizontalLayout.setExpandRatio(mainPanel, 1.0f);
		
		VerticalLayout packLayout = new VerticalLayout();
		packLayout.setHeight(null);
		packLayout.setWidth("100%");
		mainPanel.setContent(packLayout);

		
		for (int i = 1; i <= 7; ++i) {
			final int num = i;
			PackPanel packPanel = new PackPanel();
			packPanel.addClickListener(new MouseEvents.ClickListener() {
				
				@Override
				public void click(ClickEvent event) {
					Notification.show(String.format("Clicked panel %d", num));
				}
			});
			packLayout.addComponent(packPanel);
		}
		*/
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
