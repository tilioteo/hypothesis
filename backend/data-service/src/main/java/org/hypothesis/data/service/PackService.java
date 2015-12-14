/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hypothesis.data.model.Pack;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PackService implements Serializable {

	private static Logger log = Logger.getLogger(PackService.class);

	private HibernateDao<Pack, Long> packDao;

	public PackService(HibernateDao<Pack, Long> packDao) {
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
