/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.extension;

import java.util.Random;

import org.hypothesis.interfaces.Extension;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class RandomGenerator implements Extension {

	private Random random = new Random();

	public int getInteger() {
		return random.nextInt();
	}

	public int getInteger(int range) {
		return random.nextInt(range);
	}

	public double getDouble() {
		return random.nextDouble();
	}

}
