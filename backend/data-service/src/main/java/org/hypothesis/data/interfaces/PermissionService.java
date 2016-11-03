package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface PermissionService extends Serializable {

	GroupPermission addGroupPermission(GroupPermission groupPermission);

	UserPermission addUserPermission(UserPermission userPermission);

	void deleteGroupPermissions(Group group);

	void deleteUserPermissions(User user);

	void deleteUserPermissions(User user, boolean enabled);

	List<GroupPermission> findAllGroupPermissions();

	List<Pack> findAllPacks();

	List<UserPermission> findAllUserPermissions();

	Set<Pack> findUserPacks(User user, boolean excludeFinished);

	Set<Pack> findUserPacks2(User user, boolean excludeFinished);

	Set<Pack> getGroupPacks(Group group);

	Set<GroupPermission> getGroupPermissions(Group group);

	Set<GroupPermission> getGroupsPermissions(Set<Group> groups);

	Set<GroupPermission> getPackGroupPermissions(Pack pack);

	Set<UserPermission> getPackUserPermissions(Pack pack, boolean enabled);

	Set<Pack> getUserPacks(User user, Boolean enabled, Boolean excludeFinished);

	Set<UserPermission> getUserPermissions(User user);

	Set<UserPermission> getUsersPermissions(Set<User> users, boolean enabled);

	List<Pack> getPublishedPacks();

	List<Pack> getSimplePublishedPacks();

}