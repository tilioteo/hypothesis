/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@Entity
@Table(name = TableConstants.TEST_TABLE)
@Access(AccessType.PROPERTY)
public class Test extends SerializableIdObject {
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

	protected Test() {
		super();
	}

	/*
	 * public Test(Pack pack, User user) { this(); production = false; this.pack
	 * = pack; this.user = user; lastBranch = null; lastTask = null; lastSlide =
	 * null;
	 * 
	 * created = new Date(); started = null; finished = null; broken = null;
	 * lastAccess = created; setStatus(Status.CREATED); }
	 */

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TableConstants.TEST_GENERATOR)
	@SequenceGenerator(name = TableConstants.TEST_GENERATOR, sequenceName = TableConstants.TEST_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = FieldConstants.ID)
	public Long getId() {
		return super.getId();
	}

	@Column(name = FieldConstants.PRODUCTION, nullable = false)
	public boolean isProduction() {
		return production;
	}

	public void setProduction(Boolean production) {
		this.production = production != null ? production : false;
	}

	@Column(name = FieldConstants.CREATED, nullable = false)
	public Date getCreated() {
		return created;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}

	@Column(name = FieldConstants.STARTED)
	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	@Column(name = FieldConstants.FINISHED)
	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	@Column(name = FieldConstants.BROKEN)
	public Date getBroken() {
		return broken;
	}

	public void setBroken(Date broken) {
		this.broken = broken;
	}

	@Column(name = FieldConstants.LAST_ACCESS, nullable = false)
	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	@Column(name = FieldConstants.STATUS, nullable = false)
	protected Integer getStatusInternal() {
		return status;
	}

	protected void setStatusInternal(Integer status) {
		this.status = status;
	}

	@Transient
	public Status getStatus() {
		return Status.get(getStatusInternal());
	}

	public void setStatus(Status status) {
		if (status != null) {
			setStatusInternal(status.getCode());
		} else {
			setStatusInternal(null);
		}
	}

	@ManyToOne(/* cascade = { CascadeType.PERSIST, CascadeType.MERGE } */)
	@JoinColumn(name = FieldConstants.USER_ID)
	// @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(/* cascade = { CascadeType.PERSIST, CascadeType.MERGE } */)
	@JoinColumn(name = FieldConstants.PACK_ID, nullable = false)
	// @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public Pack getPack() {
		return pack;
	}

	protected void setPack(Pack pack) {
		this.pack = pack;
	}

	@ManyToOne(/* cascade = { CascadeType.PERSIST, CascadeType.MERGE } */)
	@JoinColumn(name = FieldConstants.LAST_BRANCH_ID)
	// @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public Branch getLastBranch() {
		return lastBranch;
	}

	public void setLastBranch(Branch branch) {
		this.lastBranch = branch;
	}

	@ManyToOne(/* cascade = { CascadeType.PERSIST, CascadeType.MERGE } */)
	@JoinColumn(name = FieldConstants.LAST_TASK_ID)
	// @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public Task getLastTask() {
		return lastTask;
	}

	public void setLastTask(Task task) {
		this.lastTask = task;
	}

	@ManyToOne(/* cascade = { CascadeType.PERSIST, CascadeType.MERGE } */)
	@JoinColumn(name = FieldConstants.LAST_SLIDE_ID)
	// @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public Slide getLastSlide() {
		return lastSlide;
	}

	public void setLastSlide(Slide slide) {
		this.lastSlide = slide;
	}

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = TableConstants.TEST_EVENT_TABLE, joinColumns = @JoinColumn(name = FieldConstants.TEST_ID) , inverseJoinColumns = @JoinColumn(name = FieldConstants.EVENT_ID) )
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@LazyCollection(LazyCollectionOption.TRUE)
	@OrderColumn(name = FieldConstants.RANK)
	public List<Event> getEvents() {
		return events;
	}

	protected void setEvents(List<Event> list) {
		this.events = list;
	}

	public final void addEvent(Event event) {
		if (event != null) {
			getEvents().add(event);
		}
	}

	public final void removeEvent(Event event) {
		getEvents().remove(event);
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
		Test other = (Test) obj;

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
		// List<Event> events = getEvents();
		// List<Event> events2 = other.getEvents();

		// if id of one instance is null then compare other properties
		if (id != null && id2 != null && !id.equals(id2)) {
			return false;
		}

		if (production != production2) {
			return false;
		}

		if (created == null) {
			if (created2 != null) {
				return false;
			}
		} else if (!created.equals(created2)) {
			return false;
		}

		if (started == null) {
			if (started2 != null) {
				return false;
			}
		} else if (!started.equals(started2)) {
			return false;
		}

		if (finished == null) {
			if (finished2 != null) {
				return false;
			}
		} else if (!finished.equals(finished2)) {
			return false;
		}

		if (broken == null) {
			if (broken2 != null) {
				return false;
			}
		} else if (!broken.equals(broken2)) {
			return false;
		}

		if (lastAccess == null) {
			if (lastAccess2 != null) {
				return false;
			}
		} else if (!lastAccess.equals(lastAccess2)) {
			return false;
		}

		if (status == null) {
			if (status2 != null) {
				return false;
			}
		} else if (!status.equals(status2)) {
			return false;
		}

		if (user == null) {
			if (user2 != null) {
				return false;
			}
		} else if (!user.equals(user2)) {
			return false;
		}

		if (pack == null) {
			if (pack2 != null) {
				return false;
			}
		} else if (!pack.equals(pack2)) {
			return false;
		}

		if (lastBranch == null) {
			if (lastBranch2 != null) {
				return false;
			}
		} else if (!lastBranch.equals(lastBranch2)) {
			return false;
		}

		if (lastTask == null) {
			if (lastTask2 != null) {
				return false;
			}
		} else if (!lastTask.equals(lastTask2)) {
			return false;
		}

		if (lastSlide == null) {
			if (lastSlide2 != null) {
				return false;
			}
		} else if (!lastSlide.equals(lastSlide2)) {
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
		List<Event> events = getEvents();

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
		result = prime * result + events.hashCode();
		return result;
	}

}
