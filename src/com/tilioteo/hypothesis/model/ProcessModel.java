/**
 * 
 */
package com.tilioteo.hypothesis.model;

import java.io.Serializable;

import com.tilioteo.hypothesis.core.ProcessManager;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Token;
import com.tilioteo.hypothesis.event.ErrorNotificationEvent;
import com.tilioteo.hypothesis.event.ProcessEventBus;
import com.tilioteo.hypothesis.persistence.TokenService;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessModel implements Serializable {
	
	private TokenService tokenService;
	private ProcessManager processManager;
	
	public ProcessModel() {
		tokenService = TokenService.newInstance();
		processManager = new ProcessManager();
	}
	
	public void followToken(String tokenUid) {
		Token token = tokenService.findTokenByUid(tokenUid);
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
		ProcessEventBus.get().post(new ErrorNotificationEvent(caption));
	}

	public void requestBreak() {
		processManager.requestBreakTest();
	}
	
	public void processTest(SimpleTest test) {
		processManager.processTest(test);
	}
	
	/*public void processSlideFollowing(Direction direction) {
		processManager.processSlideFollowing(direction);
	}*/
	
	public void clean() {
		processManager.clean();
		processManager.purgeFactories();
	}
	
}
