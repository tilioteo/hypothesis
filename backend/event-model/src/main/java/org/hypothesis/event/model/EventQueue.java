/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class EventQueue implements Serializable {

	private ArrayList<EventWrapper> list = new ArrayList<>();

	public void add(EventWrapper eventWrapper) {
		list.add(eventWrapper);
	}

	public List<EventWrapper> getList() {
		return list;
	}

	public void clear() {
		list.clear();
	}
}
