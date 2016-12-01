/**
 * 
 */
package org.hypothesis.common;

/**
 * @author Kamil Morong
 *
 */
public class IntSequence extends Sequence<Integer> {

	public IntSequence() {
		super();
	}

	public IntSequence(Integer value) {
		super(value);
	}

	@Override
	protected Integer zero() {
		return 0;
	}

	@Override
	protected Integer copy(Integer value) {
		return value != null ? value.intValue() : null;
	}

	@Override
	protected Integer inc(Integer current) {
		return current != null ? current + 1 : zero();
	}

}
