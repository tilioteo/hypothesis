/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class MultipleComponentPanel<C extends AbstractComponent> extends Panel {

	public enum Orientation {
		Vertical, Horizontal
	};

	private List<C> childList = new LinkedList<C>();
	private Orientation orientation = Orientation.Horizontal;
	private String childWidth = null;
	private String childHeight = null;

	protected MultipleComponentPanel() {
		// nop
	}
	
	protected void addChild(C child) {
		childList.add(child);
	}
	
	public int getChildIndex(C child) {
		int index = 0;
		for (C child2 : childList) {
			if (child2.equals(child))
				return index;

			++index;
		}
		return -1;
	}

	protected Iterator<C> getChildIterator() {
		return childList.iterator();
	}

	public String getChildsHeight() {
		return childHeight;
	}

	public String getChildsWidth() {
		return childWidth;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	private void setChildHeight(C child) {
		if (childHeight != null)
			child.setHeight(childHeight);
		else
			child.setHeight(-1, Unit.PIXELS);
	}

	public void setChildsHeight(String height) {
		this.childHeight = height;
		updateContent();
	}

	public void setChildsWidth(String width) {
		this.childWidth = width;
		updateContent();
	}

	private void setChildWidth(C child) {
		if (childWidth != null)
			child.setWidth(childWidth);
		else
			child.setWidth(-1, Unit.PIXELS);
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		updateContent();
	}

	protected void updateContent() {
		Layout layout = (Layout) getContent();
		AbstractOrderedLayout newLayout = null;

		if (layout instanceof VerticalLayout
				&& orientation.equals(Orientation.Horizontal))
			newLayout = new HorizontalLayout();
		else if (layout instanceof HorizontalLayout
				&& orientation.equals(Orientation.Vertical))
			newLayout = new VerticalLayout();

		if (newLayout != null) {
			layout.removeAllComponents();
			setContent(newLayout);
			newLayout.setSizeFull();
		} else {
			layout.setSizeFull();
		}

		for (C child : childList) {
			setChildWidth(child);
			setChildHeight(child);

			if (newLayout != null) {
				newLayout.addComponent(child);
				newLayout.setComponentAlignment(child, Alignment.MIDDLE_CENTER);
			}
		}
	}

}
