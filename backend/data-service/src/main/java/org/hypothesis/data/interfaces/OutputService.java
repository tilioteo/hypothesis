package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.BranchOutput;

import java.io.Serializable;

public interface OutputService extends Serializable {

	void saveBranchOutput(BranchOutput branchOutput);

}