package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.model.BranchOutput;

public interface OutputService extends Serializable {

	void saveBranchOutput(BranchOutput branchOutput);

}