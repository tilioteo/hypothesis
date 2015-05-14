/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.io.Serializable;
import java.util.Date;

import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public interface ProcessEvent extends Serializable {
	
	String getName();
	
	Date getTimestamp();
	
	ErrorHandler getErrorHandler();

}
