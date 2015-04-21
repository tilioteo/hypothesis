/**
 * 
 */
package com.tilioteo.hypothesis.intercom;

import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class HttpSessionSet extends HashSet<HttpSession> {
	
	public void purge() {
		for (Iterator<HttpSession> iterator = super.iterator(); iterator.hasNext();) {
			HttpSession session = iterator.next();;
			try {
				session.getCreationTime();
			} catch (IllegalStateException e) {
				// session is invalid
				remove(session);
			}
		}
	}

	@Override
	public Iterator<HttpSession> iterator() {
		purge();
		return super.iterator();
	}

	@Override
	public int size() {
		purge();
		return super.size();
	}

	@Override
	public boolean isEmpty() {
		purge();
		return super.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		purge();
		return super.contains(o);
	}

	@Override
	public boolean add(HttpSession e) {
		purge();
		return super.add(e);
	}
	
	

}
