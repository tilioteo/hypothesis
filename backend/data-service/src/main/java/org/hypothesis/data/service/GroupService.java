package org.hypothesis.data.service;

import java.util.List;

import org.hypothesis.data.dto.GroupDto;

public interface GroupService {

	GroupDto getById(long groupId);

	List<GroupDto> findAll();

	List<GroupDto> findOwnerGroups(Long userId);

	GroupDto save(GroupDto group);

	boolean delete(GroupDto group);

	boolean groupNameExists(Long id, String name);

}
