/**
 * 
 */
package org.hypothesis.common.file;

import java.io.File;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public interface FileInterface {
	
	public boolean open();
	public boolean open(String fileName);
	public boolean open(File file);
	public boolean save();
	public boolean save(String fileName);
	public boolean save(File file);
}
