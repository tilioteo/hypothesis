/**
 * 
 */
package com.tilioteo.hypothesis.model;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface Command {

	public final class Executor {
		public static final void execute(Command command) {
			if (command != null)
				command.execute();
		}
	}

	/**
	 * Causes the Command to perform its encapsulated behavior.
	 */
	void execute();
}
