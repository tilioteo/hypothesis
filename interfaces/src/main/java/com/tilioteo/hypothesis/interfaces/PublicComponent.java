/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import com.tilioteo.hypothesis.annotations.Public;
import com.tilioteo.hypothesis.annotations.PublicType;

/**
 * @author kamil
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
