/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.eventbus;

import org.hypothesis.business.SessionManager;

import java.io.Serializable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
public interface HasProcessEventBus extends Serializable {

    default ProcessEventBus getBus() {
        return SessionManager.getProcessEventBus();
    }

}
