/**
 * 
 */
package org.hypothesis.common;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Kamil Morong - Hypothesis
 *
 */

/**
 * E element Type
 * L list Type
 */
@SuppressWarnings("serial")
public class ObjectList<E, L extends LinkedList<E>> extends LinkedList<E> {
	private int lastIndex = -1;
	
	public ObjectList() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(int index, E element) {
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner((L)this);
			}
			catch (Throwable e) {}
		}

		super.add(index, element);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E element) {
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner((L)this);
			}
			catch (Throwable e) {}
		}

		return super.add(element);
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean validElement = false;

		for (E element : c) {
			if (element instanceof AbstractCollectionObject<?, ?>) {
				try {
					AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
					el.setOwner((L)this);
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
							AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)elem;
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
			boolean result = super.addAll(index, c);
			
			if (!result) {
				// reject whole collection, reset owner for all of objects 
				for (E elem : c) {
					if (elem instanceof AbstractCollectionObject<?, ?>) {
						try {
							AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)elem;
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
	public void addFirst(E element) {
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner((L)this);
			}
			catch (Throwable e) {}
		}

		super.addFirst(element);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addLast(E element) {
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner((L)this);
			}
			catch (Throwable e) {}
		}

		super.addLast(element);
	}

	@SuppressWarnings("unchecked")
	@Override
	public E remove(int index) {
		E element = super.remove(index); 
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner(null);
			}
			catch (Throwable e) {}
		}

		return element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E remove() {
		E element = super.remove(); 
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner(null);
			}
			catch (Throwable e) {}
		}

		return element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E removeFirst() {
		E element = super.removeFirst();
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner(null);
			}
			catch (Throwable e) {}
		}

		return element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E removeLast() {
		E element = super.removeLast();
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner(null);
			}
			catch (Throwable e) {}
		}

		return element;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		if (o instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)o;
				el.setOwner(null);
			}
			catch (Throwable e) {}
		}

		return super.remove(o);
	}

	@SuppressWarnings("unchecked")
	@Override
	public E set(int index, E element) {
		if (element instanceof AbstractCollectionObject<?, ?>) {
			try {
				AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
				el.setOwner((L)this);
			}
			catch (Throwable e) {}
		}

		return super.set(index, element);
	}
	
	public List<E> getList() {
		return this;
	}
	
	public void setList(List<E> list) {
		// make clone of this
		List<E> thisList = new LinkedList<E>();
		for (E element : this)
			thisList.add(element);
		
		for (E element : thisList) {
			if (!list.contains(element)) {
				this.remove(element);
			}
		}
		/*for (int i = 0; i < thisList.size(); ++i) {
			E element = this.get(i);
			if (!list.contains(element)) {
				this.remove(i);
			}
		}*/
		
		for (E element : list) {
			if (element != null) {
				int index = this.indexOf(element);
				if (index >= 0) {
					if (!this.get(index).equals(element)) {
						this.set(index, element);
					}
				}
				else {
					this.add(element);
				}
			}
		}
	}
	
	public void mergeList(List<E> list) {
		for (int i = 0; i < list.size(); ++i) {
			E element = list.get(i);
			if (!this.contains(element)) {
				list.remove(i);
			}
		}
		
		for (E element : this) {
			if (element != null) {
				int index = list.indexOf(element);
				if (index >= 0) {
					if (!list.get(index).equals(element)) {
						list.set(index, element);
					}
				}
				else {
					list.add(element);
				}
			}
		}
	}

	@Override
	public E getFirst() {
		if (size() > 0) {
			lastIndex = 0;
			return super.getFirst();
		}
		return null;
	}

	@Override
	public E getLast() {
		if (size() > 0) {
			lastIndex = size()-1;
			return super.getLast();
		}
		return null;
	}
	
	public E getNext() {
		if (size() > 0 && lastIndex < size()-1) {
			return get(++lastIndex);
		}
		return null;
	}

	public E getPred() {
		if (size() > 0 && lastIndex > 0) {
			return get(--lastIndex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ObjectList<?, ?>))
			return false;

		ObjectList<E, L> other = (ObjectList<E, L>) obj;

		if (this.size() != other.size())
			return false;

		for (int i = 0; i < this.size(); ++i) {
			if (!this.get(i).equals(other.get(i)))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		for (E element : this) {
			if (element instanceof AbstractCollectionObject<?, ?>) {
				try {
					AbstractCollectionObject<L, E> el = (AbstractCollectionObject<L, E>)element;
					el.setOwner(null);
				}
				catch (Throwable e) {}
			}
        }
		super.clear();
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
