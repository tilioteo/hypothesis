/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import org.hypothesis.interfaces.ManagementPresenter;
import org.hypothesis.ui.view.ManagementView;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractManagementPresenter extends AbstractMainBusPresenter
		implements ManagementPresenter, Listener {

	protected CssLayout buttonGroup;
	protected AbstractSelect table;

	protected ConfirmDialog deletionConfirmDialog;

	protected boolean allSelected = false;

	protected abstract Button buildAddButton();

	protected abstract ComboBox buildSelection();

	protected abstract Button buildUpdateButton();

	protected abstract Button buildDeleteButton();

	protected abstract Resource getExportResource();

	@Override
	public void enter(ViewChangeEvent event) {
	}

	protected Component buildTools() {
		HorizontalLayout tools = new HorizontalLayout();
		tools.setSpacing(true);

		tools.addComponent(buildAddButton());
		tools.addComponent(buildUpdateButton());

		return tools;
	}

	@Override
	public View createView() {
		return new ManagementView(this);
	}
}
