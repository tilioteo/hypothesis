/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.view;

import org.hypothesis.interfaces.SlideManagementPresenter;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideManagementView extends HorizontalLayout implements UIView {

	private final SlideManagementPresenter presenter;

	private AceEditor editor1;
	private AceEditor editor2;

	public SlideManagementView(SlideManagementPresenter presenter) {
		this.presenter = presenter;

		setSizeFull();

		Panel contentPanel = buildContentPanel();
		addComponent(contentPanel);
		setExpandRatio(contentPanel, 1.0f);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		presenter.enter(event);
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

	private Panel buildContentPanel() {
		Panel panel = new Panel();
		panel.setSizeFull();

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		panel.setContent(mainLayout);

		Panel controlPanel = new Panel();
		controlPanel.setHeight("50px");
		controlPanel.setWidth(100, Unit.PERCENTAGE);
		mainLayout.addComponent(controlPanel);

		Button showButton = new Button("Show");
		showButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showSlide();
			}
		});

		controlPanel.setContent(showButton);

		// VerticalSplitPanel splitPanel = new VerticalSplitPanel();
		// splitPanel.setSizeFull();
		// mainLayout.addComponent(splitPanel);
		// mainLayout.setExpandRatio(splitPanel, 1.0f);

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		mainLayout.addComponent(verticalLayout);
		mainLayout.setExpandRatio(verticalLayout, 1.0f);

		editor1 = new AceEditor();
		editor1.setSizeFull();
		editor1.setMode(AceMode.xml);
		// editor1.setTheme("ace/theme/eclipse");

		editor2 = new AceEditor();
		editor2.setSizeFull();
		editor2.setMode(AceMode.xml);

		// splitPanel.setFirstComponent(editor1);
		// splitPanel.setSecondComponent(editor2);

		verticalLayout.addComponent(editor1);
		// verticalLayout.setExpandRatio(editor1, 0.5f);

		verticalLayout.addComponent(editor2);
		// verticalLayout.setExpandRatio(editor2, 0.5f);

		return panel;
	}

	protected void showSlide() {
		presenter.showSlide(editor1.getValue(), editor2.getValue());
	}

}
