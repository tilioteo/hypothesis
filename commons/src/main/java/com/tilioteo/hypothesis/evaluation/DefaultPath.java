/**
 * 
 */
package com.tilioteo.hypothesis.evaluation;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class DefaultPath extends AbstractBasePath {

	private String branchKey;

	@Override
	public String getBranchKey() {
		return branchKey;
	}

	@Override
	public void setBranchKey(String branchKey) {
		this.branchKey = branchKey;
	}

}
