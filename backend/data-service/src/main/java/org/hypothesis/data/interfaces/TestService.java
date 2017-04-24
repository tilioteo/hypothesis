/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hypothesis.data.model.Event;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Score;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.SlideOrder;
import org.hypothesis.data.model.Status;
import org.hypothesis.data.model.Task;
import org.hypothesis.data.model.Test;
import org.hypothesis.data.model.User;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface TestService extends EntityService<SimpleTest, Long> {

	List<SimpleTest> findTestsBy(User user, Pack pack, Status... statuses);

	List<SimpleTest> findTestsBy(Pack pack, Collection<User> users, Date dateFrom, Date dateTo);
	
	List<Test> findTestScoresBy(Collection<User> users, Date dateFrom, Date dateTo);

	SimpleTest getUnattendedTest(User user, Pack pack, boolean production);

	void updateTest(SimpleTest test);

	void saveEvent(Event event, SimpleTest test);

	void saveScore(Score score, SimpleTest test);

	SlideOrder findTaskSlideOrder(SimpleTest test, Task task);

	void updateSlideOrder(SlideOrder slideOrder);

}