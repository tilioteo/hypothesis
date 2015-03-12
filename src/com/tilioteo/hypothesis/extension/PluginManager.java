/**
 * 
 */
package com.tilioteo.hypothesis.extension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.event.ProcessEventTypes;
import com.tilioteo.hypothesis.extension.SlideComponentPlugin.ValidParentGroup;
import com.tilioteo.hypothesis.plugin.map.MapPlugin;
import com.tilioteo.hypothesis.plugin.processing.ProcessingPlugin;

/**
 * @author kamil
 *
 */
public class PluginManager {
	
	// TODO make configuration
	public static final Class<?>[] PLUGINS = new Class<?>[] {
		MapPlugin.class,
		ProcessingPlugin.class
	};
	
	private static PluginManager instance = null;
	
	public static PluginManager get() {
		if (null == instance) {
			instance = new PluginManager();
		}
		
		return instance;
	}
	
	private HashSet<SlideComponentPlugin> componentPlugins = new HashSet<SlideComponentPlugin>();
	private HashMap<String, SlideComponentPlugin> namespacePluginMap = new HashMap<String, SlideComponentPlugin>();
	private HashMap<String, Set<String>> namespaceElementMap = new HashMap<String, Set<String>>();
	private HashSet<Class<? extends Plugin>> registeredClasses = new HashSet<Class<? extends Plugin>>();
	
	protected PluginManager() {
		
	}
	
	/**
	 * TODO get plugin classes from configuration
	 */
	public void registerPlugins() {
		for (Class<?> clazz : PLUGINS) {
			if (isPluginClass(clazz) && !isRegisteredClass(clazz)) {
				registerPluginClass(clazz);
			}
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

		} catch (NoSuchMethodException | SecurityException |
				InstantiationException | IllegalAccessException	|
				IllegalArgumentException | InvocationTargetException e) {
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
					Set<ValidParentGroup> parentGroups = elementParentGroups.get(elementName);
					
					for (ValidParentGroup parentGroup : parentGroups) {
						switch (parentGroup) {
						case CONTAINER: SlideXmlConstants.VALID_CONTAINER_ELEMENTS.add(elementName); break;
						case PANEL : SlideXmlConstants.VALID_PANEL_ELEMENTS.add(elementName); break;
						case VIEWPORT : SlideXmlConstants.VALID_VIEWPORT_ELEMENTS.add(elementName); break;
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
