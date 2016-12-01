package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.Message;

import java.io.Serializable;

public interface MessageService extends Serializable {

	Message findMessageByUid(String uid);

}