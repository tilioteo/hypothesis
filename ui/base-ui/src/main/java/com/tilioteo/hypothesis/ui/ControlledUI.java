/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.Locale;

import com.tilioteo.hypothesis.interfaces.HasUIPresenter;
import com.tilioteo.hypothesis.interfaces.UIPresenter;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class ControlledUI extends UI implements HasUIPresenter {

	private UIPresenter presenter;

	/**
	 * current language
	 */
	private String language = null;

	@Override
	protected void init(VaadinRequest request) {
		language = request.getParameter("lang");
		if (null == language) {
			language = "cs"; // default
		}

		setLocale(new Locale(language));

		presenter.initialize(request);
	}

	@Override
	protected void refresh(VaadinRequest request) {
		super.refresh(request);

		presenter.refresh(request);
	}
	
	@Override
	public void attach() {
		super.attach();
		
		presenter.attach();
	}

	@Override
	public void detach() {
		presenter.detach();

		super.detach();
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

	@Override
	public UIPresenter getPresenter() {
		return presenter;
	}

	@Override
	public void setPresenter(UIPresenter presenter) {
		this.presenter = presenter;
	}

	public String getLanguage() {
		return language;
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
			return ui.getLanguage();
		}

		return null;
	}
}
