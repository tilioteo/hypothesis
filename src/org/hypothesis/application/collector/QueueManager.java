/**
 * 
 */
package org.hypothesis.application.collector;

import java.util.LinkedList;

import org.hypothesis.common.HasQueue;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class QueueManager<T extends HasQueue<E>, E> {

	private LinkedList<E> queue = new LinkedList<E>();

	private E element = null;

	public E current() {
		return element;
	}

	public E find(E item) {
		if (item == element)
			return current();

		int index = queue.indexOf(item);
		if (index >= 0) {
			for (int i = 0; i < index; ++i)
				queue.remove();
		} else {
			queue.clear();
			element = null;
		}
		return next();
	}

	public E next() {
		element = queue.poll();
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

	public void setQueueOwner(T queueOwner) {
		this.queue.clear();
		if (queueOwner != null) {
			this.queue.addAll(queueOwner.getQueue());
		}
		next();
	}
}
