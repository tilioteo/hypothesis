/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.data;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class UserSession {

	private final String uid;
	private String position;
	private String address;

	private UserTestState state;

	public UserSession(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public UserTestState getState() {
		return state;
	}

	public void setState(UserTestState state) {
		this.state = state;
	}

}
