/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.special.data.ShortcutConstants;

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

	private static String capitalizeFirst(String value) {
		String str = value.trim();
		return StringUtils.left(str, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	/**
	 * Parse string representation of shortcut key
	 * 
	 * @param shortcutKey
	 *            input string
	 * @return wrapper object or null if not recognized
	 */
	public static ShortcutKeys parseShortcut(String shortcutKey) {
		if (StringUtils.isNotEmpty(shortcutKey)) {
			String[] parts = shortcutKey.split("\\+");

			ArrayList<String> modifiers = new ArrayList<>();
			String key = null;

			for (String part : parts) {
				part = ShortcutUtility.capitalizeFirst(part);

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
				int[] modifierCodes = modifiers.stream().mapToInt(ShortcutConstants.MODIFIER_MAP::get).toArray();
				return new ShortcutKeys(ShortcutConstants.SHORTCUT_MAP.get(key), modifierCodes);
			}
		}

		return null;
	}

}
