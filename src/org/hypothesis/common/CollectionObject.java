/**
 * 
 */
package org.hypothesis.common;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;


/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class CollectionObject <C extends AbstractCollection<? extends E> , E>
							extends AbstractCollectionObject<C, E>
							implements Serializable {
	
	private C owner = null; 

	public C getOwner() {
		return owner;
	}

	@Override
	protected void setOwner(C owner) {
		this.owner = owner;
	}

	@SuppressWarnings("unchecked")
	public int getOrder() {
		int order = -1;
		
		if (owner != null) {
			try {
				AbstractList<E> list = (AbstractList<E>)owner;
				
				for (E element : list) {
					++order;
					if (element == this) {
						return order;
					}
				}
			} catch(Throwable e) {}
		}
		
		return order;
	}
}
