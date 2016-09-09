/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface Structured {

	/**
	 * String pattern for property structure
	 * 
	 * @return
	 */
	public String value();
}
