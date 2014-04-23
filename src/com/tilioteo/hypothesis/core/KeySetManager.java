/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.tilioteo.hypothesis.entity.HasId;
import com.tilioteo.hypothesis.entity.HasList;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class KeySetManager<T extends HasList<E>, E extends HasId<K>, K> {

	private LinkedHashMap<K, E> keyset = new LinkedHashMap<K, E>();

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
		keyset.clear();
		if (parent != null) {
			for (E item : parent.getList()) {
				if (item != null) {
					keyset.put(item.getId(), item);
				}
			}
			if (keyset.size() > 0) {
				List<E> list = new ArrayList<E>(keyset.values());
				element = list.get(0);
			} else {
				element = null;
			}
		}
	}
}
