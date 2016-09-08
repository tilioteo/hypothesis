/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.service;

import org.apache.log4j.Logger;
import org.hypothesis.data.model.Slide;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class SlideService {

	private static final Logger log = Logger.getLogger(SlideService.class);

	private final HibernateDao<Slide, Long> slideDao;

	public static SlideService newInstance() {
		return new SlideService(new HibernateDao<Slide, Long>(Slide.class));
	}

	protected SlideService(HibernateDao<Slide, Long> taskDao) {
		this.slideDao = taskDao;
	}

	public Slide findById(Long id) {
		log.debug("SlideService::findById(" + (id != null ? id : "NULL") + ")");
		try {
			slideDao.beginTransaction();

			Slide task = slideDao.findById(id, false);
			slideDao.commit();
			return task;
		} catch (Throwable e) {
			log.error(e.getMessage());
			slideDao.rollback();
		}
		return null;
	}
}
