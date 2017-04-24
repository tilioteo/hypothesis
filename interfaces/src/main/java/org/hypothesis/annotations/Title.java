/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.annotations;

import com.vaadin.server.FontAwesome;

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
@Target({ ElementType.TYPE })
public @interface Title {
	String value();

	FontAwesome icon();
	
	int index();
}
