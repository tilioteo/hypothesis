/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.hypothesis.interfaces.UIPresenter;

import java.util.Locale;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class ControlledUI extends UI {

	private UIPresenter presenter;

	@Override
	protected void init(VaadinRequest request) {
		presenter.initialize(request);

		setLocale(presenter.getCurrentLocale());
	}

	@Override
	protected void refresh(VaadinRequest request) {
		super.refresh(request);

		presenter.refresh(request);
	}

	@Override
	public void setResource(String key, Resource resource) {
		super.setResource(key, resource);
	}

	@Override
	public void close() {
		super.close();

		presenter.close();
	}

	protected void setPresenter(UIPresenter presenter) {
		this.presenter = presenter;
	}

	public static ControlledUI getCurrent() {
		UI current = UI.getCurrent();
		if (current instanceof ControlledUI) {
			return (ControlledUI) current;
		}

		return null;
	}

	public static String getCurrentLanguage() {
		ControlledUI ui = getCurrent();
		if (ui != null) {
			Locale locale = ui.presenter.getCurrentLocale();
			if (locale != null) {
				return locale.getLanguage();
			}
		}

		return null;
	}

}
