/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.event.HypothesisEvent.PostViewChangeEvent;
import com.tilioteo.hypothesis.event.MainEventBus;
import com.tilioteo.hypothesis.ui.view.HypothesisViewType;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComponentContainer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class HypothesisNavigator extends Navigator {

	private static final HypothesisViewType ERROR_VIEW = HypothesisViewType.PACKS;
	private ViewProvider errorViewProvider;

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
				
				User user = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
				if (user != null) {
					return view.isAllowed(user.getRoles());
				}
				return false;
			}

			@Override
			public void afterViewChange(final ViewChangeEvent event) {
				HypothesisViewType view = HypothesisViewType.getByViewName(event.getViewName());
				// Appropriate events get fired after the view is changed.
				MainEventBus.get().post(new PostViewChangeEvent(view));
				//MainEventBus.get().post(new BrowserResizeEvent());
				//MainEventBus.get().post(new CloseOpenWindowsEvent());
			}
		});
	}

	private void initViewProviders() {
		// A dedicated view provider is added for each separate view type
		for (final HypothesisViewType viewType : HypothesisViewType.values()) {
			ViewProvider viewProvider = new ClassBasedViewProvider(
					viewType.getViewName(), viewType.getViewClass()) {

				// This field caches an already initialized view instance if the
				// view should be cached (stateful views).
				private View cachedInstance;

				@Override
				public View getView(final String viewName) {
					View result = null;
					if (viewType.getViewName().equals(viewName)) {
						if (viewType.isStateful()) {
							// Stateful views get lazily instantiated
							if (cachedInstance == null) {
								cachedInstance = super.getView(viewType.getViewName());
							}
							result = cachedInstance;
						} else {
							// Non-stateful views get instantiated every time
							// they're navigated to
							result = super.getView(viewType.getViewName());
						}
					}
					return result;
				}
			};

			if (viewType == ERROR_VIEW) {
				errorViewProvider = viewProvider;
			}

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
