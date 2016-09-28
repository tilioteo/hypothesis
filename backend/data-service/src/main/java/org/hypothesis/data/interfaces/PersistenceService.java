package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;

public interface PersistenceService extends Serializable {

	Pack merge(Pack entity);

	Branch merge(Branch entity);

	Task merge(Task entity);

	Slide merge(Slide entity);

	SimpleTest merge(SimpleTest entity);

}