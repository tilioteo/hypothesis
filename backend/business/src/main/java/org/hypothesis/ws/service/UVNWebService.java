package org.hypothesis.ws.service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.UserService;
import org.hypothesis.ws.entity.Pack;
import org.hypothesis.ws.utility.ConversionUtility;

@WebService(serviceName = "uvnws", targetNamespace = "ws.hypothesis.cz", portName = "uvnPort")
public class UVNWebService {

	@WebMethod
	public Collection<Pack> getPacks(String userName) {

		if (StringUtils.isNotBlank(userName)) {
			UserService userService = UserService.newInstance();
			User user = userService.findByUsername(userName);
			if (user != null) {
				PermissionService service = PermissionService.newInstance();

				return service.findUserPacks2(user, false).stream()//
						.map(ConversionUtility::entityToWs)//
						.collect(Collectors.toList());
			}
		}

		return Collections.emptyList();
	}

}
