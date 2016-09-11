/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import org.hypothesis.annotations.Public;
import org.hypothesis.annotations.PublicType;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@PublicType
public interface PublicComponent {

	@Public
	boolean isEnabled();

	@Public
	void setEnabled(boolean enabled);

	@Public
	boolean isVisible();

	@Public
	void setVisible(boolean visible);

	@Public
	boolean isReadOnly();

	@Public
	void setReadOnly(boolean readOnly);

	@Public
	String getCaption();

	@Public
	void setCaption(String caption);

	@Public
	String getDescription();

	@Public
	void setDescription(String description);

	@Public
	float getWidth();

	@Public
	void setWidth(String width);

	@Public
	float getHeight();

	@Public
	void setHeight(String height);
}
