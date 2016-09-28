/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.navigator;

import javax.inject.Inject;

import org.hypothesis.business.SessionManager;
import org.hypothesis.cdi.Main;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.EventBus;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.event.interfaces.MainUIEvent.PostViewChangeEvent;
import org.hypothesis.eventbus.MainEventBus;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class HypothesisNavigator extends Navigator {

	private static final HypothesisViewType ERROR_VIEW = HypothesisViewType.PACKS;
	private ViewProvider errorViewProvider;

	@Inject
	@Main
	private EventBus bus;

	/**
	 * Create instance and associate it with bus and container
	 * 
	 * @param bus
	 * @param container
	 */
	public HypothesisNavigator(final ComponentContainer container) {
		super(UI.getCurrent(), container);

		initViewChangeListener();
		initViewProviders();
	}

	private void initViewChangeListener() {
		addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(final ViewChangeEvent event) {
				HypothesisViewType view = HypothesisViewType.getByViewName(event.getViewName());

				User user = SessionManager.getLoggedUser();
				if (user != null) {
					return view.isAllowed(user.getRoles());
				}
				return false;
			}

			@Override
			public void afterViewChange(final ViewChangeEvent event) {
				// Appropriate events get fired after the view is changed.
				bus.post(new PostViewChangeEvent(event.getViewName()));
			}
		});
	}

	private void initViewProviders() {
		// A dedicated view provider is added for each separate view type
		for (final HypothesisViewType viewType : HypothesisViewType.values()) {
			ViewProvider viewProvider = new HypothesisViewProvider(/*bus,*/ viewType);

			addProvider(viewProvider);
		}

		setErrorProvider(new ViewProvider() {
			@Override
			public String getViewName(final String viewAndParameters) {
				return ERROR_VIEW.getViewName();
			}

			@Override
			public View getView(final String viewName) {
				return errorViewProvider.getView(ERROR_VIEW.getViewName());
			}
		});
	}
}
