package org.hypothesis.utility;

import org.hypothesis.ui.view.UIView;

public class ViewUtility {

	public static boolean isActiveView(UIView view) {
		return view != null && view.getUI() != null && view.getUI().getSession() != null;
	}
}
