/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.Date;

import javax.servlet.annotation.WebServlet;

import net.engio.mbassy.listener.Handler;

import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.HypothesisEvent.GuestAccessRequestedEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.InvalidLoginEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.InvalidUserPermissionEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.UserLoggedOutEvent;
import com.tilioteo.hypothesis.event.HypothesisEvent.UserLoginRequestedEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.tilioteo.hypothesis.persistence.UserService;
import com.tilioteo.hypothesis.server.SessionUtils;
import com.tilioteo.hypothesis.servlet.HibernateVaadinServlet;
import com.tilioteo.hypothesis.ui.view.HypothesisViewType;
import com.tilioteo.hypothesis.ui.view.LoginView;
import com.tilioteo.hypothesis.ui.view.MainView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
@Title("Hypothesis")
@Theme("hypothesis")
public class MainUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends HibernateVaadinServlet {
	}

	private String pid = null;
	
	private UserService userService;
	
	private MainView mainView = null;
	
	@Override
	protected void init(VaadinRequest request) {
		super.init(request);
		
		MainEventBus.get().register(this);
		
		userService = UserService.newInstance();
		
		pid = request.getParameter("pid");

		updateContent();
	}
	
	private MainView getMainView() {
		if (null == mainView) {
			mainView =  new MainView();
		}
		return mainView;
	}
	
    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateContent() {
        User user = SessionUtils.getAttribute(User.class);
        if (user != null) {
            // Authenticated user
            setContent(getMainView());
            removeStyleName("loginview");
            if (!User.GUEST.equals(user)) {
            	getNavigator().navigateTo(HypothesisViewType.PACKS.getViewName());
            } else {
            	getNavigator().navigateTo(HypothesisViewType.PUBLIC.getViewName());
            }
        } else {
            setContent(new LoginView());
            addStyleName("loginview");
        }
    }
    
    protected void setUser(User user) {
    	SessionUtils.setAttribute(User.class, user);
    }
    
    private boolean userCanLogin(User user) {
    	if (user != null) {
    		if (user.getEnabled() != null && user.getEnabled().booleanValue()) {
    			Date expired = user.getExpireDate();
    			Date now = new Date();
    			if (null == expired || expired.after(now)) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }

    @Handler
    public void userLoginRequested(final UserLoginRequestedEvent event) {
        User user = userService.findByUsernamePassword(event.getUserName(), event.getPassword());
        
        if (user != null) {
        	
        	if (userCanLogin(user)) {
               	setUser(user);
               	updateContent();
        	} else {
        		MainEventBus.get().post(new InvalidUserPermissionEvent());
        	}
        } else {
        	MainEventBus.get().post(new InvalidLoginEvent());
        }
    }

    @Handler
    public void guestAccessRequested(final GuestAccessRequestedEvent event) {
        setUser(User.GUEST);
        
        updateContent();
    }

    @Handler
    public void userLoggedOut(final UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
    	SessionUtils.clearAttribute(User.class);
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }


}
