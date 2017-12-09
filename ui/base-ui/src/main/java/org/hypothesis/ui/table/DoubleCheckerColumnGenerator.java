/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.table;

import java.util.HashMap;
import java.util.Map;

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
		Map<Object, ButtonsHolder> map = (Map<Object, ButtonsHolder>) source.getData();
		if (null == map) {
			map = new HashMap<>();
			source.setData(map);
		}

		CssLayout group = new CssLayout();
		group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		final Button enabledButton = new Button();
		enabledButton.setIcon(FontAwesome.CHECK);
		enabledButton.addStyleName(ValoTheme.BUTTON_SMALL);
		enabledButton.setDescription(enabledCaption);

		final Button disabledButton = new Button();
		disabledButton.setIcon(FontAwesome.TIMES);
		disabledButton.addStyleName(ValoTheme.BUTTON_SMALL);
		disabledButton.setDescription(disabledCaption);

		map.put(itemId, new ButtonsHolder(enabledButton, disabledButton));
		setButtons((Status) source.getItem(itemId).getItemProperty(stateField).getValue(), enabledButton,
				disabledButton);

		enabledButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Item item = source.getItem(itemId);
				Status state = (Status) item.getItemProperty(stateField).getValue();
				Status newState = switchEnabledStatus(state);

				if (newState != state) {
					source.getItem(itemId).getItemProperty(stateField).setValue(newState);
					setButtons(newState, enabledButton, disabledButton);
				}
			}
		});

		disabledButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Item item = source.getItem(itemId);
				Status state = (Status) item.getItemProperty(stateField).getValue();
				Status newState = switchDisabledStatus(state);

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

	public static Status switchEnabledStatus(Status oldState) {
		if (null == oldState || oldState == Status.NONE || oldState == Status.DISABLED) {
			return Status.ENABLED;
		} else if (oldState == Status.ENABLED) {
			return Status.NONE;
		}
		return null;
	}

	public static Status switchDisabledStatus(Status oldState) {
		if (null == oldState || oldState == Status.NONE) {
			return Status.DISABLED;
		} else if (oldState == Status.ENABLED || oldState == Status.DISABLED) {
			return Status.NONE;
		} else if (oldState == Status.ENABLED_INHERITED) {
			return Status.DISABLED_OVERRIDE;
		} else if (oldState == Status.DISABLED_OVERRIDE) {
			return Status.ENABLED_INHERITED;
		}
		return null;
	}

	public static void setButtons(Status state, Button enabledButton, Button disabledButton) {
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

	public static class ButtonsHolder {
		public Button enabledButton;
		public Button disabledButton;

		public ButtonsHolder(Button enabledButton, Button disabledButton) {
			this.enabledButton = enabledButton;
			this.disabledButton = disabledButton;
		}
	}

}
