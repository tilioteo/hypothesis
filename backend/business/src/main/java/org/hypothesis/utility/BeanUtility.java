/**
 * 
 */
package org.hypothesis.utility;

import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;

/**
 * @author kamil
 *
 */
public final class BeanUtility {

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
