/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.oldservice;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hypothesis.data.dao.HibernateDao;
import org.hypothesis.data.oldmodel.Pack;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Deprecated
public class PackService implements Serializable {

	private static final Logger log = Logger.getLogger(PackService.class);

	private final HibernateDao<Pack, Long> packDao;

	public static PackService newInstance() {
		return new PackService(new HibernateDao<>(Pack.class));
	}

	public PackService(HibernateDao<Pack, Long> packDao) {
		this.packDao = packDao;
	}

	public Pack merge(Pack pack) {
		try {
			packDao.beginTransaction();
			pack = mergeInit(pack);
			packDao.commit();
			return pack;
		} catch (Throwable e) {
			log.error(e.getMessage());
			packDao.rollback();
		}
		return null;
	}

	private Pack mergeInit(Pack pack) {
		packDao.clear();
		pack = packDao.merge(pack);
		// Hibernate.initialize(pack.getBranches());
		return pack;
	}

	public Pack find(Long id) {
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
