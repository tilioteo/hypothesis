/**
 * 
 */
package org.hypothesis.common.file;

import java.io.File;
import java.net.URI;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ConfigFile extends File {

	public ConfigFile(File parent, String child) {
		super(parent, child);
	}

	public ConfigFile(String parent, String child) {
		super(parent, child);
	}

	public ConfigFile(String pathname) {
		super(pathname);
	}

	public ConfigFile(URI uri) {
		super(uri);
	}

}
