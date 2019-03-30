/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@FunctionalInterface
public interface Command {

	final class Executor {

		public static void execute(Command command) {
			if (command != null) {
				command.execute();
			}
		}
	}

	/**
	 * Causes the Command to perform its encapsulated behavior.
	 */
	void execute();

	default Command andThen(Command after) {
		return () -> {
			execute();
			Executor.execute(after);
		};
	}
}
