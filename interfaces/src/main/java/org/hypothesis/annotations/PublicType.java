/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.annotations;

import java.lang.annotation.*;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface PublicType {

}
