/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.interfaces;

import java.util.List;

import org.hypothesis.data.model.Group;
import org.hypothesis.data.model.User;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface GroupService extends EntityService<Group, Long> {

	Group merge(Group group);

	Group add(Group group);

	void deleteAll();

	void delete(Group group);

	List<Group> findAll();

	List<Group> findOwnerGroups(User owner);

	boolean groupNameExists(Long id, String name);

}