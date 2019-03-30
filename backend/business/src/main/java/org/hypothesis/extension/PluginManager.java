/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.extension;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.hypothesis.common.ValidationSets;
import org.hypothesis.configuration.AbstractConfigManager;
import org.hypothesis.event.model.ProcessEventTypes;
import org.hypothesis.interfaces.Plugin;
import org.hypothesis.interfaces.SlideComponentPlugin;
import org.hypothesis.interfaces.SlideComponentPlugin.ValidParentGroup;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PluginManager extends AbstractConfigManager {

	private static final Logger log = Logger.getLogger(PluginManager.class);

	private static final String ROOT_NAME = "hypothesis-plugin-configuration";
	public static final String PLUGIN_CONFIG_LOCATION = "pluginConfigLocation";

	private static PluginManager instance = null;

	public static PluginManager get() {
		if (null == instance) {
			instance = new PluginManager();
		}

		return instance;
	}

	private final HashSet<SlideComponentPlugin> componentPlugins = new HashSet<>();
	private final HashMap<String, SlideComponentPlugin> namespacePluginMap = new HashMap<>();
	private final HashMap<String, Set<String>> namespaceElementMap = new HashMap<>();
	private final HashSet<Class<? extends Plugin>> registeredClasses = new HashSet<>();

	protected PluginManager() {

	}

	@Override
	public void setConfigFile(File file) {
		super.setConfigFile(file);

		initialize();
	}

	private void initialize() {
		Element root = getRootElement();
		if (root != null) {
			List<Element> plugins = getPluginElements(root);

			for (Element plugin : plugins) {
				registerPluginFromElement(plugin);
			}
		}
	}

	private List<Element> getPluginElements(Element root) {

		return root.selectNodes(String.format("%s//%s", "plugins", "plugin")).stream()//
				.map(Element.class::cast)//
				.collect(toList());
	}

	private void registerPluginFromElement(Element element) {
		Element classElement = getClassElement(element);
		if (classElement != null) {
			String className = classElement.getTextTrim();
			if (isNotEmpty(className)) {
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
		} catch (Throwable e) {
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

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	protected String getRootName() {
		return ROOT_NAME;
	}
}
