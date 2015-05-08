package com.tilioteo.hypothesis.ui.form;

import com.tilioteo.hypothesis.core.Messages;
import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.ColumnGenerator;

@SuppressWarnings({ "serial", "unchecked" })
public class DoubleCheckerColumnGenerator implements ColumnGenerator {
	
	private String stateField;
	
	public DoubleCheckerColumnGenerator(String stateField) {
		this.stateField = stateField;
	}

	@Override
	public Object generateCell(final Table source,
			final Object itemId, Object columnId) {
		CssLayout group = new CssLayout();
        group.addStyleName("v-component-group");
        
        Item item = source.getItem(itemId);
        Boolean state = (Boolean) item.getItemProperty(
        		stateField).getValue(); 

        final Button enabledButton = new Button();
        enabledButton.setIcon(FontAwesome.CHECK);
        enabledButton.addStyleName("small");
        enabledButton.setDescription(
        		Messages.getString("Caption.Button.EnableTest"));
        
        if (state != null && state.equals(true)) {
        	enabledButton.setData(true);
        	enabledButton.addStyleName("friendly");
        } else {
	        enabledButton.setData(false);
        }
        
        final Button disabledButton = new Button();
        disabledButton.setIcon(FontAwesome.TIMES);
        disabledButton.addStyleName("small");
        disabledButton.setDescription(
        		Messages.getString("Caption.Button.DisableTest"));
        disabledButton.setData(false);

        if (state != null && state.equals(false)) {
        	disabledButton.setData(true);
        	disabledButton.addStyleName("danger");
        } else {
	        disabledButton.setData(false);		        	
        }
        
        enabledButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (enabledButton.getData().equals(false)) {
					source.getItem(itemId).getItemProperty(
							stateField).setValue(true);
					enabledButton.setData(true);
					enabledButton.addStyleName("friendly");
					disabledButton.setData(false);
					disabledButton.removeStyleName("danger");
				} else {
					source.getItem(itemId).getItemProperty(
							stateField).setValue(null);
					enabledButton.setData(false);
					enabledButton.removeStyleName("friendly");
				}
			}
        });
        
        disabledButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (disabledButton.getData().equals(false)) {
					source.getItem(itemId).getItemProperty(
							stateField).setValue(false);
					disabledButton.setData(true);
					disabledButton.addStyleName("danger");
					enabledButton.setData(false);
					enabledButton.removeStyleName("friendly");
				} else {
					source.getItem(itemId).getItemProperty(
							stateField).setValue(null);
					disabledButton.setData(false);
					disabledButton.removeStyleName("danger");
				}
			}
        });

        group.addComponent(enabledButton);
        group.addComponent(disabledButton);

        return group;
	}

}
