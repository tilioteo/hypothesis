/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import org.hypothesis.interfaces.ExchangeVariable;

import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
