/**
 * 
 */
package com.tilioteo.hypothesis.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.tilioteo.hypothesis.common.FieldConstants;
import com.tilioteo.hypothesis.entity.Role;

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
