/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.configuration;

import org.apache.log4j.Logger;
import org.dom4j.Element;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ConfigManager extends AbstractConfigManager {

	private static final Logger log = Logger.getLogger(ConfigManager.class);

	private static final String ROOT_NAME = "hypothesis-configuration";
	public static final String CONFIG_LOCATION = "configLocation";

	private static ConfigManager instance = null;

	public static ConfigManager get() {
		if (null == instance) {
			instance = new ConfigManager();
		}

		return instance;
	}

	protected ConfigManager() {

	}
	
	public String getValue(String key) {
		Element root = getRootElement();
		if (root != null) {
			Element keyElement = (Element) root.selectSingleNode(key);
			if (keyElement != null) {
				return keyElement.getText();
			} else {
				getLogger().error(String.format("Config key %s not found.", key));
			}
		}
		return null;
	}

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	protected String getRootName() {
		return ROOT_NAME;
	}

}
