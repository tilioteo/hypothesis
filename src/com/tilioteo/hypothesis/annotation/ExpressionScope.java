/**
 * 
 */
package com.tilioteo.hypothesis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kamil
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ExpressionScope {
	public enum Scope {PUBLIC, PRIVATE}
	Scope value();
}
