/**
 * 
 */
package com.tilioteo.hypothesis.model;

import com.tilioteo.hypothesis.core.ProcessManager;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.event.AfterRenderContentEvent;
import com.tilioteo.hypothesis.event.CloseTestEvent;
import com.tilioteo.hypothesis.event.ErrorNotificationEvent;
import com.tilioteo.hypothesis.event.FinishSlideEvent.Direction;
import com.tilioteo.hypothesis.event.ProcessEventListener;
import com.tilioteo.hypothesis.persistence.TokenManager;
import com.tilioteo.hypothesis.ui.LayoutComponent;

/**
 * @author kamil
 *
 */
public class ProcessModel {
	
	private TokenManager tokenManager;
	private ProcessManager processManager;
	
	public ProcessModel(ProcessEventListener listener) {
		tokenManager = TokenManager.newInstance();
		processManager = new ProcessManager(listener);
	}
	
	public void followToken(String tokenUid) {
		Token token = tokenManager.findTokenByUid(tokenUid);
		processManager.setAutoSlideShow(false);
		processManager.processToken(token, false);
	}

	public void fireAfterRender(LayoutComponent content) {
		processManager.getProcessEventManager().fireEvent(
				new AfterRenderContentEvent(content));
	}

	/**
	 * This method will save test event
	 */
	public void fireTestError() {
		processManager.fireTestError();
	}

	public void fireClose(SimpleTest test) {
		processManager.getProcessEventManager().fireEvent(
				new CloseTestEvent(test));
	}

	/**
	 * This method does notification only.
	 * 
	 * @param caption Error description
	 */
	public void fireError(String caption) {
		processManager.getProcessEventManager().fireEvent(
				new ErrorNotificationEvent(SimpleTest.DUMMY_TEST, caption));
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
