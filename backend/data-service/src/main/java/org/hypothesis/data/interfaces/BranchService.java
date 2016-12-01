package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Pack;

import java.io.Serializable;
import java.util.Map;

public interface BranchService extends Serializable {

	Branch findById(Long id);

	Map<String, Branch> getBranches(Pack pack, Branch branch);

}