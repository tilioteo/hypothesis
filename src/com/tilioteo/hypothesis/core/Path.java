/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Map;

import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.processing.Formula;
import com.tilioteo.hypothesis.processing.DefaultPath;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Path extends DefaultPath {

	private Formula abstractBaseFormula;

	public Formula getAbstractBaseFormula() {
		return abstractBaseFormula;
	}

	public boolean isValid(Map<Long, Map<Integer, ExchangeVariable>> outputs) {
		if (abstractBaseFormula != null) {
			return abstractBaseFormula.evaluate(outputs);
		}
		return false;
	}

	public void setAbstractBaseFormula(Formula abstractBaseFormula) {
		this.abstractBaseFormula = abstractBaseFormula;
	}
}
