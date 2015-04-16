/**
 * 
 */
package com.tilioteo.hypothesis.model;

import com.tilioteo.hypothesis.core.ProcessManager;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.event.ErrorNotificationEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.persistence.TokenManager;

/**
 * @author kamil
 *
 */
public class ProcessModel {
	
	private TokenManager tokenManager;
	private ProcessManager processManager;
	
	public ProcessModel() {
		tokenManager = TokenManager.newInstance();
		processManager = new ProcessManager();
	}
	
	public void followToken(String tokenUid) {
		Token token = tokenManager.findTokenByUid(tokenUid);
		processManager.setAutoSlideShow(false);
		processManager.processToken(token, false);
	}

	/**
	 * This method will save test event
	 */
	public void fireTestError() {
		processManager.fireTestError();
	}

	/**
	 * This method does notification only.
	 * 
	 * @param caption Error description
	 */
	public void fireError(String caption) {
		ProcessEventBus.get().post(new ErrorNotificationEvent(SimpleTest.DUMMY_TEST, caption));
	}

	public void requestBreak() {
		processManager.requestBreakTest();
	}
	
	public void processTest(SimpleTest test) {
		processManager.processTest(test);
	}
	
	public void processSlideFollowing(Slide slide, Direction direction) {
		processManager.processSlideFollowing(slide, direction);
	}
	
	public void purgeFactories() {
		processManager.purgeFactories();
	}
}
