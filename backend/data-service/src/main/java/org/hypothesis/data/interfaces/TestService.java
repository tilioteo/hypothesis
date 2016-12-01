package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface TestService extends Serializable {

	SimpleTest findById(Long id);

	List<SimpleTest> findTestsBy(User user, Pack pack, Status... statuses);

	List<SimpleTest> findTestsBy(Pack pack, Collection<User> users, Date dateFrom, Date dateTo);

	SimpleTest getUnattendedTest(User user, Pack pack, boolean production);

	void updateTest(SimpleTest test);

	void saveEvent(Event event, SimpleTest test);

	SlideOrder findTaskSlideOrder(SimpleTest test, Task task);

	void updateSlideOrder(SlideOrder slideOrder);

}