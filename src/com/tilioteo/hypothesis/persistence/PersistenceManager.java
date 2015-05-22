/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.servlet.HibernateUtil;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PersistenceManager implements Serializable {
	
	private static Logger log = Logger.getLogger(PersistenceManager.class);
	
	//private PackDao packDao;
	//private BranchDao branchDao;
	//private TaskDao taskDao;
	//private SimpleTestDao;
	//private UserDao userDao;
	//private GroupDao groupDao;

	public static PersistenceManager newInstance() {
		return new PersistenceManager();
	}
	
	protected PersistenceManager() {
		//packDao = new PackDao();
		//branchDao = new BranchDao();
		//taskDao = new TaskDao();
		
		//userDao = new UserDao();
		//groupDao = new GroupDao();
	}
	
	public Pack merge(Pack entity) {
		log.debug(String.format("merge(pack id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				//HibernateUtil.getSession().clear();
				Pack pack = (Pack)HibernateUtil.getSession().merge(entity);
				Hibernate.initialize(pack.getBranches());
				HibernateUtil.commitTransaction();
				
				return pack;
			} catch (Throwable e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}
	
	public Branch merge(Branch entity) {
		log.debug(String.format("merge(branch id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				//HibernateUtil.getSession().clear();
				Branch branch = (Branch) HibernateUtil.getSession().merge(entity);
				Hibernate.initialize(branch.getTasks());
				HibernateUtil.commitTransaction();

				return branch;
			} catch (Throwable e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}

	public Task merge(Task entity) {
		log.debug(String.format("merge(task id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				//HibernateUtil.getSession().clear();
				Task task = (Task) HibernateUtil.getSession().merge(entity);
				Hibernate.initialize(task.getSlides());
				HibernateUtil.commitTransaction();

				return task;
			} catch (Throwable e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}

	public SimpleTest merge(SimpleTest entity) {
		log.debug(String.format("merge(test id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				//HibernateUtil.getSession().clear();
				SimpleTest test = (SimpleTest) HibernateUtil.getSession().merge(entity);
				HibernateUtil.commitTransaction();

				return test;
			} catch (Throwable e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}
	
	/*public User merge(User entity) {
		log.debug(String.format("merge(user id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				HibernateUtil.getSession().clear();
				User user = (User) HibernateUtil.getSession().merge(entity);
				Hibernate.initialize(user.getGroups());
				Hibernate.initialize(user.getRoles());
				HibernateUtil.commitTransaction();

				return user;
			} catch (Throwable e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}*/

	/*public Group merge(Group entity) {
		log.debug(String.format("merge(group id = %s)", entity != null ? entity.getId() : "NULL"));
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				HibernateUtil.getSession().clear();
				Group group = (Group) HibernateUtil.getSession().merge(entity);
				Hibernate.initialize(group.getUsers());
				HibernateUtil.commitTransaction();
				return group;
			} catch (Throwable e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}*/

	/*public static <E extends Serializable> E  merge(Class<E> clazz, E entity) {
		assert(clazz != null) : "Class object is null";
		assert(clazz.getAnnotation(Table.class) != null) : "Class " + clazz.getName() + " has not javax.persistence.Table annotation";
		
		// for log message only
		String idString = null;
		if (entity != null) {
			if (SerializableIdObject.class.isAssignableFrom(clazz)) {
				Long id = ((SerializableIdObject)entity).getId();
				if (id != null) {
					idString = id.toString();
				}
			} else if (SerializableUidObject.class.isAssignableFrom(clazz)) {
				idString = ((SerializableUidObject)entity).getUid();
			}
		}
		log.debug(String.format("merge(%s, %s)", clazz.getName(), (entity != null ? ("entity id="+(idString != null ? idString : "NULL")) : "NULL")));
		
		// merge code
		if (entity != null) {
			try {
				HibernateUtil.beginTransaction();
				@SuppressWarnings("unchecked")
				E mergedEntity = (E) HibernateUtil.getSession().merge(entity);
				HibernateUtil.commitTransaction();

				return mergedEntity;
			} catch (Throwable e) {
				log.error(e.getMessage());
				HibernateUtil.rollbackTransaction();
			}
		}
		return null;
	}*/

}
