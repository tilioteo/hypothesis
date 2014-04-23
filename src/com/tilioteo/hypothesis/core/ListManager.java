/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.tilioteo.hypothesis.entity.HasList;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ListManager<T extends HasList<E>, E> {

	private LinkedList<E> list = new LinkedList<E>();

	private E element = null;
	
	private Random randomGenerator;

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
		list.clear();
		randomGenerator = new Random();
		if (parent != null) {
			for (E item : parent.getList()) {
				if (item != null) {
					list.add(item);
				}
			}
		}
		next();
	}
	
	public List<Integer> createRandomOrder() {
		LinkedList<Integer> order = new LinkedList<Integer>();
		
		while (order.size() < list.size()) {
			Integer random = randomGenerator.nextInt(list.size());
			if (!order.contains(random)) {
				order.add(random);
			}
		}
		/*for (int i = 0; i < list.size(); ++i) {
			order.add();
		}*/
		
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
	}
	
}
