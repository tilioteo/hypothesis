/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.navigator;

import org.hypothesis.eventbus.HasMainEventBus;
import org.hypothesis.eventbus.MainEventBus;
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
public class HypothesisViewProvider implements ViewProvider, HasMainEventBus {

	private final HypothesisViewType viewType;

	// This field caches an already initialized view instance if the
	// view should be cached (stateful views).
	private View cachedInstance = null;

	private ViewPresenter presenter = null;

	public HypothesisViewProvider(HypothesisViewType viewType) {
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
				presenter.init();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return presenter.createView();
	}

}
