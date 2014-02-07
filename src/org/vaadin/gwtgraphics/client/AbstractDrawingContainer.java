/**
 * 
 */
package org.vaadin.gwtgraphics.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author kamil
 * 
 */
public abstract class AbstractDrawingContainer extends AbstractDrawing
		implements HasDrawings, Orderable {

	private List<AbstractDrawing> children = new ArrayList<AbstractDrawing>();

	@Override
	public AbstractDrawing add(AbstractDrawing d) {
		adopt(d);
		children.add(d);

		return d;
	}

	@Override
	public Iterator<AbstractDrawing> iterator() {
		return children.iterator();
	}

	@Override
	public AbstractDrawing remove(AbstractDrawing d) {
		if (d.getParent() != this) {
			return null;
		}

		orphan(d);
		children.remove(d);

		return d;
	}

	@Override
	public void clear() {
		for (Iterator<AbstractDrawing> iterator = iterator(); iterator.hasNext();) {
			remove(iterator.next());
		}
	}

	@Override
	public AbstractDrawing insert(AbstractDrawing d, int beforeIndex) {
		if (beforeIndex < 0 || beforeIndex > getCount()) {
			throw new IndexOutOfBoundsException();
		}

		if (children.contains(d)) {
			children.remove(d);
		}

		adoptOnIndex(d, beforeIndex);
		children.add(beforeIndex, d);

		return d;
	}

	@Override
	public AbstractDrawing get(int index) {
		return children.get(index);
	}

	protected int getCount() {
		return children.size();
	}

	protected void adopt(AbstractDrawing d) {
		getImpl().add(getElement(), d.getElement(), d.isAttached());
		d.setParent(this);
	}

	protected void adoptOnIndex(AbstractDrawing d, int beforeIndex) {
		d.setParent(this);
		getImpl().insert(getElement(), d.getElement(), beforeIndex,
				d.isAttached());
	}

	protected void orphan(AbstractDrawing d) {
		d.setParent(null);
		getElement().removeChild(d.getElement());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Widget#doAttachChildren()
	 */
	@Override
	protected void doAttachChildren() {
		for (AbstractDrawing d : children) {
			d.onAttach();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Widget#doDetachChildren()
	 */
	@Override
	protected void doDetachChildren() {
		for (AbstractDrawing d : children) {
			d.onDetach();
		}
	}
}
