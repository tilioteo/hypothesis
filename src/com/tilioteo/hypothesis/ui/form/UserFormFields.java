package com.tilioteo.hypothesis.ui.form;

import java.util.Date;

import com.tilioteo.hypothesis.core.Messages;
import com.tilioteo.hypothesis.entity.FieldConstants;
import com.tilioteo.hypothesis.entity.Group;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Role;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;

@SuppressWarnings("serial")
public class UserFormFields {
	
	private TextField idField;
	private TextField usernameField;
	private TextField generatedGroupField;
	private TextField generatedCountField;
    private TextField passwordField;
    private AbstractSelect rolesField;
    private CheckBox enabledField;
    private PopupDateField expireDateField;
    private TextField noteField;
    private AbstractSelect groupsField;
    private AbstractSelect packsField;
    
   
    public AbstractField<String> buildIdField() {
    	if (idField == null) {
    		idField = new TextField(Messages.getString("Caption.Field.Id"));
    		idField.setEnabled(false);
    	}
		return idField;
    }

    public AbstractField<String> buildGeneratedGroupField() {
    	if (generatedGroupField == null) {
	    	generatedGroupField = new TextField();
			generatedGroupField.setMaxLength(30);
			generatedGroupField.setNullRepresentation("");
			generatedGroupField.setRequired(true);
			generatedGroupField.setRequiredError(
					Messages.getString("Message.Error.GeneratedGroupRequired"));
			generatedGroupField.addValidator(new StringLengthValidator(
					Messages.getString("Message.Error.GeneratedGroupLength", 4, 30),
					4, 30, false));
    	}
		return generatedGroupField;
    }
    
    public AbstractField<String> buildGeneratedCountField() {
    	if (generatedCountField == null) {
	    	generatedCountField = new TextField();
	    	generatedCountField.setConverter(new StringToIntegerConverter());
	    	generatedCountField.setConversionError(Messages.getString("Message.Error.GeneratedCountInteger", 1, 999));
			generatedCountField.setMaxLength(3);
			generatedCountField.setNullRepresentation("");
			generatedCountField.setWidth(3, Unit.EM);
			generatedCountField.setRequired(true);
			generatedCountField.setRequiredError(Messages.getString("Message.Error.GeneratedCountRequired"));
			generatedCountField.addValidator(new IntegerRangeValidator(
					Messages.getString("Message.Error.GeneratedCountInteger", 1, 999), 1, 999));
    	}
		return generatedCountField;
    }
    
    public AbstractField<String> buildUsernameField() {
		if (usernameField == null) {
			usernameField = new TextField(Messages.getString("Caption.Field.Username"));
			usernameField.setNullRepresentation("");
			usernameField.setMaxLength(30);
			usernameField.setRequired(true);
			usernameField.setRequiredError(Messages.getString("Message.Error.UsernameRequired"));
			usernameField.addValidator(new StringLengthValidator(
					Messages.getString("Message.Error.UsernameLength", 4, 30),
					4, 30, false));
		}
		return usernameField;
	}
	
	public AbstractField<String> buildPasswordField() {
		if (passwordField == null) {
			passwordField = new TextField(Messages.getString("Caption.Field.Password"));
			passwordField.setNullRepresentation("");
			passwordField.setMaxLength(30);
			passwordField.setRequired(true);
			usernameField.setRequiredError(Messages.getString("Message.Error.PasswordRequired"));
			passwordField.addValidator(new StringLengthValidator(
					Messages.getString("Message.Error.PasswordLength", 4, 30),
					4, 30, false));
		}
		return passwordField;
	}
	
	public AbstractSelect buildRolesField() {
		if (rolesField == null) {
		    rolesField = new OptionGroup(Messages.getString("Caption.Field.Role"));
		    rolesField.addStyleName("horizontal");
			rolesField.setItemCaptionPropertyId(FieldConstants.NAME);
			rolesField.setMultiSelect(true);
			
	        BeanItemContainer<Role> dataSource =
	        		new BeanItemContainer<Role>(Role.class);
			rolesField.setContainerDataSource(dataSource);
			
			//rolesField.setRequired(true);
			//usernameField.setRequiredError(Messages.getString("Message.Error.RoleRequired"));
		}
		return rolesField;
	}
	
