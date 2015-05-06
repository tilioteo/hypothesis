/**
 * 
 */
package org.vaadin.special.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.special.shared.ui.selectbutton.SelectButtonState.LabelPosition;
import org.vaadin.special.ui.SelectButton.ClickEvent;
import org.vaadin.special.ui.SelectButton.ClickListener;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SelectPanel extends MultipleComponentPanel<SelectButton> implements ClickListener {

	protected List<ClickListener> clickListeners = new ArrayList<ClickListener>();
	protected LinkedList<SelectButton> selectedButtons = new LinkedList<SelectButton>();
	private String[] captions;
	private LabelPosition labelPosition = LabelPosition.Right;
	private boolean multiSelect = false;

	public SelectPanel() {
		super();
		setSizeUndefined();
		//addStyleName("light");
	}

	public void addButtonClickListener(SelectButton.ClickListener buttonClickListener) {
		this.clickListeners.add(buttonClickListener);
		
		for (SelectButton button : childList) {
			button.addClickListener(buttonClickListener);
		}
	}

	public boolean isMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(boolean multiSelect) {
		if (this.multiSelect != multiSelect) {
			this.multiSelect = multiSelect;
			
			addChildren();
		}
	}

	public void setCaptions(String[] captions) {
		this.captions = captions;
		
		addChildren();
	}

	public Collection<SelectButton> getSelectedButtons() {
        if (selectedButtons.size() == 0) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(selectedButtons);
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
		}
	}
	
	public void removeSelected(SelectButton button) {
		if (selectedButtons.contains(button)) {
			selectedButtons.remove(button);
			if (button.getValue()) {
				button.setValue(false);
			}
		}
	}

	protected void addChildren() {
		int i = 1;
		removeAllChildren();

		for (String caption : captions) {
			if (null == caption) {
				caption = "";
			}
			SelectButton selectButton = multiSelect ? new CheckBox(caption) : new RadioButton(caption);
			selectButton.setLabelPosition(labelPosition);
			selectButton.setData(String.format("%s_%d", this.getData() != null ? (String) this.getData() : "", i++));
			
			selectButton.addClickListener(this);
			for (SelectButton.ClickListener listener : clickListeners) {
				selectButton.addClickListener(listener);
			}

			addChild(selectButton);
		}
		updateContent();
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

}
