/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import java.util.List;

import org.hypothesis.data.model.User;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface UserService extends EntityService<User, Long> {

	User merge(User user);

	User add(User user);

	boolean anotherSuperuserExists(Long id);

	void deleteAll();

	void delete(User user);

	List<User> findAll();

	List<User> findOwnerUsers(User owner);

	User findByUsername(String username);

	User findByUsernamePassword(String username, String password);

	boolean usernameExists(Long id, String username);

}