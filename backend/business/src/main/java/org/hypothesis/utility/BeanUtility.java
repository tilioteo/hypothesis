/**
 * 
 */
package org.hypothesis.utility;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.Bean;

/**
 * @author kamil
 *
 */
public class BeanUtility {

	private BeanUtility() {
	}

	public static boolean isAnnotated(Bean<?> bean, Class<? extends Annotation> annotationClass) {
		return getAnnotation(bean, annotationClass) != null;
	}

	public static <A extends Annotation> A getAnnotation(Bean<?> bean, Class<A> annotationClass) {
		return (bean != null && annotationClass != null && !bean.isNullable())
				? bean.getBeanClass().getAnnotation(annotationClass) : null;
	}

}
