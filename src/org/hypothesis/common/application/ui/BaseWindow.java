/**
 * 
 */
package org.hypothesis.common.application.ui;

import com.vaadin.ui.Window;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Base window class with better notification support
 * 
 */
@SuppressWarnings("serial")
public class BaseWindow extends Window {

	public void showError(String caption) {
		showNotification(caption, Notification.TYPE_ERROR_MESSAGE);
	}

	public void showError(String caption, String description) {
		showNotification(caption, description, Notification.TYPE_ERROR_MESSAGE);
	}

	public void showInformation(String caption) {
		showNotification(caption, Notification.TYPE_HUMANIZED_MESSAGE);
	}

	public void showInformation(String caption, String description) {
		showNotification(caption, description,
				Notification.TYPE_HUMANIZED_MESSAGE);
	}

	public void showTrayInfo(String caption) {
		showNotification(caption, Notification.TYPE_TRAY_NOTIFICATION);
	}

	public void showTrayInfo(String caption, String description) {
		showNotification(caption, description,
				Notification.TYPE_TRAY_NOTIFICATION);
	}

	public void showWarning(String caption) {
		showNotification(caption, Notification.TYPE_WARNING_MESSAGE);
	}

	public void showWarning(String caption, String description) {
		showNotification(caption, description,
				Notification.TYPE_WARNING_MESSAGE);
	}
}
