/**
 * 
 */
package org.hypothesis.application.collector.core;




/**
 * @author Kamil Morong - Hypothesis
 * 
 */
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
