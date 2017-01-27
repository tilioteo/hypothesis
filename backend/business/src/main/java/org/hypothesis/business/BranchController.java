package org.hypothesis.business;

import java.util.Map;

import org.hypothesis.builder.Controller;
import org.hypothesis.data.model.Slide;
import org.hypothesis.evaluation.AbstractBasePath;
import org.hypothesis.interfaces.ExchangeVariable;

public interface BranchController extends Controller {

	void addPath(AbstractBasePath path);

	void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues);

	String getNextBranchKey();

	String getNextKey();

}