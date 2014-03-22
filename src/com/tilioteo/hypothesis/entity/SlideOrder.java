/**
 * 
 */
package com.tilioteo.hypothesis.entity;

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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.tilioteo.hypothesis.common.EntityFieldConstants;
import com.tilioteo.hypothesis.common.EntityTableConstants;

/**
 * @author kamil
 *
 */
@Entity
@Table(name = EntityTableConstants.SLIDE_ORDER_TABLE)
@org.hibernate.annotations.Table(appliesTo = EntityTableConstants.SLIDE_ORDER_TABLE,
indexes = { @Index(name = "IX_TEST_TASK", columnNames = { EntityFieldConstants.TEST_ID, EntityFieldConstants.TASK_ID })})
@Access(AccessType.PROPERTY)
public final class SlideOrder extends SerializableIdObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5052970377982950760L;
	
	/**
	 * processing test
	 */
	private Test test;
	
	private Task task;
	
	private String xmlData;

	protected SlideOrder() {
		super();
	}
	
	public SlideOrder(Test test, Task task) {
		this.test = test;
		this.task = task;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EntityTableConstants.SLIDE_ORDER_GENERATOR)
	@SequenceGenerator(name = EntityTableConstants.SLIDE_ORDER_GENERATOR, sequenceName = EntityTableConstants.SLIDE_ORDER_SEQUENCE, initialValue = 1, allocationSize = 1)
	@Column(name = EntityFieldConstants.ID)
	public final Long getId() {
		return super.getId();
	}
	
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.TEST_ID, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Test getTest() {
		return test;
	}

	public final void setTest(Test test) {
		this.test = test;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = EntityFieldConstants.TASK_ID, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	public final Task getTask() {
		return task;
	}

	public final void setTask(Task task) {
		this.task = task;
	}

	@Column(name = EntityFieldConstants.XML_DATA)
	@Type(type="text")
	protected final String getData() {
		return xmlData;
	}

	protected final void setData(String data) {
		this.xmlData = data;
	}
	
	@Transient
	public final List<Integer> getOrder() {
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		if (xmlData != null) {
			String[] strings = xmlData.split(",");
			
			for (String string : strings) {
				Integer item = Integer.getInteger(string);
				if (item != null) {
					list.add(item);
				}
			}
		}
		
		return list;
	}
	
	public final void setOrder(List<Integer> list) {
		if (list != null) {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (Integer item : list) {
				if (item != null) {
					if (first) {
						first = false;
					} else {
						builder.append(",");
					}
					builder.append(item);
				}
			}
			xmlData = builder.toString();
			
		} else {
			xmlData = null;
		}
	}

}
