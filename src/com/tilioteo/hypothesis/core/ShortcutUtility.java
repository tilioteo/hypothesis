/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.io.Serializable;
import java.util.ArrayList;

import org.vaadin.special.data.ShortcutConstants;

import com.tilioteo.hypothesis.common.Strings;

/**
 * @author kamil
 *
 */
public class ShortcutUtility {
	
	@SuppressWarnings("serial")
	public static class ShortcutKeys implements Serializable {
		
		public ShortcutKeys(int keyCode, int... modifiers) {
			this.keyCode = keyCode;
			this.modifiers = modifiers;
		}
		
		private int keyCode;
		private int[] modifiers;
		
		public int getKeyCode() {
			return keyCode;
		}
		
		public int[] getModifiers() {
			return modifiers;
		}
		
	}
	
	public static ShortcutKeys parseShortcut(String shortcutKey) {
		if (!Strings.isNullOrEmpty(shortcutKey)) {
			String[] parts = shortcutKey.split("\\+");
			
			ArrayList<String> modifiers = new ArrayList<String>();
			String key = null;
			
			for (String part : parts) {
				part = part.trim().toLowerCase();
				part = part.substring(0, 1).toUpperCase() + part.substring(1);
				
				if (ShortcutConstants.MODIFIER_MAP.containsKey(part)) {
					modifiers.add(part);
				} else if (ShortcutConstants.SHORTCUT_MAP.containsKey(part)) {
					if (null != key) {
						key = null;
						break;
					} else {
						key = part;
					}
				}
			}
			
			if (null != key) {
				int[] modifierCodes = new int[modifiers.size()];
				int i = 0;
				
				for (String modifier : modifiers) {
					modifierCodes[i++] = ShortcutConstants.MODIFIER_MAP.get(modifier);
				}
				
				return new ShortcutKeys(ShortcutConstants.SHORTCUT_MAP.get(key), modifierCodes);
			}
		}
		return null;
	}

}
