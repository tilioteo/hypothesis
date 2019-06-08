package org.hypothesis.data.service.impl;

import org.hypothesis.context.HibernateUtil;

public class TransactionManager {

	public static final void begin() {
		HibernateUtil.beginTransaction();
	};

	public static final void commit() {
		HibernateUtil.commitTransaction();
	};

	public static final void rollback() {
		HibernateUtil.rollbackTransaction();
	};

}
