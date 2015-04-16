/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.Field;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.core.SlideManager;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.SelectPanelData;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
import com.tilioteo.hypothesis.shared.ui.selectbutton.SelectButtonState.LabelPosition;
import com.tilioteo.hypothesis.ui.SelectButton.ClickEvent;
import com.tilioteo.hypothesis.ui.SelectButton.ClickListener;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.Alignment;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class SelectPanel extends MultipleComponentPanel<SelectButton> implements ClickListener,
		SlideComponent, Field, Validatable {

	private SlideManager slideManager;
	private ParentAlignment parentAlignment;

	private List<ClickListener> clickListeners = new ArrayList<ClickListener>();
	private LinkedList<SelectButton> selectedButtons = new LinkedList<SelectButton>();
	private String[] captions;
	private LabelPosition labelPosition = LabelPosition.Right;
	private boolean multiSelect = false;

    /**
     * Is automatic validation enabled.
     */
    private boolean validationVisible = true;

    /**
     * The list of validators.
     */
    private LinkedList<Validator> validators = null;

	public SelectPanel() {
		this.parentAlignment = new ParentAlignment();
	}

	public SelectPanel(SlideManager slideManager) {
		this();
		this.slideManager = slideManager;

		setStyleName("light");
		setSizeUndefined();
	}

	protected void addChilds() {
		int i = 1;
		for (String caption : captions) {
			if (null == caption) {
				caption = "";
			}
			SelectButton selectButton = multiSelect ? new CheckBox(caption) : new RadioButton(caption);
			selectButton.setLabelPosition(labelPosition);
			selectButton
					.setData(String.format("%s_%d",
							this.getData() != null ? (String) this.getData()
									: "", i++));
			
			selectButton.addClickListener(this);
			
			for (SelectButton.ClickListener listener : clickListeners)
				selectButton.addClickListener(listener);

			addChild(selectButton);
		}
		updateContent();
	}
	
	public void addButtonClickListener(SelectButton.ClickListener buttonClickListener) {
		this.clickListeners.add(buttonClickListener);
	}

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	private LabelPosition getLabelPosition(StringMap stringMap,
			LabelPosition defaultLabelPosition) {
		String labelPosition = stringMap.get(SlideXmlConstants.LABEL_POSITION);
		if (labelPosition != null) {
			if ("right".equals(labelPosition.toLowerCase()))
				return LabelPosition.Right;
			else if ("left".equals(labelPosition.toLowerCase()))
				return LabelPosition.Left;
			else if ("bottom".equals(labelPosition.toLowerCase()))
				return LabelPosition.Bottom;
			else if ("top".equals(labelPosition.toLowerCase()))
				return LabelPosition.Top;
		}
		return defaultLabelPosition;
	}

	public Collection<SelectButton> getSelectedButtons() {
        if (selectedButtons.size() == 0) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(selectedButtons);
        }
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		setHandlers(element);
		setValidators(element);

		addChilds();
	}

	private void setClickHandler(final String actionId) {
		addButtonClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				SelectPanelData data = new SelectPanelData(SelectPanel.this, slideManager);
				data.setButton((SelectButton) event.getSource());
				Command componentEvent = CommandFactory.createSelectPanelClickEventCommand(data);
				Command action = CommandFactory.createActionCommand(slideManager, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	protected void setHandler(Element element) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstance(slideManager).createAnonymousAction(element);
		if (anonymousAction != null)
			action = anonymousAction.getId();

		if (!Strings.isNullOrEmpty(action)) {
			if (name.equals(SlideXmlConstants.CLICK)) {
				setClickHandler(action);
			}
			// TODO add other event handlers
		}
	}

	private void setHandlers(Element element) {
		List<Element> handlers = SlideXmlUtility.getComponentHandlers(element);

		for (Element handler : handlers) {
			setHandler(handler);
		}
		
		setDefaultButtonClickListener();
	}

	private void setDefaultButtonClickListener() {
		if (clickListeners.isEmpty()) {
			addButtonClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					SelectPanelData data = new SelectPanelData(SelectPanel.this, slideManager);
					data.setButton((SelectButton) event.getSource());

					Command componentEvent = CommandFactory.createSelectPanelClickEventCommand(data);
					Command.Executor.execute(componentEvent);
				}
			});
		}
	}

	private void setValidators(Element element) {
		List<com.tilioteo.hypothesis.data.Validator> validators = ComponentFactory.createSelectPanelValidators(element);
		for (Validator validator : validators) {
			addValidator(validator);
		}
		
		if (!validators.isEmpty()) {
			setImmediate(true);
		}
	}

	public void setLabelPosition(LabelPosition labelPosition) {
		if (this.labelPosition != labelPosition) {
			this.labelPosition = labelPosition;
			updateLabelPositions();
		}
	}
	
	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	private void updateLabelPositions() {
		Iterator<SelectButton> iterator = getChildIterator();
		while (iterator.hasNext()) {
			iterator.next().setLabelPosition(labelPosition);
		}
	}

	private void setLabelPosition(StringMap properties) {
		setLabelPosition(getLabelPosition(properties, LabelPosition.Right));
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);

		this.captions = properties.getStringArray(SlideXmlConstants.CAPTIONS);

		ComponentUtility.setComponentPanelProperties(this, element, properties,
				parentAlignment);

		// set SelectPanel specific properties
		this.multiSelect = properties.getBoolean(SlideXmlConstants.MULTI_SELECT, false);
		setLabelPosition(properties);
		
	}
	
	public void addSelected(SelectButton button) {
		if (childList.contains(button) && !selectedButtons.contains(button)) {
			if (!multiSelect) {
				Iterator<SelectButton> iterator = selectedButtons.iterator();
				while (iterator.hasNext()) {
					SelectButton selectButton = iterator.next();
					selectButton.setValue(false);
					selectedButtons.remove(selectButton);
				}
			}
			
			selectedButtons.add(button);
			if (!button.getValue()) {
				button.setValue(true);
			}

			if (validators != null && !validators.isEmpty()) {
	            markAsDirty();
	        }
		}
	}
	
	public void removeSelected(SelectButton button) {
		if (selectedButtons.contains(button)) {
			selectedButtons.remove(button);
			if (button.getValue()) {
				button.setValue(false);
			}

			if (validators != null && !validators.isEmpty()) {
	            markAsDirty();
	        }
		}
	}

	/*public void setSelected(SelectButton radioButton) {
		if (null == radioButton || childList.contains(radioButton)) {
			if (this.selected != radioButton) {
				if (this.selected != null) {
					this.selected.setValue(false);
				}
				this.selected = radioButton;
			}
		}
        if (validators != null && !validators.isEmpty()) {
            markAsDirty();
        }
	}*/

	@Override
	public void setSlideManager(SlideManager slideManager) {
		this.slideManager = slideManager;
	}

	@Override
	public void readDataFromElement(Element element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeDataToElement(Element element) {
		element.addAttribute(SlideXmlConstants.TYPE, SlideXmlConstants.SELECT_PANEL);
		element.addAttribute(SlideXmlConstants.ID, (String) getData());
		Element captionElement = element.addElement(SlideXmlConstants.CAPTION);
		if (getCaption() != null) {
			captionElement.addText(getCaption());
		}

		if (selectedButtons.size() > 0) {
			for (SelectButton selected : selectedButtons) {
				Element valueElement = element.addElement(SlideXmlConstants.VALUE);
				valueElement.addAttribute(SlideXmlConstants.ID, String.format("%d", getChildIndex(selected) + 1));
				valueElement.addText(selected.getCaption());
			}
		} else {
			element.addElement(SlideXmlConstants.VALUE);
		}
	}

	@Override
	public boolean isValid() {
        try {
            validate();
            return true;
        } catch (InvalidValueException e) {
            return false;
        }
	}

	@Override
	public void buttonClick(ClickEvent event) {
		SelectButton button = event.getSelectButton();
		if (button.getValue()) {
			addSelected(button);
		} else {
			removeSelected(button);
		}
	}

	@Override
	public void addValidator(Validator validator) {
        if (validators == null) {
            validators = new LinkedList<Validator>();
        }
        validators.add(validator);
        markAsDirty();
	}

	@Override
	public void removeValidator(Validator validator) {
        if (validators != null) {
            validators.remove(validator);
        }
        markAsDirty();
	}

	@Override
	public void removeAllValidators() {
        if (validators != null) {
            validators.clear();
        }
        markAsDirty();
	}

	@Override
	public Collection<Validator> getValidators() {
        if (validators == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(validators);
        }
	}

	@Override
	public void validate() throws InvalidValueException {
		validate(getSelectedButtons());
	}

    protected void validate(Collection<SelectButton> values) throws InvalidValueException {

        List<InvalidValueException> validationExceptions = new ArrayList<InvalidValueException>();
        if (validators != null) {
            // Gets all the validation errors
            for (Validator v : validators) {
                try {
                    v.validate(values);
                } catch (final InvalidValueException e) {
                    validationExceptions.add(e);
                }
            }
        }

        // If there were no errors
        if (validationExceptions.isEmpty()) {
            return;
        }

        // If only one error occurred, throw it forwards
        if (validationExceptions.size() == 1) {
            throw validationExceptions.get(0);
        }

        InvalidValueException[] exceptionArray = validationExceptions
                .toArray(new InvalidValueException[validationExceptions.size()]);

        // Create a composite validator and include all exceptions
        throw new InvalidValueException(null, exceptionArray);
    }

    @Override
    public ErrorMessage getErrorMessage() {

        /*
         * Check validation errors only if automatic validation is enabled.
         * Empty, required fields will generate a validation error containing
         * the requiredError string. For these fields the exclamation mark will
         * be hidden but the error must still be sent to the client.
         */
        InvalidValueException validationError = null;
        if (isValidationVisible()) {
            try {
                validate();
            } catch (InvalidValueException e) {
                if (!e.isInvisible()) {
                    validationError = e;
                }
            }
        }

        // Check if there are any systems errors
        final ErrorMessage superError = super.getErrorMessage();

        // Return if there are no errors at all
        if (superError == null && validationError == null) {
            return null;
        }

        // Throw combination of the error types
        return new CompositeErrorMessage(
                new ErrorMessage[] {
                        superError,
                        AbstractErrorMessage
                                .getErrorMessageForException(validationError) });
    }

    public boolean isValidationVisible() {
        return validationVisible;
    }

    public void setValidationVisible(boolean validateAutomatically) {
        if (validationVisible != validateAutomatically) {
            markAsDirty();
            validationVisible = validateAutomatically;
        }
    }

	@Override
	public boolean isInvalidAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setInvalidAllowed(boolean invalidValueAllowed)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

}
