package org.hypothesis.data.interfaces;

import java.io.Serializable;
import java.util.List;

import org.hypothesis.data.model.Role;

public interface RoleService extends Serializable {

	Role add(Role role);

	void delete(Role role);

	void deleteAll();

	List<Role> findAll();

	List<String> findAllRoleNames();

	Role findRoleByName(String roleName);

}