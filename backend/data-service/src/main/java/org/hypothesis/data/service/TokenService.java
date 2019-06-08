package org.hypothesis.data.service;

import org.hypothesis.data.dto.TokenDto;

public interface TokenService {

	TokenDto create(long packId, Long userId, String viewUid, boolean production);

	TokenDto findById(String id);

}
