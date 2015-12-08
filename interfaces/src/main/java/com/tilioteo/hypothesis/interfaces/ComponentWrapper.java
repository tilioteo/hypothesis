/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
public interface ComponentWrapper extends Serializable {

	public Component getComponent();

	public Alignment getAlignment();

}
