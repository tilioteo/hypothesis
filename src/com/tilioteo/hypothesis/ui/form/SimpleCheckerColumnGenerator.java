package com.tilioteo.hypothesis.ui.form;

import com.tilioteo.hypothesis.core.Messages;
import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.ColumnGenerator;

@SuppressWarnings({ "serial", "unchecked" })
public class SimpleCheckerColumnGenerator implements ColumnGenerator {

private String stateField;
	
	public SimpleCheckerColumnGenerator(String stateField) {
		this.stateField = stateField;
	}

	@Override
	public Object generateCell(final Table source,
			final Object itemId, Object columnId) {
        
        Item item = source.getItem(itemId);
        Boolean state = (Boolean) item.getItemProperty(
        		stateField).getValue(); 

        final Button button = new Button();
        button.setIcon(FontAwesome.CHECK);
        button.addStyleName("small");
        button.setDescription(Messages.getString("Caption.Button.EnableTest"));
        
        if (state != null && state.equals(true)) {
        	button.setData(true);
        	button.addStyleName("friendly");
        } else {
	        button.setData(false);
        }
        
        button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (button.getData().equals(false)) {
					source.getItem(itemId).getItemProperty(
							stateField).setValue(true);
					button.setData(true);
					button.addStyleName("friendly");
				} else {
					source.getItem(itemId).getItemProperty(
							stateField).setValue(false);
					button.setData(false);
					button.removeStyleName("friendly");
				}
			}
        });
        
        return button;
	}

}
