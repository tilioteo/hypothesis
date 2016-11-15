/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hypothesis.interfaces.DocumentConstants;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ValidationSets {

	// TODO replace with DTD schema
	List<String> VALID_SLIDE_ROOT_ELEMENTS = Arrays.asList(DocumentConstants.SLIDE, DocumentConstants.SLIDE_TEMPLATE);

	List<String> VALID_PANEL_CHILDREN = Arrays.asList(DocumentConstants.HORIZONTAL_LAYOUT,
			DocumentConstants.VERTICAL_LAYOUT, DocumentConstants.FORM_LAYOUT);

	List<String> VALID_VIEWPORT_CHILDREN = Stream
			.concat(VALID_PANEL_CHILDREN.stream(), Arrays.asList(DocumentConstants.PANEL).stream())
			.collect(Collectors.toList());

	List<String> VALID_CONTAINER_CHILDREN = Stream.concat(VALID_VIEWPORT_CHILDREN.stream(),
			Arrays.asList(DocumentConstants.BUTTON, DocumentConstants.COMBOBOX, DocumentConstants.BUTTON_PANEL,
					DocumentConstants.DATE_FIELD, DocumentConstants.IMAGE, DocumentConstants.VIDEO,
					DocumentConstants.AUDIO, DocumentConstants.LABEL, DocumentConstants.SELECT_PANEL,
					DocumentConstants.TEXT_AREA, DocumentConstants.TEXT_FIELD, DocumentConstants.TIMER_LABEL).stream())
			.collect(Collectors.toList());

	List<String> VALID_WINDOW_CHILDREN = VALID_PANEL_CHILDREN;

}
