/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.installlink;

import com.tilioteo.hypothesis.client.ui.VInstallLink;
import com.tilioteo.hypothesis.client.ui.VInstallLink.WindowClosedEvent;
import com.tilioteo.hypothesis.client.ui.VInstallLink.WindowClosedEventHandler;
import com.tilioteo.hypothesis.shared.ui.installlink.InstallLinkServerRpc;
import com.vaadin.client.ui.link.LinkConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.DeployJava.InstallLink.class)
public class InstallLinkConnector extends LinkConnector implements WindowClosedEventHandler {

	@Override
	protected void init() {
		super.init();
		
		getWidget().addWindowClosedEventHandler(this);
	}

    @Override
    public VInstallLink getWidget() {
        return (VInstallLink) super.getWidget();
    }

	@Override
	public void windowClosed(WindowClosedEvent event) {
		getRpcProxy(InstallLinkServerRpc.class).windowClosed();
	}

}
