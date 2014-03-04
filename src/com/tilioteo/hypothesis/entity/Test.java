/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
public final class Test extends SerializableIdObject {
	public enum Status {
		CREATED(1), STARTED(2), FINISHED(3), BROKEN_BY_CLIENT(4), BROKEN_BY_ERROR(
				5);

		private static final Map<Integer, Status> lookup = new HashMap<Integer, Status>();

		static {
			for (Status s : EnumSet.allOf(Status.class))
				lookup.put(s.getCode(), s);
		}

		public static Status get(int code) {
			return lookup.get(code);
		}

		private Integer code;

		private Status(Integer code) {
			this.code = code;
		}

		public Integer getCode() {
			return code;
		}
	}

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

	/**
	 * list of events in running test
	 */
	private List<Event> events = new LinkedList<Event>();

	public static final Test DUMMY_TEST = new Test();
	
	protected Test() {
		super();
	}

	public Test(Pack pack, User user) {
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
	public final Date getCreated() {
		return created;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}

	@Column(name = EntityFieldConstants.STARTED)
	public final Date getStarted() {
		return started;
	}

	public final void setStarted(Date started) {
		this.started = started;
	}

	@Column(name = EntityFieldConstants.FINISHED)
	public final Date getFinished() {
		return finished;
	}

	public final void setFinished(Date finished) {
		this.finished = finished;
	}

	@Column(name = EntityFieldConstants.BROKEN)
	public final Date getBroken() {
		return broken;
	}

	public final void setBroken(Date broken) {
		this.broken = broken;
	}

	@Column(name = EntityFieldConstants.LAST_ACCESS, nullable = false)
	public final Date getLastAccess() {
		return lastAccess;
	}

	public final void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	@Column(name = EntityFieldConstants.STATUS, nullable = false)
	public final Status getStatus() {
		return Status.get(status);
	}

	public final void setStatus(Status status) {
		this.status = status.getCode();
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.USER_ID)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.PACK_ID, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.LAST_BRANCH_ID)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Branch getLastBranch() {
		return lastBranch;
	}

	public final void setLastBranch(Branch branch) {
		this.lastBranch = branch;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.LAST_TASK_ID)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Task getLastTask() {
		return lastTask;
	}

	public final void setLastTask(Task task) {
		this.lastTask = task;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.LAST_SLIDE_ID)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Slide getLastSlide() {
		return lastSlide;
	}

	public final void setLastSlide(Slide slide) {
		this.lastSlide = slide;
	}

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = EntityTableConstants.TEST_EVENT_TABLE, joinColumns = @JoinColumn(name = EntityFieldConstants.TEST_ID), inverseJoinColumns = @JoinColumn(name = EntityFieldConstants.EVENT_ID))
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderColumn(name = EntityFieldConstants.RANK)
	public final List<Event> getEvents() {
		return events;
	}

	protected void setEvents(List<Event> list) {
		this.events = list;
	}

	public final void addEvent(Event event) {
		if (event != null)
			this.events.add(event);
	}

	public final void removeEvent(Event event) {
		this.events.remove(event);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Test other = (Test) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		// TODO remove when Buffered.SourceException occurs
		if (getBroken() == null) {
			if (other.getBroken() != null)
				return false;
		} else if (!getBroken().equals(other.getBroken()))
			return false;
		if (getCreated() == null) {
			if (other.getCreated() != null)
				return false;
		} else if (!getCreated().equals(other.getCreated()))
			return false;
		if (getEvents() == null) {
			if (other.getEvents() != null)
				return false;
		} else if (!getEvents().equals(other.getEvents()))
			return false;
		if (getFinished() == null) {
			if (other.getFinished() != null)
				return false;
		} else if (!getFinished().equals(other.getFinished()))
			return false;
		if (getLastAccess() == null) {
			if (other.getLastAccess() != null)
				return false;
		} else if (!getLastAccess().equals(other.getLastAccess()))
			return false;
		if (getLastBranch() == null) {
			if (other.getLastBranch() != null)
				return false;
		} else if (!getLastBranch().equals(other.getLastBranch()))
			return false;
		if (getLastSlide() == null) {
			if (other.getLastSlide() != null)
				return false;
		} else if (!getLastSlide().equals(other.getLastSlide()))
			return false;
		if (getLastTask() == null) {
			if (other.getLastTask() != null)
				return false;
		} else if (!getLastTask().equals(other.getLastTask()))
			return false;
		if (getPack() == null) {
			if (other.getPack() != null)
				return false;
		} else if (!getPack().equals(other.getPack()))
			return false;
		if (getStarted() == null) {
			if (other.getStarted() != null)
				return false;
		} else if (!getStarted().equals(other.getStarted()))
			return false;
		if (getStatus() == null) {
			if (other.getStatus() != null)
				return false;
		} else if (!getStatus().equals(other.getStatus()))
			return false;
		if (getUser() == null) {
			if (other.getUser() != null)
				return false;
		} else if (!getUser().equals(other.getUser()))
			return false;
		return true;
	}

	@Override
	public final int hashCode() {
		final int prime = 79;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		// TODO remove when Buffered.SourceException occurs
		result = prime * result
				+ ((getBroken() == null) ? 0 : getBroken().hashCode());
		result = prime * result
				+ ((getCreated() == null) ? 0 : getCreated().hashCode());
		result = prime * result
				+ ((getEvents() == null) ? 0 : getEvents().hashCode());
		result = prime * result
				+ ((getFinished() == null) ? 0 : getFinished().hashCode());
		result = prime * result
				+ ((getLastAccess() == null) ? 0 : getLastAccess().hashCode());
		result = prime * result
				+ ((getLastBranch() == null) ? 0 : getLastBranch().hashCode());
		result = prime * result
				+ ((getLastSlide() == null) ? 0 : getLastSlide().hashCode());
		result = prime * result
				+ ((getLastTask() == null) ? 0 : getLastTask().hashCode());
		result = prime * result
				+ ((getPack() == null) ? 0 : getPack().hashCode());
		result = prime * result
				+ ((getStarted() == null) ? 0 : getStarted().hashCode());
		result = prime * result
				+ ((getStatus() == null) ? 0 : getStatus().hashCode());
		result = prime * result
				+ ((getUser() == null) ? 0 : getUser().hashCode());
		return result;
	}

}
