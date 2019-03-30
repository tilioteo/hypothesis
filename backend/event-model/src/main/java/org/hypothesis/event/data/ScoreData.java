/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.data;

import java.io.Serializable;
import java.util.Map;

import org.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ScoreData implements Serializable {

	public enum Source {
		SLIDE, ACTION
	}

	private Source source;
	private String id;
	private Map<Integer, ExchangeVariable> scores;

	public ScoreData(Source source, String id, Map<Integer, ExchangeVariable> scores) {
		this.source = source;
		this.id = id;
		this.scores = scores;
	}

	public Source getSource() {
		return source;
	}

	public String getId() {
		return id;
	}

	public Map<Integer, ExchangeVariable> getScores() {
		return scores;
	}

}
