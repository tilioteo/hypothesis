/**
 * 
 */
package com.tilioteo.hypothesis.shared.ui.timerlabel;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.label.LabelState;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class TimerLabelState extends LabelState {
    {
        primaryStyleName = "v-timerlabel";
    }

	public String timeFormat = "HH:mm:ss.S";
	public Connector timer = null;
	public int updateInterval = 100;

}
