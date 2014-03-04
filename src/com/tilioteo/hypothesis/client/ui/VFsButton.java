/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import com.google.gwt.user.client.Element;
import com.vaadin.client.ui.VButton;

/**
 * @author kamil
 *
 */
public class VFsButton extends VButton {
	
	// TODO discover why the value of this field is not accessible from JSNI
	private boolean fullscreen = true;
	
	VFsButton() {
		super();
		
		registerScript();
	}
	
	public void setFullscreen(boolean value) {
		this.fullscreen = value;
	}
	
	private void registerScript() {
		registerScript(getElement());
	}
	
	private native static void registerScript(Element element) /*-{
		function getFullscreenEnabled() {
			return this.@com.tilioteo.hypothesis.client.ui.VFsButton::fullscreen;
		};
		
		(function () {
			element.addEventListener("click", function () {
				var enabled = true; // TODO get value of fullscreen field 
				if (enabled) {
					var docElm = $doc.documentElement;
					if (docElm.requestFullscreen) {
						docElm.requestFullscreen();
					} else if (docElm.msRequestFullscreen) {
						docElm.msRequestFullscreen();
					} else if (docElm.mozRequestFullScreen) {
						docElm.mozRequestFullScreen();
					} else if (docElm.webkitRequestFullScreen) {
						docElm.webkitRequestFullScreen();
					}
				}
			}, false);
		})();
	}-*/;

}
