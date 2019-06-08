/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import static java.util.Collections.emptyMap;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.hypothesis.data.interfaces.HasId;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ListManager<E extends HasId<ID>, ID> implements Serializable {

	private Map<ID, Integer> indexByidMap;
	private Map<Integer, E> elementByindexMap;

	private Integer index;

	private E element;

	private Random randomGenerator;

	public ListManager() {
		reset();
	}

	public int size() {
		return indexByidMap.size();
	}

	public E findById(ID id) {
		index = indexByidMap.get(id);
		return getByInternalIndex();
	}

	public E findByIndex(int index) {
		this.index = index;
		return getByInternalIndex();
	}

	public E current() {
		return getByInternalIndex();
	}

	public E next() {
		if (index != null) {
			++index;
		}
		return getByInternalIndex();
	}

	public E prior() {
		if (index != null) {
			--index;
		}
		return getByInternalIndex();
	}

	public void setList(List<E> list) {
		reset();

		if (isNotEmpty(list)) {
			indexByidMap = new HashMap<>();
			elementByindexMap = new HashMap<>();
			AtomicInteger aInt = new AtomicInteger(0);

			list.forEach(e -> {
				indexByidMap.put(e.getId(), aInt.get());
				elementByindexMap.put(aInt.getAndIncrement(), e);
			});
			randomGenerator = new Random();

			index = 0;
			getByInternalIndex();
		}
	}

	public List<Integer> createRandomOrder() {
		List<Integer> order = new LinkedList<>();

		while (order.size() < size()) {
			int random = randomGenerator.nextInt(size());
			if (!order.contains(random)) {
				order.add(random);
			}
		}

		return order;
	}

	public void setOrder(List<Integer> order) {
		if (isNotEmpty(order) && !indexByidMap.isEmpty()) {
			List<E> tempList = new LinkedList<>();

			int size = Math.min(order.size(), size());
			for (int i = 0; i < size; ++i) {
				tempList.add(elementByindexMap.get(order.get(i)));
			}

			for (int i = size; i < size(); ++i) {
				tempList.add(elementByindexMap.get(i));
			}

			setList(tempList);
		}
	}

	protected void setIndex(int index) {
		if (index >= 0 && index < size()) {
			this.index = index;
		}
	}

	private void reset() {
		indexByidMap = emptyMap();
		elementByindexMap = emptyMap();
		index = null;
		element = null;
	}

	private E getByInternalIndex() {
		if (index != null && indexInBounds(index)) {
			element = elementByindexMap.get(index);
		} else {
			element = null;
			index = null;
		}
		return element;
	}

	private boolean indexInBounds(int index) {
		return index >= 0 && index < size();
	}

}
