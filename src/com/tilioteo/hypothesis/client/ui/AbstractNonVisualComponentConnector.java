/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import com.vaadin.client.ui.AbstractComponentConnector;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractNonVisualComponentConnector extends AbstractComponentConnector {

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

}
