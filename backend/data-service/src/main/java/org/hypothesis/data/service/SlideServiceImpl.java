/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import javax.enterprise.inject.Default;

import org.apache.log4j.Logger;
import org.hypothesis.data.interfaces.SlideService;
import org.hypothesis.data.model.Slide;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@Default
public class SlideServiceImpl implements SlideService {

	private static final Logger log = Logger.getLogger(SlideServiceImpl.class);

	private final HibernateDao<Slide, Long> slideDao;

	public SlideServiceImpl() {
		slideDao = new HibernateDao<Slide, Long>(Slide.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypothesis.data.service.SlideService#findById(java.lang.Long)
	 */
	@Override
	public Slide findById(Long id) {
		log.debug("SlideService::findById(" + (id != null ? id : "NULL") + ")");
		try {
			slideDao.beginTransaction();

			Slide task = slideDao.findById(id, false);
			slideDao.commit();
			return task;
		} catch (Exception e) {
			log.error(e.getMessage());
			slideDao.rollback();
		}
		return null;
	}
}
