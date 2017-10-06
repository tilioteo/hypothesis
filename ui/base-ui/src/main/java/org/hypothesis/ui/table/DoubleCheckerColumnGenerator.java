/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.table;

import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class DoubleCheckerColumnGenerator implements ColumnGenerator {
	
	public enum Status {
		NONE,
		DISABLED,
		DISABLED_OVERRIDE,
		ENABLED,
		ENABLED_INHERITED
	}

	private final String stateField;

	private String enabledCaption;
	private String disabledCaption;

	public DoubleCheckerColumnGenerator(String stateField, String enabledCaption, String disabledCaption) {
		this.stateField = stateField;
		this.enabledCaption = enabledCaption;
		this.disabledCaption = disabledCaption;
	}

	@Override
	public Object generateCell(final Table source, final Object itemId, Object columnId) {
		CssLayout group = new CssLayout();
		group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		Item item = source.getItem(itemId);
		Status state = (Status) item.getItemProperty(stateField).getValue();

		final Button enabledButton = new Button();
		enabledButton.setIcon(FontAwesome.CHECK);
		enabledButton.addStyleName(ValoTheme.BUTTON_SMALL);
		enabledButton.setDescription(enabledCaption);

		final Button disabledButton = new Button();
		disabledButton.setIcon(FontAwesome.TIMES);
		disabledButton.addStyleName(ValoTheme.BUTTON_SMALL);
		disabledButton.setDescription(disabledCaption);
		
		setButtons(state, enabledButton, disabledButton);

		enabledButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Status state = (Status) source.getItem(itemId).getItemProperty(stateField).getValue();
				Status newState = null;
				if (state == Status.NONE || state == Status.DISABLED) {
					newState = Status.ENABLED;
				} else if (state == Status.ENABLED) {
					newState = Status.NONE;
				}
				
				if (newState != state) {
					source.getItem(itemId).getItemProperty(stateField).setValue(newState);
					setButtons(newState, enabledButton, disabledButton);
				}
			}
		});

		disabledButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Status state = (Status) source.getItem(itemId).getItemProperty(stateField).getValue();
				Status newState = null;
				if (state == Status.NONE) {
					newState = Status.DISABLED;
				} else if (state == Status.ENABLED || state == Status.DISABLED) {
					newState = Status.NONE;
				} else if (state == Status.ENABLED_INHERITED) {
					newState = Status.DISABLED_OVERRIDE;
				} else if (state == Status.DISABLED_OVERRIDE) {
					newState = Status.ENABLED_INHERITED;
				}

				if (newState != state) {
					source.getItem(itemId).getItemProperty(stateField).setValue(newState);
					setButtons(newState, enabledButton, disabledButton);
				}
			}
		});

		group.addComponent(enabledButton);
		group.addComponent(disabledButton);

		return group;
	}

	private void setButtons(Status state, Button enabledButton, Button disabledButton) {
		enabledButton.setIcon(FontAwesome.CHECK);
		enabledButton.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
		enabledButton.setEnabled(true);

		disabledButton.removeStyleName(ValoTheme.BUTTON_DANGER);
		disabledButton.setEnabled(true);
		
		if (state != null && state != Status.NONE) {
			switch (state) {
			case DISABLED:
				enabledButton.removeStyleName(ValoTheme.BUTTON_FRIENDLY);

				disabledButton.addStyleName(ValoTheme.BUTTON_DANGER);
				break;
			case DISABLED_OVERRIDE:
				enabledButton.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
				enabledButton.setIcon(FontAwesome.SQUARE);
				enabledButton.setEnabled(false);

				disabledButton.addStyleName(ValoTheme.BUTTON_DANGER);
				break;
			case ENABLED:
				enabledButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
				break;
			case ENABLED_INHERITED:
				enabledButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
				enabledButton.setIcon(FontAwesome.SQUARE);
				enabledButton.setEnabled(false);
				break;

			default:
				break;
			}
		}
	}

}
