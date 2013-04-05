/**
 * 
 */
package org.hypothesis.common.expression;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ClassNodeGroup extends OperatorNodeGroup {

	public ClassNodeGroup(int beginPosition, int endPosition, int level, OperatorNodeGroup parent) {
		super(beginPosition, endPosition, level, parent);
	}
	
	/*public boolean hasMethodGroup() {
		if (size() > 0) {
			for (HasOperatorNode obj : this) {
				if (obj instanceof MethodArgumentGroup)
					return true;
			}
		}
		return false;
	}*/
	
	public MethodArgumentGroup getArgumentGroup() {
		for (HasOperatorNode obj : this) {
			if (obj instanceof MethodArgumentGroup)
				return (MethodArgumentGroup)obj;
		}
		return null;
	}

}
