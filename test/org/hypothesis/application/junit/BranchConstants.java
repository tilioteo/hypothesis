/**
 * 
 */
package org.hypothesis.application.junit;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class BranchConstants {

	public static final String BRANCH_XML = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<Branch>" +
"	<Path>" +
"		<BranchKey>prvni</BranchKey>" +
"		<Pattern>" +
"			<Nick SlideId=\"1\">" +
"				<Expression>result &lt; 3</Expression>" +
"			</Nick>" +
"		</Pattern>" +
"	</Path>" +
"	<Path>" +
"		<BranchKey>druha</BranchKey>" +
"		<Pattern>" +
"			<Nick SlideId=\"1\">" +
"				<Expression>result &lt; 5</Expression>" +
"			</Nick>" +
"		</Pattern>" +
"	</Path>" +
"	<DefaultPath>" +
"		<BranchKey>default</BranchKey>" +
"	</DefaultPath>" +
"</Branch>";

}
