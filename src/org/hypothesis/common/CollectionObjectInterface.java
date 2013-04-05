/**
 * 
 */
package org.hypothesis.common;

import java.util.AbstractCollection;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public interface CollectionObjectInterface <C extends AbstractCollection<? extends E> , E> {

	C getOwner();
	//void setOwner(C owner);
	
	int getOrder();
	
}
