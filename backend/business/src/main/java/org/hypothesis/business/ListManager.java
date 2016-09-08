/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.hypothesis.data.interfaces.HasList;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ListManager<T extends HasList<E>, E> implements Serializable {

	private LinkedList<E> list = new LinkedList<>();
	private int index = -1;

	private T parent = null;
	private E element = null;

	private Random randomGenerator;

	private E getByInternalIndex() {
		if (index >= 0 && index < list.size()) {
			element = list.get(index);
		} else {
			element = null;
		}
		return element;
	}

	public T getParent() {
		return parent;
	}

	public int getCount() {
		return list.size();
	}

	public E current() {
		return getByInternalIndex();
	}

	public E get(int index) {
		if (index >= 0 && index < list.size()) {
			this.index = index;
		} else {
			this.index = -1;
		}
		return getByInternalIndex();
	}

	public E find(E item) {
		index = list.indexOf(item);
		return getByInternalIndex();
	}

	public E next() {
		if (index < list.size()) {
			++index;
		}
		return getByInternalIndex();
	}

	public E prior() {
		if (index >= 0) {
			--index;
		}
		return getByInternalIndex();
	}

	/*
	 * set current element - for test purpose only
	 * 
	 * @param element
	 */
	/*
	 * public void setCurrent(E element) { this.element = element; }
	 */

	public void setListFromParent(T parent) {
		this.parent = parent;
		list.clear();
		index = -1;
		randomGenerator = new Random();
		if (parent != null) {
			for (E item : parent.getList()) {
				if (item != null) {
					list.add(item);
				}
			}

			if (list.size() > 0) {
				index = 0;
				getByInternalIndex();
			}
		}
	}

	public List<Integer> createRandomOrder() {
		LinkedList<Integer> order = new LinkedList<>();

		while (order.size() < list.size()) {
			Integer random = randomGenerator.nextInt(list.size());
			if (!order.contains(random)) {
				order.add(random);
			}
		}

		return order;
	}

	public void setOrder(List<Integer> order) {
		if (order.size() > 0 && list.size() > 0) {
			LinkedList<E> tempList = new LinkedList<>();

			int size = Math.min(order.size(), list.size());
			for (int i = 0; i < size; ++i) {
				tempList.add(list.get(order.get(i)));
			}

			for (int i = size; i < list.size(); ++i) {
				tempList.add(list.get(i));
			}

			this.list = tempList;
		}
		index = 0;
	}

}
