/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.HasMainEventBus;
import org.hypothesis.server.Messages;

import java.util.ArrayList;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public abstract class AbstractWindowPresenter implements HasMainEventBus, CloseListener {

    protected Object source = null;

    protected User loggedUser;

    protected WindowState state;

    protected ArrayList<AbstractField<?>> fields;

    protected Window window;

    protected TabSheet tabSheet;
    protected VerticalLayout detailsTab;

    protected abstract void initFields();

    protected abstract void fillFields();

    protected abstract void clearFields();

    protected abstract void buildContent();

    protected void createWindow() {
        window = new Window();
        window.addCloseListener(this);
        window.addCloseShortcut(KeyCode.ESCAPE);
        window.setResizable(false);
        window.setClosable(false);
        window.setWidth(70, Unit.PERCENTAGE);
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
            enabler.addValueChangeListener(e -> {
                field.setVisible(enabler.getValue());

                if (field instanceof AbstractField<?>) {
                    if (enabler.getValue()) {
                        fields.add((AbstractField<?>) field);
                    } else {
                        fields.remove(field);
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

        getBus().post(new MainUIEvent.CloseOpenWindowsEvent());
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
