/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ComponentDataPojoGenerator {

	private ComponentDataPojoGenerator() {
	}

	/**
	 * Generate new POJO class dynamically
	 * 
	 * @param className
	 *            name of generated class
	 * @param superClass
	 *            super class to inherit from
	 * @param properties
	 *            properties to be defined in POJO, getters and setters are
	 *            generated as well
	 * @param structures
	 *            optional {@link Structured} annotation passed to properties
	 * @return new Class object or null when an error occurs
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public static Class<?> generate(String className, Class<?> superClass, Map<String, Class<?>> properties,
			Map<String, String> structures) throws NotFoundException, CannotCompileException {
		// TODO replace structures map by specific annotation instances for
		// general use
		ClassPool pool = ClassPool.getDefault();

		CtClass cc = null;

		try {
			cc = pool.get(className);
		} catch (NotFoundException e) {
			e.getMessage();
		}

		if (null == cc) {
			cc = pool.makeClass(className);

			ClassFile ccFile = cc.getClassFile();
			ConstPool constpool = ccFile.getConstPool();

			// define a super class to extend
			CtClass superCtClass = resolveCtClass(superClass);
			cc.setSuperclass(superCtClass);

			// add this to define an interface to implement, ie:
			// cc.addInterface(resolveCtClass(Serializable.class));

			final CtClass finalCc = cc;
			properties.entrySet().forEach(e -> {
				CtField field;
				try {
					field = new CtField(resolveCtClass(e.getValue()), e.getKey(), finalCc);

					// specific part - insert annotation to mark serialized
					// structure of
					// field
					String structure = structures.get(e.getKey());
					if (structure != null) {
						AnnotationsAttribute attr = new AnnotationsAttribute(constpool,
								AnnotationsAttribute.visibleTag);
						Annotation annot = new Annotation(Structured.class.getName(), constpool);
						annot.addMemberValue("value", new StringMemberValue(structure, ccFile.getConstPool()));
						attr.addAnnotation(annot);
						field.getFieldInfo().addAttribute(attr);
					}

					finalCc.addField(field);

					// add getter
					finalCc.addMethod(generateGetter(finalCc, e.getKey(), e.getValue()));

					// add setter
					// cc.addMethod(generateSetter(cc, entry.getKey(),
					// entry.getValue()));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			return cc.toClass();
		} else {
			Class<?> result = null;

			try {
				result = Class.forName(className);
			} catch (ClassNotFoundException e) {
			}

			return result;
		}

	}

	private static CtMethod generateGetter(CtClass declaringClass, String fieldName, Class<?> fieldClass)
			throws CannotCompileException {

		String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

		StringBuilder sb = new StringBuilder();
		sb.append("public ").append(fieldClass.getName()).append(" ").append(getterName).append("(){")
				.append("return this.").append(fieldName).append(";").append("}");
		return CtMethod.make(sb.toString(), declaringClass);
	}

	/*
	 * private static CtMethod generateSetter(CtClass declaringClass, String
	 * fieldName, Class<?> fieldClass) throws CannotCompileException {
	 * 
	 * String setterName = "set" + fieldName.substring(0, 1).toUpperCase() +
	 * fieldName.substring(1);
	 * 
	 * StringBuffer sb = new StringBuffer(); sb.append("public void "
	 * ).append(setterName).append("(").append(fieldClass.getName()).append(" ")
	 * .append(fieldName).append(")").append("{").append("this.").append(
	 * fieldName).append("=") .append(fieldName).append(";").append("}"); return
	 * CtMethod.make(sb.toString(), declaringClass); }
	 */

	private static CtClass resolveCtClass(Class<?> clazz) throws NotFoundException {
		ClassPool pool = ClassPool.getDefault();
		ClassLoader loader = clazz.getClassLoader();
		pool.appendClassPath(new LoaderClassPath(loader));
		return pool.get(clazz.getName());
	}

}
