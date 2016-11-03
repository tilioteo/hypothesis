/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common;

import com.tilioteo.common.collections.StringSet;
import org.hypothesis.interfaces.DocumentConstants;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ValidationSets {

	// TODO replace with DTD schema
	StringSet VALID_SLIDE_ROOT_ELEMENTS = new StringSet(
			new String[] { DocumentConstants.SLIDE, DocumentConstants.SLIDE_TEMPLATE });

	StringSet VALID_PANEL_CHILDREN = new StringSet(new String[] { DocumentConstants.HORIZONTAL_LAYOUT,
			DocumentConstants.VERTICAL_LAYOUT, DocumentConstants.FORM_LAYOUT });

	StringSet VALID_VIEWPORT_CHILDREN = new StringSet(VALID_PANEL_CHILDREN, new String[] { DocumentConstants.PANEL });

	StringSet VALID_CONTAINER_CHILDREN = new StringSet(VALID_VIEWPORT_CHILDREN,
			new String[] { DocumentConstants.BUTTON, DocumentConstants.COMBOBOX, DocumentConstants.BUTTON_PANEL,
					DocumentConstants.DATE_FIELD, DocumentConstants.IMAGE, DocumentConstants.VIDEO,
					DocumentConstants.AUDIO, DocumentConstants.LABEL, DocumentConstants.SELECT_PANEL,
					DocumentConstants.TEXT_AREA, DocumentConstants.TEXT_FIELD, DocumentConstants.TIMER_LABEL });

	StringSet VALID_WINDOW_CHILDREN = VALID_PANEL_CHILDREN;

}
