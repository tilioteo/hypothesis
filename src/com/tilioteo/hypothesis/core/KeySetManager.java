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

	private K key = null;
	private E element = null;

	private E getByInternalKey() {
		if (key != null) {
			element = keyset.get(key);
		} else {
			element = null;
		}
		return element;
	}

	public E current() {
		return getByInternalKey();
	}
	
	public E find(E item) {
		key = null;
		element = null;
		
		for (K k : keyset.keySet()) {
			E e = keyset.get(k);
			if (e == item) {
				key = k;
				element = e;
				break;
			}
		}
		return element;
	}

	public E get(K key) {
		this.key = key;
		return getByInternalKey();
	}
	
	/*
	 * set current element - for test purpose only
	 * 
	 * @param element
	 */
	/*protected void setCurrent(E element) {
		this.element = element;
	}*/

	public void setListFromParent(T parent) {
		keyset.clear();
		key = null;
		if (parent != null) {
			for (E item : parent.getList()) {
				if (item != null) {
					keyset.put(item.getId(), item);
				}
			}
			if (keyset.size() > 0) {
				List<E> list = new ArrayList<E>(keyset.values());
				find(list.get(0));
			} else {
				element = null;
			}
		}
	}
}
