/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.dao.PackDao;
import com.tilioteo.hypothesis.entity.Pack;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class PackManager {

	private static Logger log = Logger.getLogger(PackManager.class);

	private PackDao packDao;

	public PackManager(PackDao packDao) {
		this.packDao = packDao;
	}

	public Pack findPackById(Long id) {
		log.debug(String.format("findPackById: id = %s", id != null ? id : "null"));
		try {
			packDao.beginTransaction();
			Pack pack = packDao.findById(id, true);
			packDao.commit();

			return pack;
		} catch (Throwable e) {
			log.error(e.getMessage());
			packDao.rollback();
			return null;
		}
	}

}
