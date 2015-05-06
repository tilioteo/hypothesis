/**
 * 
 */
package org.vaadin.special.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.themes.ValoTheme;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ButtonPanel extends MultipleComponentPanel<Button> {

	private List<Button.ClickListener> clickListeners = new ArrayList<Button.ClickListener>();
	Button selected = null;
	private String[] captions;
	
	public ButtonPanel() {
		super();
		setSizeUndefined();
		addStyleName(ValoTheme.PANEL_BORDERLESS);
	}

	public void addButtonClickListener(Button.ClickListener buttonClickListener) {
		this.clickListeners.add(buttonClickListener);
		
		for (Button button : childList) {
			button.addClickListener(buttonClickListener);
		}
	}

	public void setCaptions(String[] captions) {
		this.captions = captions;
		
		addChildren();
	}

	protected void addChildren() {
		int i = 1;
		removeAllChildren();
		
		for (String caption : captions) {
			if (null == caption) {
				caption = "";
			}
			Button button = new Button();
			button.setCaption(caption);
			button.setData(String.format("%s_%d", this.getData() != null ? (String) this.getData() : "", i++));

			for (Button.ClickListener listener : clickListeners) {
				button.addClickListener(listener);
			}

			addChild(button);
		}
		updateContent();
	}

	public Button getSelected() {
		return selected;
	}

	public void setSelected(Button button) {
		this.selected = button;
	}

}
