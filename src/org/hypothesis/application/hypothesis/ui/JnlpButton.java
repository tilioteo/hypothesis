/**
 * 
 */
package org.hypothesis.application.hypothesis.ui;

import org.hypothesis.terminal.gwt.client.ui.VJnlpButton;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Button;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
@ClientWidget(value = VJnlpButton.class, loadStyle = LoadStyle.EAGER)
public class JnlpButton extends Button {

	private String requestUrl = null;

	public JnlpButton() {
		super();
	}

	public JnlpButton(String caption) {
		super(caption);
	}

	public JnlpButton(String caption, ClickListener listener) {
		super(caption, listener);
	}

	public void openRequest(String request) {
		this.requestUrl = request;
		requestRepaint();
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		if (requestUrl != null) {
			target.addVariable(this, "request", requestUrl);
			requestUrl = null;
		}
	}
}
