/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.browserappletframe;

import com.tilioteo.hypothesis.client.ui.VBrowserAppletFrame;
import com.tilioteo.hypothesis.client.ui.VBrowserAppletFrame.ReadyCheckEvent;
import com.tilioteo.hypothesis.client.ui.VBrowserAppletFrame.ReadyCheckEventHandler;
import com.tilioteo.hypothesis.shared.ui.browserappletframe.BrowserAppletFrameClientRpc;
import com.tilioteo.hypothesis.shared.ui.browserappletframe.BrowserAppletFrameServerRpc;
import com.tilioteo.hypothesis.shared.ui.browserappletframe.BrowserAppletFrameState;
import com.vaadin.client.ui.browserframe.BrowserFrameConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.BrowserAppletFrame.class)
public class BrowserAppletFrameConnector extends BrowserFrameConnector implements ReadyCheckEventHandler {

    @Override
    protected void init() {
        super.init();
        
        getWidget().addReadyCheckEventHandler(this);
		
        registerRpc(BrowserAppletFrameClientRpc.class, new BrowserAppletFrameClientRpc() {
			@Override
			public void startBrowser(String token) {
				getWidget().startBrowser(token);
			}

			@Override
			public void checkReadyState() {
				getWidget().checkReady();
			}
		});
    }

    @Override
    public VBrowserAppletFrame getWidget() {
        return (VBrowserAppletFrame) super.getWidget();
    }

    @Override
    public BrowserAppletFrameState getState() {
        return (BrowserAppletFrameState) super.getState();
    }
    
	@Override
	public void readyChecked(ReadyCheckEvent event) {
    	getRpcProxy(BrowserAppletFrameServerRpc.class).readyChecked(event.getReadyState());
	}

}
