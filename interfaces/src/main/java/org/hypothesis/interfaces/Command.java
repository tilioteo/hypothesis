/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface Command extends Serializable {

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
}
