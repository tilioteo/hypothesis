/**
 * 
 */
package org.hypothesis.data;

import java.io.Serializable;

import org.hypothesis.interfaces.Document;

/**
 * @author kamil
 *
 */
public interface DocumentReader extends Serializable {

	public Document readString(String string);

}
