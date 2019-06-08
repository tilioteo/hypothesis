package org.hypothesis.utility;

import org.hypothesis.data.interfaces.HasId;

public class ObjectUtility {

	public static <ID> ID getId(HasId<ID> hasId) {
		return hasId != null ? hasId.getId() : null;
	}

}
