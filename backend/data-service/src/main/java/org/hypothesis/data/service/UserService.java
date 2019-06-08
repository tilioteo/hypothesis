package org.hypothesis.data.service;

import java.util.Date;
import java.util.List;

import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.data.dto.UserDto;

public interface UserService {

	List<UserDto> findAll();

	List<UserDto> findOwnerUsers(long userId);

	SimpleUserDto getSimpleById(long userId);

	UserDto getById(long userId);

	SimpleUserDto findByUsernameAndPassword(String username, String password);

	UserDto findFullByUsernamePassword(String username, String password);

	UserDto findByUsername(String userName);

	boolean anotherSuperuserExists(long userid);

	void updateUsersTestingSuspendedVN(List<Long> ids, boolean suspend);

	List<SimpleUserDto> findPlannedUsersVN(Date date);

	List<UserDto> findByPasswordAkaBirthNumberVN(String password);

	UserDto save(UserDto user);

	boolean delete(SimpleUserDto user);

	boolean usernameExists(Long id, String username);

}
