/**
 * 
 */
package org.hypothesis.common.application.ui;

import org.hypothesis.common.application.AbstractBaseApplication;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Base application main window holds typed reference of application
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractMainWindow<T extends AbstractBaseApplication> extends
		BaseWindow {

	private final T application;

	public AbstractMainWindow(final T application) {
		super();
		this.application = application;

		init();
		Component content = createContent();

		if (content != null) {
			if (content instanceof ComponentContainer) {
				setContent((ComponentContainer) content);
			} else {
				addComponent(content);
			}
		}

		afterInit();
	}

	protected void afterInit() {

	}

	protected abstract Component createContent();

	public T getApp() {
		return application;
	}

	protected void init() {

	}

}
