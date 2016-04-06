/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.server;

import java.util.HashMap;

import org.hibernate.Session;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SessionMap extends HashMap<Thread, Session> {

}
