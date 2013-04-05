/**
 * 
 */
package org.hypothesis.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Kamil Morong - Hypothesis
 *
 */

/**
 * E element Type
 * S set Type
 * 
 * HashSet is not serializable, maybe bug
 * 
 */
@SuppressWarnings("serial")
public class ObjectSet<E, S extends HashSet<E>> extends HashSet<E> {
	
	public ObjectSet() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E element) {
		boolean res = false;
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<S, E> el = (AbstractCollectionObject<S, E>)element;
				res = super.add(element);
			
				if (res)
					el.setOwner((S)this);
			}
			catch (Throwable e) {}
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
        for (E element : this) {
    		if (element instanceof AbstractCollectionObject<?, ?>) {
    			try {
    				AbstractCollectionObject<S, E> el = (AbstractCollectionObject<S, E>)element;
    				el.setOwner(null);
    			}
    			catch (Throwable e) {}
    		}
        }
		super.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		boolean res = false; 
		if (o instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<S, E> el = (AbstractCollectionObject<S, E>)o;
				res = super.remove(o);
			
				if (res)
					el.setOwner(null);
			}
			catch (Throwable e) {}
		}

		return res; 
	}

	public Set<E> getSet() {
		return this;
	}
	
	public void setSet(Set<E> set) {
		// make clone of this
		Set<E> thisSet = new HashSet<E>();
		
		for (E element : this)
			thisSet.add(element);
		
		for (E element : thisSet) {
			if (!set.contains(element)) {
				this.remove(element);
			}
		}
		
		for (E element : set) {
			if (!this.contains(element)) {
				this.add(element);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean validElement = false;

		for (E element : c) {
			if (element instanceof AbstractCollectionObject<?, ?>) {
				try {
					AbstractCollectionObject<S, E> el = (AbstractCollectionObject<S, E>)element;
					el.setOwner((S)this);
					validElement = true;
				} catch (Throwable e) {}
			}
			else
				validElement = false;
			
			if (!validElement) {
				// reset owner for all of objects since first error occurs 
				for (E elem : c) {
					if (elem instanceof AbstractCollectionObject<?, ?>) {
						try {
							AbstractCollectionObject<S, E> el = (AbstractCollectionObject<S, E>)elem;
							el.setOwner(null);
						}
						catch (Throwable e) {
							break;
						}
					}
				}
			}
		}
		
		if (!validElement) {
			boolean result = super.addAll(c);
			
			if (!result) {
				// reject whole collection, reset owner for all of objects 
				for (E elem : c) {
					if (elem instanceof AbstractCollectionObject<?, ?>) {
						try {
							AbstractCollectionObject<S, E> el = (AbstractCollectionObject<S, E>)elem;
							el.setOwner(null);
						} catch (Throwable t) {
							break;
						}
					}
				}
			}
			return result;
		}
		
		return false; 
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ObjectSet<?, ?>))
			return false;
        
        ObjectSet<E, S> other = (ObjectSet<E, S>)obj;
        
        if (this.size() != other.size())
        	return false;
        
        for (E element : this) {
        	if (!other.contains(element))
        		return false;
        }
        
        return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		for (E element : this) {
			result = prime * result + element.hashCode();
		}
		
		return result;
	}

}
