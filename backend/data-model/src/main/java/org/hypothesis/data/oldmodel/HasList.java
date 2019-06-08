/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.oldmodel;

import java.io.Serializable;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Deprecated
public interface HasList<E> extends Serializable {

	List<E> getList();

}
