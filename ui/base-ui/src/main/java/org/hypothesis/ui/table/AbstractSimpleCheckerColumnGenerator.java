/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.table;

import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
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
public abstract class AbstractSimpleCheckerColumnGenerator implements ColumnGenerator {

	private final String stateField;

	private final String buttonCaption;

	public AbstractSimpleCheckerColumnGenerator(String stateField, String buttonCaption) {
		this.stateField = stateField;
		this.buttonCaption = buttonCaption;
	}

	@Override
	public Object generateCell(final Table source, final Object itemId, Object columnId) {

		Item item = source.getItem(itemId);
		Boolean state = (Boolean) item.getItemProperty(stateField).getValue();

		final Button button = new Button();
		button.setIcon(FontAwesome.CHECK);
		button.addStyleName(ValoTheme.BUTTON_SMALL);
		button.setDescription(buttonCaption);

		if (state != null && state.equals(true)) {
			button.setData(true);
			button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		} else {
			button.setData(false);
		}

		button.addClickListener(e -> {
			Item item1 = source.getItem(itemId);
			boolean checked = false;
			if (button.getData().equals(false)) {
				item1.getItemProperty(stateField).setValue(true);
				checked = true;
				button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			} else {
				item1.getItemProperty(stateField).setValue(false);
				button.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
			}
			button.setData(checked);
			onStateChanged(itemId, checked);

		});

		return button;
	}

	public abstract void onStateChanged(final Object itemId, final boolean checked);

}
