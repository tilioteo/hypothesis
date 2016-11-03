/**
 * 
 */
package com.tilioteo.hypothesis.core;

import com.tilioteo.hypothesis.interfaces.CoreObject;

import java.util.Random;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class RandomGenerator implements CoreObject {
	
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
