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
public interface DocumentWriter extends Serializable {

	public String writeString(Document document);

}