	public AbstractField<Boolean> buildEnabledField() {
		if (enabledField == null) {
			enabledField = new CheckBox(Messages.getString("Caption.Field.Enabled"));
		}
        return enabledField;
	}
	
	public AbstractField<Date> buildExpireDateField() {
		if (expireDateField == null) {
		    expireDateField = new PopupDateField(Messages.getString("Caption.Field.ExpireDate"));
		    expireDateField.setResolution(Resolution.DAY);
		    expireDateField.setDateFormat(Messages.getString("Format.Date"));
		}
	    return expireDateField;
	}
	
	public AbstractField<String> buildNoteField() {
		if (noteField == null) {
			noteField = new TextField(Messages.getString("Caption.Field.Note"));
			noteField.setNullRepresentation("");
		}
		return noteField;
	}

	public AbstractSelect buildGroupsField(boolean required) {
		if (groupsField == null) {
			final Table table = new CheckTable(
					Messages.getString("Caption.Field.Groups"));
			table.setSelectable(false);
			table.addStyleName("small");
			table.addStyleName("no-header");
            table.addStyleName("borderless");
            table.addStyleName("no-stripes");
			table.addStyleName("no-horizontal-lines");
			table.addStyleName("no-vertical-lines");
            table.addStyleName("compact");
			
			table.addContainerProperty(FieldConstants.NAME, String.class, null);
			table.addContainerProperty(FieldConstants.SELECTED, Boolean.class, null);
			
			table.addGeneratedColumn(FieldConstants.ENABLER,
					new SimpleCheckerColumnGenerator(
							FieldConstants.SELECTED));			

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source,
						Object itemId, Object propertyId) {
					Group group = (Group) itemId;
					return Messages.getString("Caption.Item.GroupDescription",
							group.getName(), group.getId());
				}
			});
			
			table.setVisibleColumns(FieldConstants.ENABLER,
					FieldConstants.NAME);
			table.setColumnHeaders(Messages.getString("Caption.Field.State"),
					Messages.getString("Caption.Field.Name"));

			table.setPageLength(table.size());

			groupsField = table;
			
			// TODO: vymyslet validator na kontrolu vyberu
			// (nevybiram primo v tabulce), vyhnout se CheckTable
			// (je prilis zjednodsena a nedoladena)
			if (required) {
				groupsField.setRequired(true);
				groupsField.setRequiredError(
						Messages.getString("Message.Error.GroupRequired"));
			}
		}
		return groupsField;
	}

	public AbstractSelect buildPacksField() {
		if (packsField == null) {
			final Table table = new Table(Messages.getString("Caption.Field.Packs"));
			table.setSelectable(false);
			table.addStyleName("small");
			table.addStyleName("no-header");
            table.addStyleName("borderless");
            table.addStyleName("no-stripes");
			table.addStyleName("no-horizontal-lines");
			table.addStyleName("no-vertical-lines");
            table.addStyleName("compact");
			
			table.addContainerProperty(FieldConstants.NAME, String.class, null);
			table.addContainerProperty(FieldConstants.TEST_STATE, Boolean.class, null);
			
			table.addGeneratedColumn(FieldConstants.TEST_ENABLER,
					new DoubleCheckerColumnGenerator(
							FieldConstants.TEST_STATE));			

			table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
				@Override
				public String generateDescription(Component source,
						Object itemId, Object propertyId) {
					Pack pack = (Pack) itemId;
					return Messages.getString("Caption.Item.PackDescription",
							pack.getName(), pack.getId(), pack.getDescription());
				}
			});
			
			table.setVisibleColumns(FieldConstants.TEST_ENABLER,
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

	public AbstractField<String> getUsernameField() {
		return usernameField;
	}
	
	public AbstractField<String> getGeneratedGroupField() {
		return generatedGroupField;
	}
	
	public AbstractField<String> getGeneratedCountField() {
		return generatedCountField;
	}
	
	public AbstractField<String> getPasswordField() {
		return passwordField;
	}

	public AbstractField<Boolean> getEnabledField() {
		return enabledField;
	}

	public AbstractField<Date> getExpireDateField() {
		return expireDateField;
	}

	public TextField getNoteField() {
		return noteField;
	}

	public AbstractSelect getRolesField() {
		return rolesField;
	}

	public AbstractSelect getGroupsField() {
		return groupsField;
	}
	
	public AbstractSelect getPacksField() {
		return packsField;
	}

}
