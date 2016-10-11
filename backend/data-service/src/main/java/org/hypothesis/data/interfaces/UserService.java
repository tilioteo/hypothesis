package org.hypothesis.data.interfaces;

import java.io.Serializable;
import java.util.List;

import org.hypothesis.data.model.User;

public interface UserService extends Serializable {

	User merge(User user);

	User add(User user);

	boolean anotherSuperuserExists(Long id);

	void deleteAll();

	void delete(User user);

	List<User> findAll();

	List<User> findOwnerUsers(User owner);

	User find(long id);

	User findByUsername(String username);

	User findByUsernamePassword(String username, String password);

	boolean usernameExists(Long id, String username);

}