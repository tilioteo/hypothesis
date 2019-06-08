/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hypothesis.data.interfaces.HasId;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class KeySetManager<E extends HasId<ID>, ID> implements Serializable {

	private Map<ID, E> elementByIdMap;

	private ID id;

	private E element;

	public KeySetManager() {
		reset();
	}

	public E findById(ID id) {
		this.id = id;
		return getByIdInternal();
	}

	public E current() {
		return getByIdInternal();
	}

	public void setList(List<E> list) {
		reset();

		if (isNotEmpty(list)) {
			elementByIdMap = list.stream()//
					.collect(toMap(k -> k.getId(), identity(), (u, v) -> v, LinkedHashMap::new));
			id = elementByIdMap.keySet().stream().findFirst().orElse(null);
			getByIdInternal();
		}
	}

	private void reset() {
		elementByIdMap = emptyMap();
		id = null;
		element = null;
	}

	private E getByIdInternal() {
		if (id != null && elementByIdMap.containsKey(id)) {
			element = elementByIdMap.get(id);
		} else {
			element = null;
			id = null;
		}
		return element;
	}
}
