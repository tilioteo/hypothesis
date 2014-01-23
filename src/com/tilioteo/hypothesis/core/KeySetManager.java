/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.HashMap;

import com.tilioteo.hypothesis.entity.HasId;
import com.tilioteo.hypothesis.entity.HasList;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class KeySetManager<T extends HasList<E>, E extends HasId<K>, K> {

	private HashMap<K, E> keyset = new HashMap<K, E>();

	private E element = null;

	public E current() {
		return element;
	}

	public E find(E item) {
		if (keyset.containsValue(item)) {
			element = item;
		} else {
			element = null;
		}
		return element;
	}

	public E get(K key) {
		element = keyset.get(key);
		return element;
	}

	/**
	 * set current element - for test purpose only
	 * 
	 * @param element
	 */
	protected void setCurrent(E element) {
		this.element = element;
	}

	public void setListParent(T parent) {
		this.keyset.clear();
		if (parent != null) {
			for (E item : parent.getList()) {
				this.keyset.put(item.getId(), item);
			}
			if (parent.getList().size() > 0)
				element = parent.getList().get(0);
			else
				element = null;
		}
	}
}
