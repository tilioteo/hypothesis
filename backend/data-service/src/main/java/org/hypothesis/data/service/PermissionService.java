package org.hypothesis.data.service;

import java.util.List;
import java.util.Set;

import org.hypothesis.data.dto.PackDto;

public interface PermissionService {

	boolean userCanAccess(Long userId, long packId);

	void disableForVN(long userId, long packId);

	List<PackDto> getUserPacksVN(long userId);

	List<PackDto> getPublishedPacks();

	List<PackDto> findUserPacks2(long userid, boolean excludeFinished);

	List<PackDto> getGroupPacks(long groupId);

	List<PackDto> getGroupPacks(Set<Long> groupIds);

	void setGroupPermissions(long groupId, Set<PackDto> enabledPacks);

	void setUserPermissions(long userId, Set<PackDto> enabledPacks, Set<PackDto> disabledPacks);

	List<PackDto> getUserPacks(long userId, boolean enabled);

}
