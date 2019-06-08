package org.hypothesis.ws.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.data.dto.UserDto;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.service.impl.PermissionServiceImpl;
import org.hypothesis.data.service.impl.UserServiceImpl;
import org.hypothesis.ws.entity.Pack;
import org.hypothesis.ws.utility.ConversionUtility;

@WebService(serviceName = "uvnws", targetNamespace = "ws.hypothesis.cz", portName = "uvnws")
public class UVNWebService {

	@WebMethod
	public Collection<Pack> getPacks(String userName) {

		if (StringUtils.isNotBlank(userName)) {
			final UserService userService = new UserServiceImpl();
			UserDto user = userService.findByUsername(userName);
			if (user != null) {
				final PermissionService service = new PermissionServiceImpl();

				return service.findUserPacks2(user.getId(), false).stream()//
						.map(ConversionUtility::dtoToWs)//
						.collect(toList());
			}
		}

		return emptyList();
	}

}
