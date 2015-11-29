/**
 * 
 */
package com.tilioteo.hypothesis.navigator;

import com.tilioteo.hypothesis.eventbus.MainEventBus;
import com.tilioteo.hypothesis.interfaces.ViewPresenter;
import com.tilioteo.hypothesis.presenter.HasMainEventBus;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class HypothesisViewProvider implements ViewProvider {

	private MainEventBus bus;

	private HypothesisViewType viewType;

	// This field caches an already initialized view instance if the
	// view should be cached (stateful views).
	private View cachedInstance = null;

	private ViewPresenter presenter = null;

	public HypothesisViewProvider(MainEventBus bus, HypothesisViewType viewType) {
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

				if (presenter instanceof HasMainEventBus) {
					((HasMainEventBus) presenter).setMainEventBus(bus);
				}
			} catch (InstantiationException | IllegalAccessException e) {
			}
		}

		return presenter.createView();
	}

}
