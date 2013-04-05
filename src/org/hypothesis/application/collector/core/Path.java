/**
 * 
 */
package org.hypothesis.application.collector.core;

import org.hypothesis.common.PairList;
import org.hypothesis.entity.Slide;

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
