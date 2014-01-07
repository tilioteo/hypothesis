package org.hypothesis.application.hypothesis.ui;

import java.util.Set;

import org.hypothesis.Globals;
import org.hypothesis.application.HypothesisApplication;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Token;
import org.hypothesis.entity.User;
import org.hypothesis.persistence.TokenManager;
import org.hypothesis.persistence.hibernate.TokenDao;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * The class represents available packs list
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class ListPacks extends VerticalLayout implements ClickListener {

	private static final long serialVersionUID = -1972069125917529719L;

	private Set<Pack> userPacks = null;
	private Pack selectedPack = null;
	private Button startButton;
	private Button cancelButton;
	private Window infoWindow;

	private HypothesisApplication application;

	/**
	 * Constructor
	 * 
	 * @param manager
	 *            - manager application
	 */
	public ListPacks(final HypothesisApplication app) {
		this.application = app;
		// set layout
		this.setSpacing(true);
		this.setMargin(true);

		// heading
		// Label heading = new Label("<h2>" +
		// ApplicationMessages.get().getMessage(Messages.TEXT_ENABLED_TESTS) + "</h2>");
		// heading.setContentMode(Label.CONTENT_XHTML);
		// addComponent(heading);

		// publish the packs
		Long userId = app.getCurrentUser().getId();
		User user = app.getUserGroupManager().findUser(userId);

		userPacks = app.getPermitionManager().findUserPacks(user, true);
		if (userPacks != null) {
			for (Pack pack : userPacks) {
				String caption = pack.getName() + " - " + pack.getDescription();
				Button button = new Button(caption);
				button.setData(pack);
				addComponent(button);
				this.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
				button.addListener(this);
			}
		}
	}

	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		if (source == startButton) {
			// start testing of pack
			if (selectedPack != null) {
				// TODO udelat checkbox pro spusteni testu s priznakem
				// PRODUCTION=FALSE,
				// ktery oznacuje, ze data budou testovaci
				// tuto volbu bude mit jen uzivatel s roli MANAGER nebo
				// SUPERUSER
				// Pokud bude test jiz odstartovany - bude ve stavu
				// BROKEN_BY_CLIENT nebo BROKEN_BY_USER,
				// potom uz nebude mozne zmenit priznak a checkbox by mel byt
				// disabled
				boolean production = false;
				Token token = new TokenManager(new TokenDao()).createToken(
						application.getCurrentUser(), selectedPack, production);

				String urlString;
				if (Globals.USE_HYPOTHESIS_BROWSER) {
					// open in hypothesis browser
					// TODO replace with string constants
					
					urlString = application.getURL().toString()
							+ "service/launch.jnlp?application_url="
							+ application.getURL().toString()
							+ "collector/&token=" + token.getUid();
					((JnlpButton) source).openRequest(urlString);
					

				} else {
					// open in new window
					urlString = application.getURL().toString()
							+ "collector/?token=" + token.getUid();
					application.getMainWindow().open(
							new ExternalResource(urlString), "_blank");
				}
				// application.getMainWindow().open(new
				// ExternalResource(urlString),
				// "jnlp_startup"/*"_blank"*/,1,1,0);

				// TODO find a way to close info window
				/*
				 * timer = new Timer(); timer.schedule(new TimerTask() {
				 * 
				 * @Override public void run() {
				 * getWindow().removeWindow(infoWindow); } }, 7000);
				 */

				// getWindow().removeWindow(infoWindow);
			}
		}

		else if (source == cancelButton) {
			selectedPack = null;
			getWindow().removeWindow(infoWindow);
		}

		// any of the pack button
		else {
			if (source.getData() != null && source.getData() instanceof Pack) {
				selectedPack = (Pack) source.getData();
			}
			showInfoWindow(source.getCaption());
		}
	}

	private void showInfoWindow(String caption) {
		infoWindow = new Window();
		infoWindow.setWidth(50, UNITS_PERCENTAGE);
		infoWindow.setModal(true);
		infoWindow.setCaption(caption);
		VerticalLayout vl = new VerticalLayout();
		infoWindow.addComponent(vl);

		vl.addComponent(new Label(ApplicationMessages.get().getString(
				Messages.TEXT_TEST_INFO)));

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.setSpacing(true);
		
		/*JavaApplet applet = new JavaApplet("/service/hypothesis-browser-1.0.1.jar", "org.hypothesis.loader.BrowserApplet");
		applet.setHeight("300px");
		applet.setWidth("300px");
		
		vl.addComponent(applet);*/
		
		startButton = new JnlpButton(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_START));
		startButton.addStyleName("big default");
		startButton.addListener(this);
		buttons.addComponent(startButton);
		
		cancelButton = new Button(ApplicationMessages.get().getString(
				Messages.TEXT_BUTTON_CANCEL));
		cancelButton.addListener(this);
		buttons.addComponent(cancelButton);
		vl.addComponent(buttons);
		vl.setComponentAlignment(buttons, Alignment.BOTTOM_CENTER);

		getWindow().addWindow(infoWindow);
	}

}
