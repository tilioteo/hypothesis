/**
 * 
 */
package org.hypothesis.entity;

import java.util.LinkedList;
import java.util.List;

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

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hypothesis.common.SerializableIdObject;

/**
 * @author kamil
 *
 */
@Entity
@Table(name = "TBL_SLIDE_ORDER")
@org.hibernate.annotations.Table(appliesTo = "TBL_SLIDE_ORDER",
indexes = { @Index(name = "IX_TEST_TASK", columnNames = { "TEST_ID", "TASK_ID" })})
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "slideOrderGenerator")
	@SequenceGenerator(name = "slideOrderGenerator", sequenceName = "hbn_slide_order_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	public Long getId() {
		return super.getId();
	}
	
	@ManyToOne
	@JoinColumn(name = "TEST_ID", nullable = false)
	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	@ManyToOne
	@JoinColumn(name = "TASK_ID", nullable = false)
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Column(name = "XML_DATA")
	@Type(type="text")
	protected String getData() {
		return xmlData;
	}

	protected void setData(String data) {
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
