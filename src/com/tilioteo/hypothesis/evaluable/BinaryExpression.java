/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class BinaryExpression extends UnaryExpression {
	
	protected Primitive leftSide;

	private void clearLeftSide() {
		if (leftSide != null) {
			if (leftSide instanceof Variable) {
				Variable variable = (Variable)leftSide;
				if (variable.decRefCount() == 0) {
					variables.remove(variable);
				}
			}
			
			leftSide = null;
		}
	}
	
	public BinaryExpression(Expression parent) {
		super(parent);
	}
	
	public Primitive getLeftSide() {
		return leftSide;
	}
	
	public void setLeftSide(Primitive leftSide) {
		if (this.leftSide != leftSide) {
			clearLeftSide();
			
			this.leftSide = leftSide;
			
			if (this.leftSide != null) {
				if (this.leftSide instanceof Variable) {
					variables.add((Variable)this.leftSide);
				} else if (this.leftSide instanceof Expression) {
					Expression expression = (Expression)this.leftSide;
					if (expression.parent != this) {
						//throw new Exception("Cannot assign expression with different parent");
					}
				}
			}
		}
	}
	
	@Override
	public void clear() {
		clearLeftSide();
		super.clear();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object getValue() {
		if (leftSide != null && rightSide != null && operator.isBinary()) {
			if (leftSide instanceof Variable && operator.equals(Operator.ASSIGN)) {
				Object rightValue = rightSide.getValue();
				leftSide.setValue(rightValue);
				return leftSide;
			} else if (operator.equals(Operator.DECLASS) && rightSide instanceof HasReference) {
				HasReference right = (HasReference)rightSide;
				right.setReference(leftSide);
				return rightSide.getValue();
			} else {
				Object leftValue = leftSide.getValue();
				Object rightValue = rightSide.getValue();
				
				if (leftValue != null && rightValue != null) {
					if (leftValue instanceof Boolean && rightValue instanceof Boolean) {
						boolean left = (Boolean)leftValue;
						boolean right = (Boolean)rightValue;
						switch (operator) {
						case PLUS:
						case OR:
							return left || right;
						case MULTIPLY:
						case AND:
							return left && right;
						case EQUALS:
							return left == right;
						case NOT_EQUALS:
							return left != right;
						case XOR:
							return left ^ right;
						}
					}
					
					if (leftValue instanceof Integer) {
						int left = (Integer)leftValue;
						
						if (rightValue instanceof Integer) {
							int right = (Integer)rightValue;
							switch (operator) {
							case MINUS:
								return left - right;
							case PLUS:
								return left + right;
							case MULTIPLY:
								return left * right;
							case DIVIDE:
								return left / right;
							case EQUALS:
								return left == right;
							case NOT_EQUALS:
								return left != right;
							case GREATER:
								return left > right;
							case LESS:
								return left < right;
							case GREATER_OR_EQUAL:
								return left >= right;
							case LESS_OR_EQUAL:
								return left <= right;
							case AND:
								return left & right;
							case OR:
								return left | right;
							case XOR:
								return left ^ right;
							}
						} else if (rightValue instanceof Double) {
							double right = (Double)rightValue;
							switch (operator) {
							case MINUS:
								return left - right;
							case PLUS:
								return left + right;
							case MULTIPLY:
								return left * right;
							case DIVIDE:
								return left / right;
							case EQUALS:
								return left == right;
							case NOT_EQUALS:
								return left != right;
							case GREATER:
								return left > right;
							case LESS:
								return left < right;
							case GREATER_OR_EQUAL:
								return left >= right;
							case LESS_OR_EQUAL:
								return left <= right;
							}
						}
					}
					
					if (leftValue instanceof Double) {
						double left = (Double)leftValue;
						
						if (rightValue instanceof Double) {
							double right = (Double)rightValue;
							
							switch (operator) {
							case MINUS:
								return left - right;
							case PLUS:
								return left + right;
							case MULTIPLY:
								return left * right;
							case DIVIDE:
								return left / right;
							case EQUALS:
								return left == right;
							case NOT_EQUALS:
								return left != right;
							case GREATER:
								return left > right;
							case LESS:
								return left < right;
							case GREATER_OR_EQUAL:
								return left >= right;
							case LESS_OR_EQUAL:
								return left <= right;
							}
						} else if (rightValue instanceof Integer) {
							int right = (Integer)rightValue;
							switch (operator) {
							case MINUS:
								return left - right;
							case PLUS:
								return left + right;
							case MULTIPLY:
								return left * right;
							case DIVIDE:
								return left / right;
							case EQUALS:
								return left == right;
							case NOT_EQUALS:
								return left != right;
							case GREATER:
								return left > right;
							case LESS:
								return left < right;
							case GREATER_OR_EQUAL:
								return left >= right;
							case LESS_OR_EQUAL:
								return left <= right;
							}
						}
					}
					
					if (leftValue instanceof String && rightValue instanceof String) {
						String left = (String)leftValue;
						String right = (String)rightValue;
						switch (operator) {
						case PLUS:
							return left + right;
						case EQUALS:
							return left.equals(right);
						case NOT_EQUALS:
							return !left.equals(right);
						case GREATER:
							return left.compareTo(right) > 0;
						case LESS:
							return left.compareTo(right) < 0;
						case GREATER_OR_EQUAL:
							return left.compareTo(right) >= 0;
						case LESS_OR_EQUAL:
							return left.compareTo(right) <= 0;
						}
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		return "(" + leftSide.toString() + operator.toString() + rightSide.toString() + ")";
	}
}
