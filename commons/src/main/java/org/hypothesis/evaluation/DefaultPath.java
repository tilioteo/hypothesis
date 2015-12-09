/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
