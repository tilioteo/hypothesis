package org.hypothesis.presenter;

import org.hypothesis.business.SessionManager;
import org.hypothesis.business.UserControlServiceImpl;
import org.hypothesis.business.data.TestData;
import org.hypothesis.business.data.UserControlData;
import org.hypothesis.business.data.UserTestState;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.servlet.BroadcastService;
import org.hypothesis.utility.UIMessageUtility;
import org.vaadin.button.ui.OpenPopupButton.WindowClosedEvent;
import org.vaadin.button.ui.OpenPopupButton.WindowClosedListener;

@SuppressWarnings("serial")
public class LegacyButtonWindowClosedListener implements WindowClosedListener {

	private final PacksPresenter presenter;
	private final TestData data;

	public LegacyButtonWindowClosedListener(PacksPresenter presenter, TestData data) {
		this.presenter = presenter;
		this.data = data;
	}

	@Override
	public void windowClosed(WindowClosedEvent event) {
		data.setRunning(false);
		presenter.refreshView();
		presenter.unmaskView();

		SimpleUserDto user = SessionManager.getLoggedUser2();
		String uid = SessionManager.getMainUID();

		UserControlServiceImpl userControlService = new UserControlServiceImpl();
		UserControlData data = userControlService.ensureUserControlData(user);
		userControlService.updateUserControlDataWithSession(data, uid);

		if (!data.getSessions().isEmpty()) {
			UserTestState state = data.getSessions().get(0).getState();
			if (state != null) {
				state.setPackId(null);

				if (!"FINISH_TEST".equals(state.getEventName())) {
					state.setEventName("BREAK_TEST");
				}

				BroadcastService.broadcast(UIMessageUtility.createRefreshUserTestStateMessage(user.getId()));
			}
		}
	}

}
