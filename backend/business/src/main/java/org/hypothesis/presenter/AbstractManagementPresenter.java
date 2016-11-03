/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Table.ColumnGenerator;
import org.hypothesis.interfaces.ManagementPresenter;
import org.hypothesis.server.Messages;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractManagementPresenter implements ManagementPresenter, ColumnGenerator, Listener {

	protected CssLayout buttonGroup;
	protected Table table;

	protected ConfirmDialog deletionConfirmDialog;

	protected boolean allSelected = false;

	protected abstract Button buildAddButton();

	protected abstract ComboBox buildSelection();

	protected abstract Button buildUpdateButton();

	protected abstract Button buildDeleteButton();

	protected abstract Resource getExportResource();

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

}
