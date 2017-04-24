/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import java.io.Serializable;

/**
 * @author morongk
 *
 */
public interface EntityService<T extends HasId<ID>, ID> extends Serializable {
	
	T findById(ID id);

}
