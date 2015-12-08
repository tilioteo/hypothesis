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
public interface DocumentReader extends Serializable {

	public Document readString(String string);

}
