/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class MethodArgumentGroup extends OperatorNodeGroup {
	
	private OperatorNode operatorNode;

	public MethodArgumentGroup(int beginPosition, int endPosition, int level, OperatorNodeGroup parent) {
		super(beginPosition, endPosition, level, parent);
	}

	@Override
	public OperatorNode getOperatorNode() {
		return operatorNode;
	}

	public void setOperatorNode(OperatorNode operatorNode) {
		this.operatorNode = operatorNode;
	}
	
	public Primitive[] getArgumentPrimitives() {
		Primitive[] arguments = new Primitive[this.size()];

		for (int i = 0; i < this.size(); ++i) {
			HasOperatorNode element = this.get(i);
			if (element instanceof MethodArgument) {
				Primitive argument = ExpressionFactory.parseString(((MethodArgument)element).getText());
				arguments[i] = argument;
			}
		}
		
		return arguments;
	}

}
