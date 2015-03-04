/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import com.tilioteo.hypothesis.servlet.HibernateVaadinServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Theme("valo")
public class ManagerUI extends UI {

	@WebServlet(value = "/manager/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ManagerUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends HibernateVaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		// Create the content root layout for the UI
        VerticalLayout content = new VerticalLayout();
        setContent(content);

        // Display the greeting
        content.addComponent(new com.vaadin.ui.Label("Hello World!"));

        // Have a clickable button        
        content.addComponent(new com.vaadin.ui.Button("Push Me!",
            new com.vaadin.ui.Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent e) {
                    Notification.show("Pushed!");
                }
            }));	}
}
