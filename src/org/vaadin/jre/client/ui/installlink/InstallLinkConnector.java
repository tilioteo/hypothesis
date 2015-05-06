/**
 * 
 */
package org.vaadin.jre.client.ui.installlink;

import org.vaadin.jre.client.ui.VInstallLink;
import org.vaadin.jre.client.ui.VInstallLink.WindowClosedEvent;
import org.vaadin.jre.client.ui.VInstallLink.WindowClosedEventHandler;
import org.vaadin.jre.shared.ui.installlink.InstallLinkServerRpc;

import com.vaadin.client.ui.link.LinkConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.jre.ui.DeployJava.InstallLink.class)
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
