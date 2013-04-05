/**
 * 
 */
package org.hypothesis.common;

import java.util.AbstractCollection;


/**
 * @author Kamil Morong - Hypothesis
 *
 */
public abstract class AbstractCollectionObject <C extends AbstractCollection<? extends E> , E>
				implements CollectionObjectInterface<AbstractCollection<? extends E>, E> {

	protected abstract void setOwner(C owner);
}
