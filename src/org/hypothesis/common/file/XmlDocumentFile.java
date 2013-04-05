/**
 * 
 */
package org.hypothesis.common.file;

import java.io.File;

import org.dom4j.Element;
import org.hypothesis.common.xml.Utility;
import org.hypothesis.common.xml.XmlDocument;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class XmlDocumentFile extends XmlDocument implements FileInterface {
	private File file = null;

	public XmlDocumentFile() {
	}
	
	public XmlDocumentFile(File file) {
		this.file = file;
	}
	
	public XmlDocumentFile(String fileName) {
		file = new File(fileName);
	}
	
	private void init() {
		Element root = getRootElement();
		if (root != null) {
			initFromRoot(root);
		}
	}
	
	protected void initFromRoot(Element root) {
	}

	public boolean open() {
		doc = Utility.readFile(file);
		if (doc != null) {
			init();
		}

		return (doc != null);
	}
	
	public boolean open(File file) {
		this.file = file;
		return open();
	}
	
	public boolean open(String fileName) {
		if (fileName.length() > 0) {
			file = new File(fileName);
			return open();
		}
		return false;
	}
	
	public boolean save() {
		//return Utility.writeFile(doc, file);
		return false;
	}
	
	public boolean save(File file) {
		this.file = file;
		return save();
	}
	
	public boolean save(String fileName) {
		if (fileName.length() > 0) {
			file = new File(fileName);
			return save();
		}
		return false;
	}
	
	/*@Override
	public Element getRootElement() {
		return super.getRootElement();
	}
	
	@Override
	public Document getDocument() {
		return super.getDocument();
	}*/
}
