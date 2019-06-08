package org.hypothesis.data.service;

import org.hypothesis.data.dto.MessageDto;

public interface MessageService {

	MessageDto findById(String id);

}
