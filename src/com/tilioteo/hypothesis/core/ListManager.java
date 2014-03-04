/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.LinkedList;

import com.tilioteo.hypothesis.entity.HasList;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ListManager<T extends HasList<E>, E> {

	private LinkedList<E> list = new LinkedList<E>();

	private E element = null;

	public E current() {
		return element;
	}

	public E find(E item) {
		if (item == element)
			return current();

		int index = list.indexOf(item);
		if (index >= 0) {
			for (int i = 0; i < index; ++i)
				list.remove();
		} else {
			list.clear();
			element = null;
		}
		return next();
	}

	public E next() {
		element = list.poll();
		return element;
	}

	/**
	 * set current element - for test purpose only
	 * 
	 * @param element
	 */
	public void setCurrent(E element) {
		this.element = element;
	}

	public void setListParent(T parent) {
		this.list.clear();
		if (parent != null) {
			for (E item : parent.getList()) {
				if (item != null) {
					this.list.add(item);
				}
			}
		}
		next();
	}
}
