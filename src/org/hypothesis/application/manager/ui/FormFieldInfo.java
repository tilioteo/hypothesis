package org.hypothesis.application.manager.ui;

public class FormFieldInfo {
	private int row;
	private String caption;

	public FormFieldInfo(int row, String caption) {
		this.row = row;
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

	public int getRow() {
		return row;
	}
}
