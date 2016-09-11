/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import org.hypothesis.interfaces.Command;
import org.vaadin.jouni.animator.AnimatorProxy;
import org.vaadin.jouni.animator.AnimatorProxy.AnimationEvent;
import org.vaadin.jouni.animator.shared.AnimType;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
@PreserveOnRefresh
@Push(value = PushMode.MANUAL)
public class ProcessUI extends HypothesisUI {

	private boolean requestClose = false;

	private final CssLayout clearLayout = new CssLayout();

	public void showErrorDialog(ErrorDialog dialog) {
		dialog.show(this);
	}

	/**
	 * sets empty layout to remove old content
	 */
	public void clearContent(boolean animate, final Command nextCommand) {
		removeAllTimers();
		removeAllShortcutKeys();

		Component content = getContent();
		if (animate && content instanceof ComponentContainer) {
			AnimatorProxy animator = new AnimatorProxy();
			animator.addListener(new AnimatorProxy.AnimationListener() {
				@Override
				public void onAnimation(AnimationEvent event) {
					setContent(clearLayout);
					Command.Executor.execute(nextCommand);
				}
			});
			((ComponentContainer) content).addComponent(animator);
			animator.animate(content, AnimType.FADE_OUT).setDuration(300).setDelay(0);
		} else {
			setContent(clearLayout);
			Command.Executor.execute(nextCommand);
		}
	}

	public void setSlideContent(Component component) {
		setContent(component);

		focus();
	}

	public void requestClose() {
		requestClose = true;
		close();
	}

	@Override
	public void close() {
		if (!requestClose) {
			// log.warn("ProcessUI closing without request. Possible runtime
			// error or user closed the browser window.");
			requestClose = false;
		}

		// getSession().close(); // closes all windows in this session
		super.close();
	}

	public void setLoadingIndicatorVisible(boolean visible) {
		getPage().getJavaScript().execute(
				"var x=document.getElementsByClassName(\"v-loading-indicator\");if(x.length>0){x[0].style.zIndex=\""
						+ (visible ? 9999 : 0) + "\"}");
	}

}
