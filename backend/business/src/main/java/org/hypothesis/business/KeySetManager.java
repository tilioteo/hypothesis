/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import org.hypothesis.data.interfaces.HasId;
import org.hypothesis.data.interfaces.HasList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 * @param <T>
 *            type (of parental object) extending {@link HasList} of item type
 * @param <E>
 *            item type
 * @param <K>
 *            key type
 */
@SuppressWarnings("serial")
public class KeySetManager<T extends HasList<E>, E extends HasId<K>, K> implements Serializable {

	private LinkedHashMap<K, E> keyset = new LinkedHashMap<>();

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

	/**
	 * Returns current item by internal state
	 * 
	 * @return element or null
	 */
	public E current() {
		return getByInternalKey();
	}

	/**
	 * Look for an item and set internal state
	 * 
	 * @param item
	 *            to look for
	 * @return the same item or null if not found
	 */
	public E find(E item) {
		Entry<K, E> entry = keyset.entrySet().stream().filter(f -> f.getValue() == item).findFirst().orElse(null);
		if (entry != null) {
			key = entry.getKey();
			element = entry.getValue();
		} else {
		key = null;
		element = null;
		}

		return element;
	}

	/**
	 * Get an item by key value and set internal state
	 * 
	 * @param key
	 *            key value
	 * @return found item or null
	 */
	public E get(K key) {
		this.key = key;
		return getByInternalKey();
	}

	/*
	 * set current element - for test purpose only
	 * 
	 * @param element
	 */
	/*
	 * protected void setCurrent(E element) { this.element = element; }
	 */

	public void setListFromParent(T parent) {
		keyset.clear();
		key = null;
		if (parent != null) {
			parent.getList().stream().filter(Objects::nonNull).forEach(e -> keyset.put(e.getId(), e));

			if (keyset.size() > 0) {
				List<E> list = new ArrayList<>(keyset.values());
				find(list.get(0));
			} else {
				element = null;
			}
		}
	}
}
