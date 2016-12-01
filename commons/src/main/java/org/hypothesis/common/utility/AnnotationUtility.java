/**
 * 
 */
package org.hypothesis.common.utility;

import java.lang.annotation.Annotation;

/**
 * @author kamil
 *
 */
public class AnnotationUtility {

	public static <A extends Annotation> A getClassAnnotationRecursive(Class<?> clazz, Class<A> annotationClass) {
		if (clazz != null && annotationClass != null) {
			A annotation = clazz.getAnnotation(annotationClass);
			if (null == annotation) {
				Class<?> superClass = clazz.getSuperclass();
				while (superClass != null) {
					annotation = superClass.getAnnotation(annotationClass);
					if (annotation != null) {
						return annotation;
					}
					
					superClass = superClass.getSuperclass();
				}
			}
		}
		return null;
	}
}
