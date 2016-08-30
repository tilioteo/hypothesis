/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.jar.Attributes;

import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;
import org.hypothesis.utility.ManifestUtility;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class VersionInfoPresenter extends AbstractWindowPresenter {

	private static String VERSION;
	private static String VERSION_ADDITIONAL;
	private static String VERSION_SPECIFIC;
	private static String SPECIFICATION_VERSION;

	public VersionInfoPresenter(MainEventBus bus) {
		super(bus);

		Attributes attributes = ManifestUtility.getManifestAttributes();

		VERSION = attributes.getValue(ManifestUtility.VERSION);
		VERSION_ADDITIONAL = attributes.getValue(ManifestUtility.VERSION_ADDITIONAL);
		VERSION_SPECIFIC = attributes.getValue(ManifestUtility.VERSION_SPECIFIC);
		SPECIFICATION_VERSION = attributes.getValue(ManifestUtility.SPECIFICATION_VERSION);
	}

	@Override
	protected void initFields() {
	}

	@Override
	protected void fillFields() {
	}

	@Override
	protected void clearFields() {
	}

	@Override
	protected void buildContent() {
		VerticalLayout content = new VerticalLayout();
		content.setSizeFull();
		window.setContent(content);

		content.addComponent(buildVersionInfo());
		content.addComponent(buildDetailVersionInfo());
		content.addComponent(buildAdditionalVersionInfo());
		content.addComponent(buildVaadinVersionInfo());

		content.addComponent(buildFooter());
	}

	private Component buildDetailVersionInfo() {
		return new Label("Version detail: " + VERSION_SPECIFIC);
	}

	private Component buildAdditionalVersionInfo() {
		return new Label("Additional version information: " + VERSION_ADDITIONAL);
	}

	private Component buildVaadinVersionInfo() {
		return new Label("Vaadin version: " + SPECIFICATION_VERSION);
	}

	private Component buildVersionInfo() {
		return new Label("Hypothesis version: " + VERSION);
	}

	private Component buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
		footer.setSpacing(true);

		Button ok = new Button(Messages.getString("Caption.Button.OK"));
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				window.close();
			}
		});

		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

		return footer;
	}

}
