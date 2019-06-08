/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.data;

import java.io.Serializable;

import org.hypothesis.data.dto.PackDto;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TestData implements Serializable {

	private PackDto pack;
	private boolean running = false;

	public TestData(PackDto pack) {
		this.pack = pack;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public PackDto getPack() {
		return pack;
	}
}
