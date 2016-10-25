/**
 * 
 */
package org.hypothesis.common;

/**
 * @author Kamil Morong
 *
 */
public abstract class Sequence<T extends Number> {

	private T value;
	private T start;

	public Sequence(T value) {
		this.start = copy(value);
		this.value = copy(value);
	}

	public Sequence() {
		value = null;
		start = null;
	}

	protected abstract T zero();

	protected abstract T copy(T value);

	protected abstract T inc(T current);

	public T current() {
		return value;
	}

	public T next() {
		value = inc(value);
		return current();
	}
	
	public void reset() {
		value = copy(start);
	}
}
