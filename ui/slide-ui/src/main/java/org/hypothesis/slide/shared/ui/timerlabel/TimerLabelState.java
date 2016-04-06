/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.slide.shared.ui.timerlabel;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.label.LabelState;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
