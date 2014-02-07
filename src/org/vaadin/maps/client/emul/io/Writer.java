/*
 * Copyright (c) 1996, 2005, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.vaadin.maps.client.emul.io;

public abstract class Writer {

	/**
	 * Temporary buffer used to hold writes of strings and single characters
	 */
	private char[] writeBuffer;

	/**
	 * Size of writeBuffer, must be >= 1
	 */

	private final int writeBufferSize = 1024;

	public abstract void write(int c) throws IOException;

	public void write(char cbuf[]) throws IOException {
		write(cbuf, 0, cbuf.length);
	}

	abstract public void write(char cbuf[], int off, int len)
			throws IOException;

	public void write(String str) throws IOException {
		write(str, 0, str.length());
	}

	public void write(String str, int off, int len) throws IOException {
		char cbuf[];
		if (len <= writeBufferSize) {
			if (writeBuffer == null) {
				writeBuffer = new char[writeBufferSize];
			}
			cbuf = writeBuffer;
		} else { // Don't permanently allocate very large buffers.
			cbuf = new char[len];
		}
		str.getChars(off, (off + len), cbuf, 0);
		write(cbuf, 0, len);
	}

	public Writer append(char c) throws IOException {
		write(c);
		return this;
	}

	abstract public void flush() throws IOException;

	abstract public void close() throws IOException;

}
