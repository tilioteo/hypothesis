package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.*;

import java.io.Serializable;

public interface PersistenceService extends Serializable {

	Pack merge(Pack entity);

	Branch merge(Branch entity);

	Task merge(Task entity);

	Slide merge(Slide entity);

	SimpleTest merge(SimpleTest entity);

}