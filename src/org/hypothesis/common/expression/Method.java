/**
 * 
 */
package org.hypothesis.common.expression;

import org.hypothesis.annotation.ExpressionScope;
import org.hypothesis.annotation.ExpressionScope.Scope;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class Method extends Primitive implements HasReference {
	
	private Primitive reference;
	private String name;
	private Primitive[] arguments;
	
	public Method(String name, Primitive... arguments) {
		this.name = name;
		this.arguments = arguments;
	}
	
	@Override
	public Object getValue() {
		if (reference != null) {
			Object obj = reference.getValue();

			if (obj != null) {
				java.lang.reflect.Method method;
				try {
					Class<?> argTypes[];
					Object args[];
					if (arguments != null) {
						argTypes = new Class<?>[arguments.length];
						args = new Object[arguments.length];
						
						for (int i = 0; i < arguments.length; ++i) {
							args[i] = arguments[i].getValue();
							argTypes[i] = getPrimitiveType(args[i]);
						}
					} else {
						argTypes = new Class<?>[] {};
						args = new Object[] {};
					}
					
					/*java.lang.reflect.Method[] methods = obj.getClass().getDeclaredMethods();
					for (int i = 0; i < methods.length; ++i) {
						java.lang.reflect.Method method2 = methods[i];
						Class<?>[] types = method2.getParameterTypes();
					}*/
					
					// TODO: find substitute method for parameter typecast
					method = obj.getClass().getDeclaredMethod(name, argTypes);
					//method = obj.getClass().getMethod(name, argTypes);
					
					if (method != null) {
						if (method.isAnnotationPresent(ExpressionScope.class)) {
							ExpressionScope scope = method.getAnnotation(ExpressionScope.class);
							if (Scope.PRIVATE.equals(scope.value())) {
								throw new Exception(String.format("Method '%s' of class '%s' is eliminated from expression evaluation.", method.getName(), obj.getClass().getName()));
							}
						}
						Object res = method.invoke(obj, args);
						return res;
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println(e.getMessage());
				}
			} /*else
				throw new NullReferenceException(String.format("Object reference for method %s is null", name));*/
		}
		return null;
	}
	
	private	Class<?> getPrimitiveType(Object value) {
		if (value == null)
			return Object.class;
		else if (value instanceof Integer || value.getClass() == int.class)
			return int.class;
		else if (value instanceof Double || value.getClass() == double.class)
			return double.class;
		else if (value instanceof Boolean || value.getClass() == boolean.class)
			return boolean.class;
		else
			return Object.class;
	}

	
	public String getName() {
		return name;
	}
	
	public void setReference(Primitive reference) {
		this.reference = reference;
	}

}
