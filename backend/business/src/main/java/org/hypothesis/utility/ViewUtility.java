/**
 * 
 */
package org.hypothesis.utility;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import org.hypothesis.annotations.RolesAllowed;
import org.hypothesis.annotations.Title;
import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.User;
import org.hypothesis.ui.MainUI;

import javax.enterprise.inject.spi.Bean;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * @author kamil
 *
 */
public final class ViewUtility {

	private ViewUtility() {
	}

	public static final Comparator<Bean<?>> titleIndexComparator = (o1,
			o2) -> BeanUtility.getAnnotation(o1, Title.class).index()
					- BeanUtility.getAnnotation(o2, Title.class).index();

	public static final Predicate<Bean<?>> filterCDIViewsForMainUI = t -> BeanUtility.isAnnotated(t, CDIView.class)
			&& BeanUtility.isAnnotated(t, Title.class) && BeanUtility.isAnnotated(t, RolesAllowed.class)
			&& Arrays.asList(BeanUtility.getAnnotation(t, CDIView.class).uis()).contains(MainUI.class);

	@SuppressWarnings("unchecked")
	public static final Predicate<Bean<?>> filterByRoles = t -> isUserViewAllowed((Class<? extends View>) t.getBeanClass());

	public static boolean isUserViewAllowed(Class<? extends View> viewClass) {
		User user = SessionManager.getLoggedUser();
		RolesAllowed rolesAllowed = viewClass.getAnnotation(RolesAllowed.class);

		return user != null && rolesAllowed != null
				&& RoleUtility.isAnyRoleAllowed(Arrays.asList(rolesAllowed.value()), user.getRoles());
	}
	
	public static String getViewPathFromCDIAnnotation(Class<? extends View> viewClass) {
		if (viewClass != null) {
			CDIView cdiView = viewClass.getAnnotation(CDIView.class);
			if (cdiView != null) {
				return cdiView.value();
			}
		}
		
		return null;
	}

}
