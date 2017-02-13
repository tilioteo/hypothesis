/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hypothesis.data.interfaces.PackService;
import org.hypothesis.data.model.Pack;

import javax.enterprise.inject.Default;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class PackServiceImpl implements PackService {

	private static final Logger log = Logger.getLogger(PackServiceImpl.class);

	private final HibernateDao<Pack, Long> packDao;

	public PackServiceImpl(HibernateDao<Pack, Long> packDao) {
		this.packDao = packDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.PackService#findPackById(java.lang.Long)
	 */
	@Override
	public Pack findById(Long id) {
		log.debug(String.format("findPackById: id = %s", id != null ? id : "null"));
		try {
			packDao.beginTransaction();
			Pack pack = packDao.findById(id, true);
			packDao.commit();

			return pack;
		} catch (Exception e) {
			log.error(e.getMessage());
			packDao.rollback();
			return null;
		}
	}

}
