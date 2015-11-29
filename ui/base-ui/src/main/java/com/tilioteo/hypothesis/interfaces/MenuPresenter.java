/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
public interface MenuPresenter extends Serializable {

	public void attach();

	public void detach();

	public Component buildContent();

}
