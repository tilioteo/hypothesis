/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.annotation.ExpressionScope;
import com.tilioteo.hypothesis.annotation.ExpressionScope.Scope;
import com.tilioteo.hypothesis.annotation.ExpressionScopePrivate;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class Attribute extends Variable implements HasReference {
	
	private static Logger log = Logger.getLogger(Attribute.class);

	private Primitive reference;
	
	public Attribute(String name) {
		super(name);
	}
	
	public String getString() {
		Object val = getValue();
		if (val instanceof Variable)
			val = ((Variable)val).getValue();
		if (val instanceof String)
			return (String)val;
		else if (val instanceof Double) {
			return Double.toString((Double)val);
		} else if (val instanceof Integer) {
			return Integer.toString((Integer)val);
		} else if (val instanceof Boolean) {
			return Boolean.toString((Boolean)val);
		} else
			return null;
	}

	@Override
	public Object getValue() {
		if (reference != null) {
			Object obj = reference.getValue();
	
			if (obj != null) {
				boolean classPrivateScope = obj.getClass().isAnnotationPresent(ExpressionScopePrivate.class);
				
				java.lang.reflect.Field field;
				try {
					field = obj.getClass().getField(getName());
					if (field.isAnnotationPresent(ExpressionScope.class)) {
						ExpressionScope scope = field.getAnnotation(ExpressionScope.class);
						if (classPrivateScope || Scope.PRIVATE.equals(scope.value())) {
							throw new Exception(String.format("Field '%s' of class '%s' is eliminated from expression evaluation.", field.getName(), obj.getClass().getName()));
						}
					}
					
					Object res = field.get(obj);
					return res;
				} catch (Exception e) {
					log.error(e.getMessage());
					// TODO: handle exception
					System.err.println();
				}
			} /*else
				throw new NullReferenceException(String.format("Object reference for method %s is null", name));*/
		}
		return null;
	}
	
	public void setReference(Primitive reference) {
		this.reference = reference;
	}

}
