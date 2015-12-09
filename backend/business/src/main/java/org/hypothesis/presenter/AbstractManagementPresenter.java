/**
 * 
 */
package org.hypothesis.presenter;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.User;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.interfaces.ManagementPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.view.ManagementView;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractManagementPresenter
		implements ManagementPresenter, HasMainEventBus, ColumnGenerator, Listener {

	protected User loggedUser;

	protected MainEventBus bus;

	protected CssLayout buttonGroup;
	protected Table table;

	protected ConfirmDialog deletionConfirmDialog;

	protected boolean allSelected = false;

	protected abstract Button buildAddButton();

	protected abstract ComboBox buildSelection();

	protected abstract Button buildUpdateButton();

	protected abstract Button buildDeleteButton();

	protected abstract Resource getExportResource();

	@Override
	public void setMainEventBus(MainEventBus bus) {
		this.bus = bus;
	}

	@Override
	public void attach() {
		bus.register(this);
	}

	@Override
	public void detach() {
		bus.unregister(this);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	protected Component buildTools() {
		HorizontalLayout tools = new HorizontalLayout();
		tools.setSpacing(true);

		tools.addComponent(buildAddButton());
		tools.addComponent(buildSelection());

		buttonGroup = new CssLayout();
		buttonGroup.addStyleName("v-component-group");
		buttonGroup.addComponent(buildUpdateButton());
		buttonGroup.addComponent(buildDeleteButton());
		buttonGroup.addComponent(buildExportButton());
		buttonGroup.setEnabled(false);
		tools.addComponent(buttonGroup);

		return tools;
	}

	private Component buildExportButton() {
		Button exportButton = new Button(Messages.getString("Caption.Button.Export"));
		Resource exportResource = getExportResource();
		FileDownloader fileDownloader = new FileDownloader(exportResource);
		fileDownloader.extend(exportButton);
		return exportButton;
	}

	@Override
	public View createView() {
		loggedUser = SessionManager.getLoggedUser();

		return new ManagementView(this);
	}

}
