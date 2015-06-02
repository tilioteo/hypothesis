/**
 * 
 */
package org.vaadin.button.client.ui;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.ui.VButton;

/**
 * @author kamil
 *
 */
public class VFullscreenButton extends VButton {
	
	// TODO discover why the value of this field is not accessible from JSNI
	private boolean fullscreen = true;
	
	VFullscreenButton() {
		super();
		
		registerScript();
	}
	
	public void setFullscreen(boolean value) {
		this.fullscreen = value;
	}
	
	private void registerScript() {
		registerScript(this, getElement());
	}
	
	private native static void registerScript(VFullscreenButton button, Element element) /*-{
		(function () {
			element.addEventListener("click", function () {
				var enabled = button.@org.vaadin.button.client.ui.VFullscreenButton::fullscreen;
				if (enabled) {
					var isChrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
					var el = isChrome ? $doc.documentElement : $doc.getElementsByTagName("body")[0];
					
					if (el.requestFullscreen) {
						el.requestFullscreen();
					} else if (el.requestFullScreen) {
						el.requestFullScreen();
					} else if (el.msRequestFullscreen) {
						el.msRequestFullscreen();
					} else if (el.msRequestFullScreen) {
						el.msRequestFullScreen();
					} else if (el.mozRequestFullscreen) {
						el.mozRequestFullscreen();
					} else if (el.mozRequestFullScreen) {
						el.mozRequestFullScreen();
					} else if (el.webkitRequestFullscreen) {
						el.webkitRequestFullscreen();
					} else if (el.webkitRequestFullScreen) {
						el.webkitRequestFullScreen();
					}
				}
			}, false);
		})();
	}-*/;

}
