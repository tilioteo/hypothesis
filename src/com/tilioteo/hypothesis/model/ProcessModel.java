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
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ProcessModel implements Serializable {
	
	private TokenService tokenService;
	private ProcessManager processManager;
	
	private ProcessEventBus bus;
	
	public ProcessModel() {
		this.bus = ProcessEventBus.get(UI.getCurrent());
		tokenService = TokenService.newInstance();
		processManager = new ProcessManager();
	}
	
	public void followToken(String tokenUid) {
		Token token = tokenService.findTokenByUid(tokenUid);
		processManager.setAutoSlideShow(false);
		// TODO maybe in the future send broadcast message to main view
		/*if (token != null && token.getViewUid() != null) {
			ProcessUIMessage message = new ProcessUIMessage(token.getViewUid());
			Broadcaster.broadcast(message.toString());
		}*/
		
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
		bus.post(new ErrorNotificationEvent(caption));
	}

	public void requestBreak() {
		processManager.requestBreakTest();
	}
	
	public void processTest(SimpleTest test) {
		processManager.processTest(test);
	}
	
	public void clean() {
		processManager.clean();
		processManager.purgeFactories();
	}
	
}
