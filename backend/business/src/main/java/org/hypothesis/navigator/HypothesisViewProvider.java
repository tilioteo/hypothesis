/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.navigator;

import javax.inject.Inject;

import org.hypothesis.cdi.Main;
import org.hypothesis.event.interfaces.EventBus;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.ManagementPresenter;
import org.hypothesis.interfaces.ViewPresenter;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class HypothesisViewProvider implements ViewProvider {

	@Inject
	@Main
	private EventBus bus;

	private final HypothesisViewType viewType;

	// This field caches an already initialized view instance if the
	// view should be cached (stateful views).
	private View cachedInstance = null;

	private ViewPresenter presenter = null;

	/**
	 * Create instance and associate it with bus and view type
	 * 
	 * @param bus
	 * @param viewType
	 */
	public HypothesisViewProvider(/*MainEventBus bus,*/ HypothesisViewType viewType) {
		this.bus = bus;
		this.viewType = viewType;
	}

	@Override
	public String getViewName(String navigationState) {
		if (null == navigationState || null == viewType) {
			return null;
		}

		String viewName = viewType.getViewName();
		if (navigationState.equals(viewName) || navigationState.startsWith(viewName + "/")) {
			return viewName;
		}

		return null;
	}

	@Override
	public View getView(final String viewName) {
		View result = null;

		if (viewType.getViewName().equals(viewName)) {
			if (viewType.isStateful()) {
				// Stateful views get lazily instantiated
				if (cachedInstance == null) {
					cachedInstance = createView();
				}
				result = cachedInstance;
			} else {
				// Non-stateful views get instantiated every time
				// they're navigated to
				result = createView();
			}
		}
		return result;
	}

	private View createView() {
		if (null == presenter) {
			try {
				presenter = viewType.getViewPresenterClass().newInstance();

				/*if (presenter instanceof HasMainEventBus) {
					((HasMainEventBus) presenter).setMainEventBus(bus);
				}

				if (presenter instanceof ManagementPresenter) {
					((ManagementPresenter) presenter).init();
				}*/

				return presenter.createView();

			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
