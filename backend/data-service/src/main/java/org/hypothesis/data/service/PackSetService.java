package org.hypothesis.data.service;

import java.util.List;

import org.hypothesis.data.dto.PackSetDto;

public interface PackSetService {

	List<PackSetDto> findAll();

	PackSetDto save(PackSetDto packSet);

}
