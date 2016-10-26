package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.model.Message;

public interface MessageService extends Serializable {

	Message findMessageByUid(String uid);

}