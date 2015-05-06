/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.processing;

import com.tilioteo.hypothesis.client.ui.VProcessingHanoi;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.plugin.processing.ui.ProcessingHanoi.class)
public class ProcessingHanoiConnector extends AbstractComponentConnector {

	@Override
	public VProcessingHanoi getWidget() {
		// TODO Auto-generated method stub
		return (VProcessingHanoi)super.getWidget();
	}

}
