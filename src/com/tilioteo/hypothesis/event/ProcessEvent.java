/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.Date;

import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public interface ProcessEvent {
	
	String getName();
	
	Date getTimestamp();
	
	ErrorHandler getErrorHandler();

}
