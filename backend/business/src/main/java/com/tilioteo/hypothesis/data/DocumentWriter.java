/**
 * 
 */
package com.tilioteo.hypothesis.data;

import java.io.Serializable;

import com.tilioteo.hypothesis.interfaces.Document;

/**
 * @author kamil
 *
 */
public interface DocumentWriter extends Serializable {

	public String writeString(Document document);

}
