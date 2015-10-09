/**
 * 
 */
package com.tilioteo.hypothesis.persistence;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.dao.SlideDao;
import com.tilioteo.hypothesis.entity.Slide;

/**
 * @author kamil
 *
 */
public class SlideService {

	private static Logger log = Logger.getLogger(SlideService.class);

	private SlideDao slideDao; 

	public static SlideService newInstance() {
		return new SlideService(new SlideDao());
	}
	
	protected SlideService(SlideDao taskDao) {
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
