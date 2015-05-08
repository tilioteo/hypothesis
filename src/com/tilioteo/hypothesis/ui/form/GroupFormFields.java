package com.tilioteo.hypothesis.ui.form;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.entity.FieldConstants;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.User;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class GroupFormFields {

	private TextField idField;
    private TextField nameField;
    private AbstractSelect usersField;
    private TextField noteField;
    private AbstractSelect packsField;
    
   
    public AbstractField<String> buildIdField() {
    	if (idField == null) {
    		idField = new TextField(Messages.getString("Caption.Field.Id"));
    		idField.setEnabled(false);
    	}
		return idField;
    }

    public AbstractField<String> buildNameField() {
		if (nameField == null) {
			nameField = new TextField(Messages.getString("Caption.Field.Name"));
			nameField.setNullRepresentation("");
			nameField.setMaxLength(30);
			nameField.setRequired(true);
			nameField.setRequiredError(Messages.getString("Message.Error.NameRequired"));
			nameField.addValidator(new StringLengthValidator(Messages.getString("Message.Error.NameLength",
					4, 30), 4, 30, false));
		}
		return nameField;
	}
	
	public AbstractField<String> buildNoteField() {
		if (noteField == null) {
			noteField = new TextField(Messages.getString("Caption.Field.Note"));
			noteField.setNullRepresentation("");
		}
		return noteField;
	}

	public AbstractSelect buildUsersField(boolean required) {
		if (usersField == null) {
			final Table table = new Table(Messages.getString("Caption.Field.Users"));
			table.setSelectable(false);
			table.addStyleName("small");
			table.addStyleName("no-header");
            table.addStyleName("borderless");
            table.addStyleName("no-stripes");
			table.addStyleName("no-horizontal-lines");
			table.addStyleName("no-vertical-lines");
            table.addStyleName("compact");
			
			table.addContainerProperty(FieldConstants.USERNAME, String.class, null);
			table.addContainerProperty(FieldConstants.SELECTED, Boolean.class, null);
			
			table.addGeneratedColumn(FieldConstants.ENABLER,
					new SimpleCheckerColumnGenerator(
							FieldConstants.SELECTED));			

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source,
						Object itemId, Object propertyId) {
					User user = (User) itemId;
					return Messages.getString("Caption.Item.UserDescription",
						user.getUsername(), user.getId());
				}
			});
			
			table.setVisibleColumns(FieldConstants.ENABLER,
					FieldConstants.USERNAME);
			table.setColumnHeaders(Messages.getString("Caption.Field.State"),
					Messages.getString("Caption.Field.Username"));

			table.setPageLength(table.size());

			usersField = table;
			
			if (required) {
				usersField.setRequired(true);
				usersField.setRequiredError(
						Messages.getString("Message.Error.UserRequired"));
			}
		}
		return usersField;		
	}

	public AbstractSelect buildPacksField() {
		if (packsField == null) {
			final Table table = new Table(
					Messages.getString("Caption.Field.EnabledPacks"));
			table.setSelectable(false);
			table.addStyleName("small");
			table.addStyleName("no-header");
            table.addStyleName("borderless");
            table.addStyleName("no-stripes");
			table.addStyleName("no-horizontal-lines");
			table.addStyleName("no-vertical-lines");
            table.addStyleName("compact");
            table.setPageLength(8);
			
			table.addContainerProperty(FieldConstants.NAME, String.class, null);
			table.addContainerProperty(FieldConstants.SELECTED, Boolean.class, null);
			
			table.addGeneratedColumn(FieldConstants.ENABLER,
					new SimpleCheckerColumnGenerator(
							FieldConstants.SELECTED));			

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source,
						Object itemId, Object propertyId) {
					Pack pack = (Pack) itemId;
					return Messages.getString("Caption.Item.PackDescription",
							pack.getName(), pack.getId(), pack.getDescription());
				}
			});
			
			table.setVisibleColumns(FieldConstants.ENABLER,
					FieldConstants.NAME);
			table.setColumnHeaders(Messages.getString("Caption.Field.State"),
					Messages.getString("Caption.Field.Name"));

			table.setPageLength(table.size());

			packsField = table;
		}
		return packsField;
	}

	public AbstractField<String> getIdField() {
		return idField;
	}

	public AbstractField<String> getNameField() {
		return nameField;
	}
	
	public AbstractField<String> getNoteField() {
		return noteField;
	}

	public AbstractSelect getUsersField() {
		return usersField;
	}
	
	public AbstractSelect getPacksField() {
		return packsField;
	}

}
