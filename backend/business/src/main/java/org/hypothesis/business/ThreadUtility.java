package org.hypothesis.business;

public class ThreadUtility {
	
	public static ThreadGroup createExportGroup() {
		return new ThreadGroup("export-service");
	}

}
