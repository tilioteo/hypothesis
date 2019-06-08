package org.hypothesis.data.service;

import java.util.List;

import org.hypothesis.data.dto.PackDto;

public interface PackService {

	PackDto getById(long id);

	PackDto getById(long id, boolean deep);
	
	List<PackDto> findAll();

}
