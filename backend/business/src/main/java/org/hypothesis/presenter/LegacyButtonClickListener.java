package org.hypothesis.presenter;

import org.hypothesis.business.data.TestData;
import org.hypothesis.data.dto.TokenDto;
import org.hypothesis.interfaces.PacksPresenter;
import org.hypothesis.utility.UrlUtility;
import org.vaadin.button.ui.OpenPopupButton;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class LegacyButtonClickListener implements ClickListener {
	private final OpenPopupButton button;
	private final PacksPresenter presenter;
	private final TestData data;

	public LegacyButtonClickListener(OpenPopupButton button, PacksPresenter presenter, TestData data) {
		this.button = button;
		this.presenter = presenter;
		this.data = data;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (!presenter.isTestRunning()) {
			TokenDto token = presenter.createToken(data.getPack());

			if (token != null) {
				presenter.maskView();
				button.setUrl(UrlUtility.constructStartUrl(token.getId()));
				data.setRunning(true);
			}
		}
	}

}
