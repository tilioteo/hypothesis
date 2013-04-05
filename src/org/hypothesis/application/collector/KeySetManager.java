/**
 * 
 */
package org.hypothesis.application.collector;

import java.util.HashMap;

import org.hypothesis.common.HasId;
import org.hypothesis.common.HasQueue;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class KeySetManager<T extends HasQueue<E>, E extends HasId<K>, K> {

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

	public void setQueueOwner(T queueOwner) {
		this.keyset.clear();
		if (queueOwner != null) {
			for (E item : queueOwner.getQueue()) {
				this.keyset.put(item.getId(), item);
			}
			if (queueOwner.getQueue().size() > 0)
				element = queueOwner.getQueue().get(0);
			else
				element = null;
		}
	}
}
