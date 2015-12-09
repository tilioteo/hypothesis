package org.hypothesis.ui.table;

import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 * Hypothesis
 *
 */
@SuppressWarnings({ "serial", "unchecked" })
public class SimpleCheckerColumnGenerator implements ColumnGenerator {

	private String stateField;

	private String buttonCaption = "buttonCaption";

	public SimpleCheckerColumnGenerator(String stateField) {
		this.stateField = stateField;
	}

	@Override
	public Object generateCell(final Table source, final Object itemId, Object columnId) {

		Item item = source.getItem(itemId);
		Boolean state = (Boolean) item.getItemProperty(stateField).getValue();

		final Button button = new Button();
		button.setIcon(FontAwesome.CHECK);
		button.addStyleName(ValoTheme.BUTTON_SMALL);
		button.setDescription(buttonCaption);// Messages.getString("Caption.Button.EnableTest")

		if (state != null && state.equals(true)) {
			button.setData(true);
			button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		} else {
			button.setData(false);
		}

		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (button.getData().equals(false)) {
					source.getItem(itemId).getItemProperty(stateField).setValue(true);
					button.setData(true);
					button.addStyleName(ValoTheme.BUTTON_FRIENDLY);
				} else {
					source.getItem(itemId).getItemProperty(stateField).setValue(false);
					button.setData(false);
					button.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
				}
			}
		});

		return button;
	}

	public void setButtonCaption(String caption) {
		this.buttonCaption = caption;
	}

}
