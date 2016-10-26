package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.BranchMap;
import org.hypothesis.data.model.Pack;

public interface BranchService extends Serializable {

	Branch findById(Long id);

	BranchMap getBranchMap(Pack pack, Branch branch);

}