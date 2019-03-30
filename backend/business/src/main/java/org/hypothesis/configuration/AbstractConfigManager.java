/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.configuration;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hypothesis.utility.XmlUtility;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractConfigManager implements Serializable {

	private File configFile;

	protected abstract Logger getLogger();

	public void setConfigFile(File file) {
		this.configFile = file;
	}

	protected abstract String getRootName();

	protected Document getDocumentFromFile(File file) {
		if (file != null) {
			Document document = XmlUtility.readFile(file);
			if (document != null) {
				return document;
			} else {
				getLogger().error("Cannot read configuration from file " + file.getPath());
			}
		} else {
			getLogger().error("Configuration file not specified.");
		}
		return null;
	}

	private Element getRootElementFromDocument(Document document) {
		String rootName = getRootName();
		if (rootName != null) {
			Element root = document.getRootElement();
			if (root.getName().equals(rootName)) {
				return root;
			} else {
				getLogger().error(String.format("Not valid configuration file. Root element %s not found.", rootName));
			}
		} else {
			getLogger().error("Configuration root element name not specified.");
		}
		return null;
	}

	protected Element getRootElement() {
		return getRootElementFromDocument(getDocumentFromFile(configFile));
	}

}
