/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import org.apache.log4j.Logger;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface Command {

	public final class Executor {
		
		private static Logger log = Logger.getLogger(Executor.class);
		
		public static final void execute(Command command) {
			if (command != null) {
				try {
					command.execute();
				} catch (Throwable e) {
					log.error("Error when executing command.", e);
				}
			}
		}
	}

	/**
	 * Causes the Command to perform its encapsulated behavior.
	 */
	void execute();
}
