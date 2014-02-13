/**
 * 
 */
package com.tilioteo.hypothesis.core;

import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.processing.AbstractBaseFormula;
import com.tilioteo.hypothesis.processing.DefaultPath;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Path extends DefaultPath {

	private AbstractBaseFormula abstractBaseFormula;

	public AbstractBaseFormula getAbstractBaseFormula() {
		return abstractBaseFormula;
	}

	public boolean isValid(PairList<Slide, Object> results) {
		if (abstractBaseFormula != null) {
			return abstractBaseFormula.evaluate(results);
		}
		return false;
	}

	public void setAbstractBaseFormula(AbstractBaseFormula abstractBaseFormula) {
		this.abstractBaseFormula = abstractBaseFormula;
	}
}
