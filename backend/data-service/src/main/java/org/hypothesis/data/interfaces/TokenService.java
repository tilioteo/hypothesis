package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.model.User;

public interface TokenService extends Serializable {

	Token createToken(User user, Pack pack, String viewUid, boolean production);

	Token findTokenByUid(String uid);

}