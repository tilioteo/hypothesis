/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class UnaryExpression extends Expression {
	
	protected Primitive rightSide;
	
	private void clearRightSide() {
		if (rightSide != null) {
			if (rightSide instanceof Variable) {
				Variable variable = (Variable)rightSide;
				if (variable.decRefCount() == 0) {
					variables.remove(variable);
				}
			}
			
			rightSide = null;
		}
	}
	
	public UnaryExpression(Expression parent) {
		super(parent);
	}
	
	public Primitive getRightSide() {
		return rightSide;
	}
	
	public void setRightSide(Primitive rightSide) {
		if (this.rightSide != rightSide) {
			clearRightSide();
			
			this.rightSide = rightSide;
			
			if (this.rightSide != null) {
				if (this.rightSide instanceof Variable) {
					variables.add((Variable)this.rightSide);
				} else if (this.rightSide instanceof Expression) {
					Expression expression = (Expression)this.rightSide;
					if (expression.parent != this) {
						//throw new Exception("Cannot assign expression with different parent");
					}
				}
			}
		}
	}
	
	@Override
	public void clear() {
		clearRightSide();
		super.clear();
	}

	@Override
	public Object getValue() {
		if (rightSide != null) {
			Object rightValue = rightSide.getValue();
			if (rightValue != null && operator.isUnary()) {
				// apply unary operators
				switch (operator) {
				case NOT:
				case MINUS:
					if (rightValue instanceof Boolean)
						return !(Boolean)rightValue;
					else if (rightValue instanceof Double) {
						return -(Double)rightValue;
					} else if (rightValue instanceof Integer) {
						return -(Integer)rightValue;
					}
					break;

				default:
					return rightValue;
				}
			}
		}
		
		return null;
	}
}
