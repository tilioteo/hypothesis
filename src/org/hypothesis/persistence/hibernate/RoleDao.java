/**
 * 
 */
package org.hypothesis.persistence.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.hypothesis.core.FieldConstants;
import org.hypothesis.entity.Role;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class RoleDao extends AbstractHibernateDao<Role, Long> {

	public Role findByNameIgnoreCase(String name) {
		List<Role> roles = findByCriteria(Restrictions.eq(FieldConstants.NAME,
				name).ignoreCase());
		if (roles.size() > 0)
			return roles.get(0);

		return null;
	}

}
