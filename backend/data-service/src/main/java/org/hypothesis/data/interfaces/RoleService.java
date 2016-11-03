package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.Role;

import java.io.Serializable;
import java.util.List;

public interface RoleService extends Serializable {

	Role add(Role role);

	void delete(Role role);

	void deleteAll();

	List<Role> findAll();

	List<String> findAllRoleNames();

	Role findRoleByName(String roleName);

}