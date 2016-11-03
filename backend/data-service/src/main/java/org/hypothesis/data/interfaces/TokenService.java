package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.Token;
import org.hypothesis.data.model.User;

import java.io.Serializable;

public interface TokenService extends Serializable {

	Token createToken(User user, Pack pack, String viewUid, boolean production);

	Token findTokenByUid(String uid);

}