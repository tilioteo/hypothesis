/**
 * 
 */
package org.hypothesis.persistence;

import org.hypothesis.entity.Pack;
import org.hypothesis.persistence.hibernate.PackDao;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class PackManager {

	// private static Logger logger = Logger.getLogger(TestManager.class);

	private PackDao packDao;

	public PackManager(PackDao packDao) {
		this.packDao = packDao;
	}

	public Pack findPackById(Long id) {
		// logger.log(GPTest.LOG_PRIORITY,
		// String.format("TestManager.findTest(%d)", id));
		try {
			packDao.beginTransaction();
			Pack pack = packDao.findById(id, true);
			packDao.commit();

			// logger.log(GPTest.LOG_PRIORITY, "return test");
			return pack;
		} catch (Throwable e) {
			packDao.rollback();
			// logger.error(String.format("TestManager.findTest(%d)", id), e);
			return null;
		}
	}

}
