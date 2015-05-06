/**
 * 
 */
package com.tilioteo.hypothesis.slide.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;
import org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition;
import org.vaadin.special.ui.SelectButton;
import org.vaadin.special.ui.SelectButton.ClickEvent;
import org.vaadin.special.ui.SelectButton.ClickListener;

import com.tilioteo.hypothesis.common.StringMap;
import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.SlideFactory;
import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.SlideXmlUtility;
import com.tilioteo.hypothesis.event.SelectPanelData;
import com.tilioteo.hypothesis.interfaces.Field;
import com.tilioteo.hypothesis.interfaces.SlideComponent;
import com.tilioteo.hypothesis.interfaces.SlideFascia;
import com.tilioteo.hypothesis.processing.AbstractBaseAction;
import com.tilioteo.hypothesis.processing.Command;
import com.tilioteo.hypothesis.processing.CommandFactory;
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
public class SelectPanel extends org.vaadin.special.ui.SelectPanel implements SlideComponent, Field, Validatable {

	private SlideFascia slideFascia;
	private ParentAlignment parentAlignment;

    /**
     * Is automatic validation enabled.
     */
    private boolean validationVisible = true;

    /**
     * The list of validators.
     */
    private LinkedList<Validator> validators = null;

	public SelectPanel() {
		super();
		this.parentAlignment = new ParentAlignment();
	}

	@Override
	public Alignment getAlignment() {
		return parentAlignment.getAlignment();
	}

	@Override
	public void loadFromXml(Element element) {

		setProperties(element);
		setHandlers(element);
		setValidators(element);
	}

	private void setClickHandler(final String actionId) {
		addButtonClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				SelectPanelData data = new SelectPanelData(SelectPanel.this, slideFascia);
				data.setButton((SelectButton) event.getSource());
				Command componentEvent = CommandFactory.createSelectPanelClickEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
				Command action = CommandFactory.createActionCommand(slideFascia, actionId, data);

				Command.Executor.execute(componentEvent);
				Command.Executor.execute(action);
			}
		});
	}

	protected void setHandler(Element element) {
		String name = element.getName();
		String action = null;
		AbstractBaseAction anonymousAction = SlideFactory.getInstance(slideFascia).createAnonymousAction(element);
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

	private void setValidators(Element element) {
		List<com.tilioteo.hypothesis.data.Validator> validators = ComponentFactory.createSelectPanelValidators(element);
		for (Validator validator : validators) {
			addValidator(validator);
		}
		
		if (!validators.isEmpty()) {
			setImmediate(true);
		}
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

	private void setLabelPosition(StringMap properties) {
		setLabelPosition(getLabelPosition(properties, LabelPosition.Right));
	}

	protected void setProperties(Element element) {
		StringMap properties = SlideXmlUtility.getPropertyValueMap(element);

		setCaptions(properties.getStringArray(SlideXmlConstants.CAPTIONS));

		ComponentUtility.setComponentPanelProperties(this, element, properties, parentAlignment);

		// set SelectPanel specific properties
		setMultiSelect(properties.getBoolean(SlideXmlConstants.MULTI_SELECT, false));
		setLabelPosition(properties);
		
	}
	
	@Override
	public void addSelected(SelectButton button) {
		super.addSelected(button);
		if (childList.contains(button) && validators != null && !validators.isEmpty()) {
            markAsDirty();
		}
	}
	
	@Override
	public void removeSelected(SelectButton button) {
		boolean contained = selectedButtons.contains(button);
		super.removeSelected(button);
		if (contained && validators != null && !validators.isEmpty()) {
            markAsDirty();
		}
	}

	@Override
	public void setSlideManager(SlideFascia slideFascia) {
		this.slideFascia = slideFascia;
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

	private void setDefaultButtonClickListener() {
		if (clickListeners.isEmpty()) {
			addButtonClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					SelectPanelData data = new SelectPanelData(SelectPanel.this, slideFascia);
					data.setButton((SelectButton) event.getSource());

					Command componentEvent = CommandFactory.createSelectPanelClickEventCommand(data, event.getServerDatetime(), event.getClientDatetime());
					Command.Executor.execute(componentEvent);
				}
			});
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
	public void setInvalidAllowed(boolean invalidValueAllowed) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

}
