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
	public boolean isEnabled();

	@Public
	public void setEnabled(boolean enabled);

	@Public
	public boolean isVisible();

	@Public
	public void setVisible(boolean visible);

	@Public
	public boolean isReadOnly();

	@Public
	public void setReadOnly(boolean readOnly);

	@Public
	public String getCaption();

	@Public
	public void setCaption(String caption);

	@Public
	public String getDescription();

	@Public
	public void setDescription(String description);

	@Public
	public float getWidth();

	@Public
	public void setWidth(String width);

	@Public
	public float getHeight();

	@Public
	public void setHeight(String height);
}
