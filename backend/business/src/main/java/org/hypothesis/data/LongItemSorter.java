package org.hypothesis.data;

import java.io.Serializable;
import java.util.Comparator;

import com.vaadin.data.util.DefaultItemSorter;

@SuppressWarnings("serial")
public class LongItemSorter extends DefaultItemSorter {

	public LongItemSorter() {
		super(new LongPropertyValueComparator());
	}

	public static class LongPropertyValueComparator implements Comparator<Object>, Serializable {

		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			int r = 0;
			// Normal non-null comparison
			if (o1 != null && o2 != null) {
				if ((o1 instanceof Long) && (o2 instanceof Long)) {
					return ((Long) o1).compareTo((Long) o2);
				} else {
					// Assume the objects can be cast to Comparable, throw
					// ClassCastException otherwise.
					r = ((Comparable<Object>) o1).compareTo(o2);
				}
			} else if (o1 == o2) {
				// Objects are equal if both are null
				r = 0;
			} else {
				if (o1 == null) {
					r = -1; // null is less than non-null
				} else {
					r = 1; // non-null is greater than null
				}
			}

			return r;
		}
	}

}
