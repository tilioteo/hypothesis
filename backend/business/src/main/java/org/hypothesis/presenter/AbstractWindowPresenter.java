/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.ArrayList;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractWindowPresenter implements CloseListener {

	protected Object source = null;

	protected User loggedUser;

	protected WindowState state;

	protected MainEventBus bus;

	protected ArrayList<AbstractField<?>> fields;

	protected Window window;

	protected TabSheet tabSheet;
	protected VerticalLayout detailsTab;

	protected abstract void initFields();

	protected abstract void fillFields();

	protected abstract void clearFields();

	protected abstract void buildContent();

	protected AbstractWindowPresenter(MainEventBus bus) {
		this.bus = bus;
	}

	protected void createWindow() {
		window = new Window();
		window.addCloseListener(this);
		window.setCloseShortcut(KeyCode.ESCAPE, null);
		window.setResizable(false);
		window.setClosable(false);
		window.setWidth(50, Unit.PERCENTAGE);
		window.setHeight(80.0f, Unit.PERCENTAGE);
		window.setModal(true);

		initFields();

		if (state.equals(WindowState.UPDATE)) {
			fillFields();
		}

		buildContent();
	}

	protected void addInformationLabel(AbstractOrderedLayout layout) {
		Label info = new Label(Messages.getString("Caption.Label.ChooseFields"));
		info.setSizeUndefined();
		layout.addComponent(info);
		layout.setComponentAlignment(info, Alignment.TOP_CENTER);
	}

	protected void addField(GridLayout form, final Component field) {
		if (state.equals(WindowState.MULTIUPDATE)) {
			final CheckBox enabler = new CheckBox(field.getCaption());
			enabler.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(Property.ValueChangeEvent event) {
					field.setVisible(enabler.getValue());

					if (field instanceof AbstractField<?>) {
						if (enabler.getValue()) {
							fields.add((AbstractField<?>) field);
						} else {
							fields.remove((AbstractField<?>) field);
						}
					}
				}
			});
			field.setVisible(false);
			form.addComponent(enabler);
			form.setComponentAlignment(enabler, Alignment.TOP_LEFT);
		} else {
			if (field instanceof AbstractField<?>) {
				fields.add((AbstractField<?>) field);
			}
			Label caption = new Label(field.getCaption());
			caption.setSizeUndefined();
			form.addComponent(caption);
			form.setComponentAlignment(caption, Alignment.TOP_LEFT);
		}

		field.setCaption(null);
		form.addComponent(field);
		form.setComponentAlignment(field, Alignment.TOP_LEFT);
	}

	protected void showWindow(WindowState state, Object source) {
		this.state = state;
		this.source = source;

		this.loggedUser = SessionManager.getLoggedUser();

		createWindow();

		bus.post(new MainUIEvent.CloseOpenWindowsEvent());
		UI.getCurrent().addWindow(window);
		window.center();
		window.focus();
	}

	public void showWindow() {
		showWindow(WindowState.CREATE, null);
	}

	@Override
	public void windowClose(CloseEvent e) {
		clearFields();

		state = null;
		source = null;

		window = null;
	}

}
