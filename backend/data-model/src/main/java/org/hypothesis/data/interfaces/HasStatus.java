/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.api.Status;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface HasStatus extends Serializable {

	Status getStatus();

}
