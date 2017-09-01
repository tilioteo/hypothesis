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
		Integer state = (Integer) item.getItemProperty(stateField).getValue();

		final Button enabledButton = new Button();
		enabledButton.setIcon(FontAwesome.CHECK);
		enabledButton.addStyleName(ValoTheme.BUTTON_SMALL);
		enabledButton.setDescription(enabledCaption);

		if (state != null) {
			if (state == 1) {
				enabledButton.setData(true);
				enabledButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
				enabledButton.setIcon(FontAwesome.SQUARE);
				enabledButton.setEnabled(false);
			} else if (state == 2) {
				enabledButton.setData(true);
				enabledButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			}
		} else {
			enabledButton.setData(false);
		}

		final Button disabledButton = new Button();
		disabledButton.setIcon(FontAwesome.TIMES);
		disabledButton.addStyleName(ValoTheme.BUTTON_SMALL);
		disabledButton.setDescription(disabledCaption);

		if (state != null && state == 0) {
			disabledButton.setData(true);
			disabledButton.addStyleName(ValoTheme.BUTTON_DANGER);
		} else {
			disabledButton.setData(false);
		}

		enabledButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (enabledButton.getData().equals(false)) {
					source.getItem(itemId).getItemProperty(stateField).setValue(1);
					enabledButton.setData(true);
					enabledButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
					disabledButton.setData(false);
					disabledButton.removeStyleName(ValoTheme.BUTTON_DANGER);
				} else {
					source.getItem(itemId).getItemProperty(stateField).setValue(null);
					enabledButton.setData(false);
					enabledButton.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
				}
			}
		});

		disabledButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (disabledButton.getData().equals(false)) {
					source.getItem(itemId).getItemProperty(stateField).setValue(0);
					disabledButton.setData(true);
					disabledButton.addStyleName(ValoTheme.BUTTON_DANGER);
					enabledButton.setData(false);
					enabledButton.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
				} else {
					source.getItem(itemId).getItemProperty(stateField).setValue(null);
					disabledButton.setData(false);
					disabledButton.removeStyleName(ValoTheme.BUTTON_DANGER);
				}
			}
		});

		group.addComponent(enabledButton);
		group.addComponent(disabledButton);

		return group;
	}

}
