/**
 * 
 */
package org.hypothesis.common.expression;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
final class OperatorNode implements HasOperatorNode {
	
	private int position;
	private Operator operator;
	private boolean unary;
	private OperatorNode leftNode;
	private OperatorNode rightNode;
	private OperatorNodeGroup group;
	
	public OperatorNode() {
		this(0, null);
	}
	
	public OperatorNode(int position, Operator operator) {
		this.group = null;
		this.position = position;
		this.operator = operator;
		this.unary = false;
		this.leftNode = null;
		this.rightNode = null;
	}
	
	public int getPosition() {
		return position;
	}
	
	protected void setPosition(int position) {
		this.position = position;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	protected void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public OperatorNode getLeftNode() {
		return leftNode;
	}
	
	public void setLeftNode(OperatorNode leftNode) {
		this.leftNode = leftNode;
	}
	
	public OperatorNode getRightNode() {
		return rightNode;	
	}

	public void setRightNode(OperatorNode rightNode) {
		this.rightNode = rightNode;
	}
	
	public int getOperatorPriority() {
		if (operator == null) {
			return 0;
		} else if (unary) {
			return 1;
		} else {
			return operator.getPriority();
		}
	}
	
	public boolean isUnary() {
		return unary;
	}
	
	protected void setUnary(boolean unary) {
		this.unary = unary;
	}

	public OperatorNode getOperatorNode() {
		return this;
	}

	public OperatorNodeGroup getGroup() {
		return group;
	}

	public void setGroup(OperatorNodeGroup group) {
		this.group = group;
	}
	
}
