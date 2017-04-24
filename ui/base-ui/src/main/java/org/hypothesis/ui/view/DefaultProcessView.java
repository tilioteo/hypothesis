/**
 * 
 */
package org.hypothesis.ui.view;

import org.hypothesis.ui.ProcessUI;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CssLayout;

/**
 * @author morongk
 *
 */
@SuppressWarnings("serial")
@CDIView(value = "", uis = { ProcessUI.class })
public class DefaultProcessView extends CssLayout implements View {
	
	public DefaultProcessView() {
		setSizeFull();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// noop
	}

}
