/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Database entity for test instance
 * 
 */
@Entity
@Table(name = EntityTableConstants.TEST_TABLE)
@Access(AccessType.PROPERTY)
public class SimpleTest extends SerializableIdObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5623194129920968655L;

	/**
	 * signalize if test data are for production
	 */
	private boolean production;

	/**
	 * timestamp test created at
	 */
	private Date created;

	/**
	 * timestamp test started at
	 */
	private Date started;

	/**
	 * timestamp test done at
	 */
	private Date finished;

	/**
	 * timestamp test broken at
	 */
	private Date broken;

	/**
	 * timestamp of last access
	 */
	private Date lastAccess;

	/**
	 * status code
	 */
	private Integer status;
	private User user;

	private Pack pack;

	/**
	 * last processing branch
	 */
	private Branch lastBranch;

	/**
	 * last processing task
	 */
	private Task lastTask;

	/**
	 * last processing slide
	 */
	private Slide lastSlide;

	public static final SimpleTest DUMMY_TEST = new SimpleTest();
	
	protected SimpleTest() {
		super();
	}

	public SimpleTest(Pack pack, User user) {
		this();
		production = false;
		this.pack = pack;
		this.user = user;
		lastBranch = null;
		lastTask = null;
		lastSlide = null;

		created = new Date();
		started = null;
		finished = null;
		broken = null;
		lastAccess = created;
		setStatus(Status.CREATED);
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.TEST_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.TEST_GENERATOR, sequenceName = EntityTableConstants.TEST_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}

	@Column(name = EntityFieldConstants.PRODUCTION, nullable = false)
	public boolean isProduction() {
		return production;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	@Column(name = EntityFieldConstants.CREATED, nullable = false)
	public Date getCreated() {
		return created;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}

	@Column(name = EntityFieldConstants.STARTED)
	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	@Column(name = EntityFieldConstants.FINISHED)
	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	@Column(name = EntityFieldConstants.BROKEN)
	public Date getBroken() {
		return broken;
	}

	public void setBroken(Date broken) {
		this.broken = broken;
	}

	@Column(name = EntityFieldConstants.LAST_ACCESS, nullable = false)
	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	@Column(name = EntityFieldConstants.STATUS, nullable = false)
	protected Integer getStatusInternal() {
		return status;
	}
	
	protected void setStatusInternal(Integer status) {
		this.status = status;
	}

	@Transient
	public final Status getStatus() {
		return Status.get(getStatusInternal());
	}

	public final void setStatus(Status status) {
		if (status != null) {
			setStatusInternal(status.getCode());
		} else {
			setStatusInternal(null);
		}
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.USER_ID)
	public User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.PACK_ID, nullable = false)
	public Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.LAST_BRANCH_ID)
	public Branch getLastBranch() {
		return lastBranch;
	}

	public void setLastBranch(Branch branch) {
		this.lastBranch = branch;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.LAST_TASK_ID)
	public Task getLastTask() {
		return lastTask;
	}

	public void setLastTask(Task task) {
		this.lastTask = task;
	}

	@ManyToOne
	@JoinColumn(name = EntityFieldConstants.LAST_SLIDE_ID)
	public Slide getLastSlide() {
		return lastSlide;
	}

	public void setLastSlide(Slide slide) {
		this.lastSlide = slide;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimpleTest other = (SimpleTest) obj;
		
		Long id = getId();
		Long id2 = other.getId();
		boolean production = isProduction();
		boolean production2 = other.isProduction();
		Date created = getCreated();
		Date created2 = other.getCreated();
		Date started = getStarted();
		Date started2 = other.getStarted();
		Date finished = getFinished();
		Date finished2 = other.getFinished();
		Date broken = getBroken();
		Date broken2 = other.getBroken();
		Date lastAccess = getLastAccess();
		Date lastAccess2 = other.getLastAccess();
		Integer status = getStatusInternal();
		Integer status2 = other.getStatusInternal();
		User user = getUser();
		User user2 = other.getUser();
		Pack pack = getPack();
		Pack pack2 = other.getPack();
		Branch lastBranch = getLastBranch();
		Branch lastBranch2 = other.getLastBranch();
		Task lastTask = getLastTask();
		Task lastTask2 = other.getLastTask();
		Slide lastSlide = getLastSlide();
		Slide lastSlide2 = other.getLastSlide();
		//List<Event> events = getEvents();
		//List<Event> events2 = other.getEvents();
		
		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}
		
		if (production != production2) {
			return false;
		}

		if (created != null && !created.equals(created2)) {
			return false;
		} else if (created2 != null) {
			return false;
		}
		
		if (started != null && !started.equals(started2)) {
			return false;
		} else if (started2 != null) {
			return false;
		}
		
		if (finished != null && !finished.equals(finished2)) {
			return false;
		} else if (finished2 != null) {
			return false;
		}
		
		if (broken != null && !broken.equals(broken2)) {
			return false;
		} else if (broken2 != null) {
			return false;
		}
		
		if (lastAccess != null && !lastAccess.equals(lastAccess2)) {
			return false;
		} else if (lastAccess2 != null) {
			return false;
		}
		
		if (status != null && !status.equals(status2)) {
			return false;
		} else if (status2 != null) {
			return false;
		}
		
		if (user != null && !user.equals(user2)) {
			return false;
		} else if (user2 != null) {
			return false;
		}
		
		if (pack != null && !pack.equals(pack2)) {
			return false;
		} else if (pack2 != null) {
			return false;
		}
		
		if (lastBranch != null && !lastBranch.equals(lastBranch2)) {
			return false;
		} else if (lastBranch2 != null) {
			return false;
		}
		
		if (lastTask != null && !lastTask.equals(lastTask2)) {
			return false;
		} else if (lastTask2 != null) {
			return false;
		}
		
		if (lastSlide != null && !lastSlide.equals(lastSlide2)) {
			return false;
		} else if (lastSlide2 != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		Long id = getId();
		boolean production = isProduction();
		Date created = getCreated();
		Date started = getStarted();
		Date finished = getFinished();
		Date broken = getBroken();
		Date lastAccess = getLastAccess();
		Integer status = getStatusInternal();
		User user = getUser();
		Pack pack = getPack();
		Branch lastBranch = getLastBranch();
		Task lastTask = getLastTask();
		Slide lastSlide = getLastSlide();
		//List<Event> events = getEvents();

		final int prime = 53;
		int result = 1;
		result = prime * result + (id != null ? id.hashCode() : 0);
		result = prime * result + (production ? 1 : 0);
		result = prime * result + (created != null ? created.hashCode() : 0);
		result = prime * result + (started != null ? started.hashCode() : 0);
		result = prime * result + (finished != null ? finished.hashCode() : 0);
		result = prime * result + (broken != null ? broken.hashCode() : 0);
		result = prime * result + (lastAccess != null ? lastAccess.hashCode() : 0);
		result = prime * result + (status != null ? status.hashCode() : 0);
		result = prime * result + (user != null ? user.hashCode() : 0);
		result = prime * result + (pack != null ? pack.hashCode() : 0);
		result = prime * result + (lastBranch != null ? lastBranch.hashCode() : 0);
		result = prime * result + (lastTask != null ? lastTask.hashCode() : 0);
		result = prime * result + (lastSlide != null ? lastSlide.hashCode() : 0);
		//result = prime * result + events.hashCode();
		return result;
	}

}
