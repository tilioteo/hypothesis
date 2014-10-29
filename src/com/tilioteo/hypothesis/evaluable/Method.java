/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.annotation.ExpressionScope;
import com.tilioteo.hypothesis.annotation.ExpressionScopePrivate;
import com.tilioteo.hypothesis.annotation.ExpressionScope.Scope;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class Method extends Primitive implements HasReference {
	
	private static Logger log = Logger.getLogger(Method.class);

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
				boolean classPrivateScope = obj.getClass().isAnnotationPresent(ExpressionScopePrivate.class);

				java.lang.reflect.Method method;
				try {
					Class<?> argTypes[];
					Object args[];
					if (arguments != null) {
						argTypes = new Class<?>[arguments.length];
						args = new Object[arguments.length];
						
						for (int i = 0; i < arguments.length; ++i) {
							args[i] = arguments[i].getValue();
							argTypes[i] = getValueType(args[i]);
						}
					} else {
						argTypes = new Class<?>[] {};
						args = new Object[] {};
					}
					
					// TODO: find substitute method for parameter typecast
					method = getDeclaredMethodDeeply(obj.getClass(), name, argTypes);
					if (method == null) {
						method = getDeclaredMethodCast(obj.getClass(), name, argTypes);
					}
					
					if (method != null) {
						if (method.isAnnotationPresent(ExpressionScope.class)) {
							ExpressionScope scope = method.getAnnotation(ExpressionScope.class);
							if (classPrivateScope || Scope.PRIVATE.equals(scope.value())) {
								throw new Exception(String.format("Method '%s' of class '%s' is eliminated from expression evaluation.", method.getName(), obj.getClass().getName()));
							}
						}
						// prepare arguments, do conversions if needed
						Class<?>[] argTypes2 = method.getParameterTypes();
						for (int i = 0; i < argTypes.length; ++i) {
							Class<?> type = argTypes[i];
							Class<?> type2 = argTypes2[i];
							
							if (!type2.isAssignableFrom(type)) { // parameters cannot be implicitly casted
								if  (type2 == String.class) { // try to convert to string
									if (type == Number.class) {
										args[i] = args[i].toString();
									} else  if (type == int.class || type == double.class || type == long.class || type == short.class || type == float.class) {
										args[i] = "" + args[i];
									}
								}
							}

						}
						
						Object res = method.invoke(obj, args);
						return res;
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					// TODO: handle exception
					System.err.println(e.getMessage());
				}
			} /*else
				throw new NullReferenceException(String.format("Object reference for method %s is null", name));*/
		}
		return null;
	}
	
	private java.lang.reflect.Method getDeclaredMethodDeeply(Class<?> clazz, String name, Class<?>... parameterTypes) {
		//log.debug(String.format("getDeclaredMethodDeeply: name = %s", name != null ? name : "NULL"));
		do {
			java.lang.reflect.Method method = null;
			try {
				method = clazz.getDeclaredMethod(name, parameterTypes);
				return method;
			} catch (Exception e) {
			}
			
			clazz = clazz.getSuperclass();
			
		} while (clazz != null);
		return null;
	}
	
	private java.lang.reflect.Method getDeclaredMethodCast(Class<?> clazz, String name, Class<?>... parameterTypes) {
		ArrayList<java.lang.reflect.Method> list = new ArrayList<java.lang.reflect.Method>();
		
		// get all methods with given name and parameter count
		do {
			java.lang.reflect.Method[] methods = getDeclaredMethodsByName(clazz, name, parameterTypes.length);
			for (java.lang.reflect.Method method : methods) {
				list.add(method);
			}
			
			clazz = clazz.getSuperclass();
			
		} while (clazz != null);
		
		double[] rank = new double[list.size()];
		// calculate method usability rank
		for (int i = 0; i < list.size(); ++i) {
			rank[i] = 0;
			java.lang.reflect.Method method = list.get(i);
			Class<?>[] parameterTypes2 = method.getParameterTypes();
			
			for (int j = 0; j < parameterTypes.length; ++j) {
				Class<?> type = parameterTypes[j];
				Class<?> type2 = parameterTypes2[j];
				
				if (type == type2) { // identical types are the best
					rank[i] += 5;
				} else if (type2.isAssignableFrom(type)) { // parameters are able to cast
					rank[i] += 3;
				} else if (type2 == String.class && (type == Number.class || type == int.class || type == double.class || type == long.class || type == short.class || type == float.class)) { // try to convert to string
					rank[i] += 1;
				} else { // cannot be used
					rank[i] = 0;
					break;
				}
			}
			rank[i] = rank[i]/parameterTypes.length;
			
		}
		// find index of max rank
		int maxIndex = -1;
		double max = 0;
		for (int i = 0; i < rank.length; ++i) {
			if (i == 0) {
				max = rank[i];
				maxIndex = i;
			} else if (rank[i] > max) {
				max = rank[i];
				maxIndex = i;
			}
		}
		
		if (maxIndex >= 0 && rank[maxIndex] > 0) {
			return list.get(maxIndex);
		}

		return null;
	}
	
	private java.lang.reflect.Method[] getDeclaredMethodsByName(Class<?> clazz, String name, int parameterCount) {
		ArrayList<java.lang.reflect.Method> list = new ArrayList<java.lang.reflect.Method>();
		java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
		
		for (java.lang.reflect.Method method : methods) {
			int modifiers = method.getModifiers();
			if (!Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && method.getName().equals(name) && method.getParameterTypes().length == parameterCount) {
				list.add(method);
			}
		}
		return list.toArray(new java.lang.reflect.Method[] {});
	}
	
	private	Class<?> getValueType(Object value) {
		if (value == null)
			return Object.class;
		else if (value instanceof Integer || value.getClass() == int.class)
			return int.class;
		else if (value instanceof Double || value.getClass() == double.class)
			return double.class;
		else if (value instanceof Boolean || value.getClass() == boolean.class)
			return boolean.class;
		else if (value instanceof String)
			return String.class;
		else
			return value.getClass();
	}

	
	public String getName() {
		return name;
	}
	
	public void setReference(Primitive reference) {
		this.reference = reference;
	}

}
