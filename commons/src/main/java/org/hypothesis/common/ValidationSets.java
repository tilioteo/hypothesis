/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.hypothesis.interfaces.DocumentConstants.AUDIO;
import static org.hypothesis.interfaces.DocumentConstants.BUTTON;
import static org.hypothesis.interfaces.DocumentConstants.BUTTON_PANEL;
import static org.hypothesis.interfaces.DocumentConstants.CLIENT_SIM;
import static org.hypothesis.interfaces.DocumentConstants.COMBOBOX;
import static org.hypothesis.interfaces.DocumentConstants.DATE_FIELD;
import static org.hypothesis.interfaces.DocumentConstants.FORM_LAYOUT;
import static org.hypothesis.interfaces.DocumentConstants.HORIZONTAL_LAYOUT;
import static org.hypothesis.interfaces.DocumentConstants.IMAGE;
import static org.hypothesis.interfaces.DocumentConstants.LABEL;
import static org.hypothesis.interfaces.DocumentConstants.PANEL;
import static org.hypothesis.interfaces.DocumentConstants.SELECT_PANEL;
import static org.hypothesis.interfaces.DocumentConstants.SLIDE;
import static org.hypothesis.interfaces.DocumentConstants.SLIDE_TEMPLATE;
import static org.hypothesis.interfaces.DocumentConstants.TEXT_AREA;
import static org.hypothesis.interfaces.DocumentConstants.TEXT_FIELD;
import static org.hypothesis.interfaces.DocumentConstants.TIMER_LABEL;
import static org.hypothesis.interfaces.DocumentConstants.VERTICAL_LAYOUT;
import static org.hypothesis.interfaces.DocumentConstants.VIDEO;

import java.util.Set;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public final class ValidationSets {

	// TODO replace with DTD schema
	public static final Set<String> VALID_SLIDE_ROOT_ELEMENTS = of(SLIDE, SLIDE_TEMPLATE).collect(toSet());

	public static final Set<String> VALID_PANEL_CHILDREN = of(HORIZONTAL_LAYOUT, VERTICAL_LAYOUT, FORM_LAYOUT)
			.collect(toSet());

	public static final Set<String> VALID_VIEWPORT_CHILDREN = concat(VALID_PANEL_CHILDREN.stream(), of(PANEL))
			.collect(toSet());

	public static final Set<String> VALID_CONTAINER_CHILDREN = concat(VALID_VIEWPORT_CHILDREN.stream(),
			of(BUTTON, COMBOBOX, BUTTON_PANEL, DATE_FIELD, IMAGE, VIDEO, AUDIO, LABEL, SELECT_PANEL, TEXT_AREA,
					TEXT_FIELD, TIMER_LABEL, CLIENT_SIM)).collect(toSet());

	public static final Set<String> VALID_WINDOW_CHILDREN = VALID_PANEL_CHILDREN;

}
