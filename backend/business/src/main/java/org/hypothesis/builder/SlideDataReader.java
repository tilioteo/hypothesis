/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hypothesis.data.DocumentReader;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface SlideDataReader extends Serializable {

	List<String> getOutputValues(String text, DocumentReader reader);
	
	List<String> getScores(String text, DocumentReader reader);

	FieldWrapper getFields(String text, DocumentReader reader);

	interface FieldWrapper extends Serializable {

		Map<String, String> getFieldCaptionMap();

		Map<String, String> getFieldValueMap();

		Map<String, Map<String, String>> getFieldValueCaptionMap();
	}

}
