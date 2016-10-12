/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;
import java.util.ArrayList;

import org.vaadin.special.data.ShortcutConstants;

import com.tilioteo.common.Strings;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ShortcutUtility {

	private ShortcutUtility() {
	}

	/**
	 * 
	 * Wrapper class for shortcut key
	 */
	@SuppressWarnings("serial")
	public static class ShortcutKeys implements Serializable {

		private int keyCode;
		private int[] modifiers;

		/**
		 * 
		 * @param keyCode
		 * @param modifiers
		 *            set of modifier codes (Ctrl, Alt...)
		 */
		public ShortcutKeys(int keyCode, int... modifiers) {
			this.keyCode = keyCode;
			this.modifiers = modifiers;
		}

		public int getKeyCode() {
			return keyCode;
		}

		public int[] getModifiers() {
			return modifiers;
		}
	}

	/**
	 * Parse string representation of shortcut key
	 * 
	 * @param shortcutKey
	 *            input string
	 * @return wrapper object or null if not recognized
	 */
	public static ShortcutKeys parseShortcut(String shortcutKey) {
		if (!Strings.isNullOrEmpty(shortcutKey)) {
			String[] parts = shortcutKey.split("\\+");

			ArrayList<String> modifiers = new ArrayList<>();
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
