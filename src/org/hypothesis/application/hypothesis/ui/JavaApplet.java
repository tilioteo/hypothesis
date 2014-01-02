/**
 * 
 */
package org.hypothesis.application.hypothesis.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hypothesis.terminal.gwt.client.ui.VJavaApplet;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

/**
 * @author morong
 * 
 */
@SuppressWarnings("serial")
@ClientWidget(VJavaApplet.class)
public class JavaApplet extends AbstractComponent {

	private String code;

	private String archive;

	/**
	 * Hash of java arguments.
	 */
	private final Map<String, String> arguments = new HashMap<String, String>();

	public JavaApplet() {

	}

	public JavaApplet(String archive, String code) {
		this();
		setArchive(archive);
		setCode(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		requestRepaint();
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(String archive) {
		this.archive = archive;
		requestRepaint();
	}

	public String getArgument(String key) {
		return arguments.get(key);
	}

	public void setArgument(String key, String value) {
		arguments.put(key, value);
		requestRepaint();
	}

	public void removeArgument(String key) {
		arguments.remove(key);
		requestRepaint();
	}

	public Iterator<String> getArgumentNames() {
		return arguments.keySet().iterator();
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {

		if (code != null && !"".equals(code)) {
			target.addAttribute("code", code);
		}

		if (archive != null && !"".equals(archive)) {
			target.addAttribute("archive", archive);
		}

		// Arguments
		for (final Iterator<String> i = getArgumentNames(); i.hasNext();) {
			target.startTag("java_argument");
			final String key = i.next();
			target.addAttribute("name", key);
			target.addAttribute("value", getArgument(key));
			target.endTag("java_argument");
		}
	}

}
