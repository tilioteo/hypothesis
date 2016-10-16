/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.extension;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hypothesis.common.ValidationSets;
import org.hypothesis.event.model.ProcessEventTypes;
import org.hypothesis.interfaces.Plugin;
import org.hypothesis.interfaces.SlideComponentPlugin;
import org.hypothesis.interfaces.SlideComponentPlugin.ValidParentGroup;
import org.hypothesis.utility.XmlUtility;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PluginManager implements Serializable {

	private static final Logger log = Logger.getLogger(PluginManager.class);

	public static final String PLUGIN_CONFIG_LOCATION = "pluginConfigLocation";

	private static PluginManager instance = null;

	private HashSet<SlideComponentPlugin> componentPlugins = new HashSet<>();
	private HashMap<String, SlideComponentPlugin> namespacePluginMap = new HashMap<>();
	private HashMap<String, Set<String>> namespaceElementMap = new HashMap<>();
	private HashSet<Class<? extends Plugin>> registeredClasses = new HashSet<>();

	protected PluginManager() {
	}

	/**
	 * Get singleton instance
	 * 
	 * @return
	 */
	public static PluginManager get() {
		if (null == instance) {
			instance = new PluginManager();
		}

		return instance;
	}

	/**
	 * Read xml configuration and initialize plugins
	 * 
	 * @param file
	 */
	public void initializeFromFile(File file) {
		if (file != null) {
			Document document = XmlUtility.readFile(file);
			if (document != null) {
				initializeFromDocument(document);
			} else {
				log.error("Cannot read plugin configuration from file " + file.getPath());
			}
		} else {
			log.error("Plugin configuration file not specified.");
		}
	}

	private void initializeFromDocument(Document document) {
		Element root = document.getRootElement();
		if ("hypothesis-plugin-configuration".equals(root.getName())) {
			List<Element> plugins = getPluginElements(root);

			for (Element plugin : plugins) {
				registerPluginFromElement(plugin);
			}
		} else {
			log.error("Not valid plugin configuration file.");
		}
	}

	@SuppressWarnings("unchecked")
	private List<Element> getPluginElements(Element root) {
		return root.selectNodes(String.format("%s//%s", "plugins", "plugin"));
	}

	private void registerPluginFromElement(Element element) {
		Element classElement = getClassElement(element);
		if (classElement != null) {
			String className = classElement.getTextTrim();
			if (StringUtils.isNotEmpty(className)) {
				registerPluginClassName(className);
			}
		}
	}

	private Element getClassElement(Element element) {
		return (Element) element.selectSingleNode("class");
	}

	/**
	 * TODO get plugin classes from configuration
	 */
	private void registerPluginClassName(String className) {
		try {
			Class<?> clazz = Class.forName(className);

			if (isPluginClass(clazz) && !isRegisteredClass(clazz)) {
				registerPluginClass(clazz);
			}
		} catch (Exception e) {
			log.error("Plugin class " + className + " cannot be found.");
		}
	}

	private boolean isPluginClass(Class<?> clazz) {
		return Plugin.class.isAssignableFrom(clazz);
	}

	private boolean isSlideComponentPluginClass(Class<?> clazz) {
		return SlideComponentPlugin.class.isAssignableFrom(clazz);
	}

	private boolean isRegisteredClass(Class<?> clazz) {
		return registeredClasses.contains(clazz);
	}

	private void registerPluginClass(Class<?> clazz) {
		if (isSlideComponentPluginClass(clazz)) {
			registerSlideComponentPluginClass(clazz);
		} else {

		}
	}

	private void registerSlideComponentPluginClass(Class<?> clazz) {
		Constructor<?> ctor;
		try {
			ctor = clazz.getConstructor();
			Object object = ctor.newInstance();

			registerSlideComponentPlugin((SlideComponentPlugin) object);

		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void registerSlideComponentPlugin(SlideComponentPlugin plugin) {
		String namespace = plugin.getNamespace();
		if (namespace != null && !"".equals(namespace.trim())) {
			if (!namespacePluginMap.containsKey(namespace)) {
				namespacePluginMap.put(namespace, plugin);

				if (namespaceElementMap.containsKey(namespace)) {
					Set<String> elements = namespaceElementMap.get(namespace);
					elements.addAll(plugin.getElements());
				} else {
					namespaceElementMap.put(namespace, plugin.getElements());
				}

				Map<String, Set<ValidParentGroup>> elementParentGroups = plugin.getElementParentGroups();
				for (String elementName : elementParentGroups.keySet()) {
					String fullElementName = namespace + org.hypothesis.interfaces.Document.NAMESPACE_SEPARATOR
							+ elementName;
					Set<ValidParentGroup> parentGroups = elementParentGroups.get(elementName);

					for (ValidParentGroup parentGroup : parentGroups) {
						switch (parentGroup) {
						case CONTAINER:
							ValidationSets.VALID_CONTAINER_CHILDREN.add(fullElementName);
							break;
						case PANEL:
							ValidationSets.VALID_PANEL_CHILDREN.add(fullElementName);
							break;
						case VIEWPORT:
							ValidationSets.VALID_VIEWPORT_CHILDREN.add(fullElementName);
							break;
						}
					}
				}

				ProcessEventTypes.registerPluginEvents(plugin.getEventTypes());

				componentPlugins.add(plugin);
				registeredClasses.add(plugin.getClass());
			} else {
				// TODO throw exception
			}
		} else {
			// TODO throw exception
		}
	}

	public SlideComponentPlugin getComponentPlugin(String namespace, String elementName) {
		SlideComponentPlugin componentPlugin = namespacePluginMap.get(namespace);

		if (componentPlugin != null) {
			Set<String> elements = namespaceElementMap.get(namespace);
			if (elements.contains(elementName)) {
				return componentPlugin;
			}
		}

		return null;
	}
}
