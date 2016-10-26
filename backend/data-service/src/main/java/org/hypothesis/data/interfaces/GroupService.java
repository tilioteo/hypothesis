package org.hypothesis.data.interfaces;

import java.io.Serializable;
import java.util.List;

import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.User;

public interface GroupService extends Serializable {

	Group merge(Group group);

	Group add(Group group);

	void deleteAll();

	void delete(Group group);

	List<Group> findAll();

	Group find(long id);

	List<Group> findOwnerGroups(User owner);

	boolean groupNameExists(Long id, String name);

}