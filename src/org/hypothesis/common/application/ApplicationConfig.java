/**
 * 
 */
package org.hypothesis.common.application;

import java.io.File;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.hypothesis.common.Strings;
import org.hypothesis.common.file.XmlDocumentFile;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Parsing of user application xml config file
 * 
 */
public class ApplicationConfig extends XmlDocumentFile {
	private String applicationBasePath = "";
	private String hibernateConfigName = "";
	private SecretKeySpec secretKey = null;
	private String cipherMethod = "";

	public ApplicationConfig(String applicationBasePath) {
		super();
		this.applicationBasePath = applicationBasePath;
	}

	public String getCipherMethod() {
		return cipherMethod;
	}

	public String getHibernateConfigName() {
		return hibernateConfigName;
	}

	public SecretKeySpec getSecretKey() {
		return secretKey;
	}

	@Override
	protected void initFromRoot(Element root) {
		try {
			if (root.getName().equalsIgnoreCase("ApplicationConfig")) {
				Node node = root.selectSingleNode("Hibernate");
				initHibernate(node);
				node = root.selectSingleNode("ApplicationSecurity");
				initSecurity(node);
			}
		} catch (Throwable e) {
			e.getMessage();
		}
	}

	private void initHibernate(Node node) {
		if (node != null) {
			Node configFileNode = node.selectSingleNode("ConfigFile");
			if (configFileNode != null) {
				hibernateConfigName = makeAbsolutePath(configFileNode.getText());
			}
		}
	}

	private void initSecurity(Node node) {
		if (node != null) {
			Node secretKeyNode = node.selectSingleNode("SecretKey");
			if (secretKeyNode != null) {
				String key = secretKeyNode.getText();
				cipherMethod = ((Element) secretKeyNode)
						.attributeValue("Method");
				if (key != null && !key.equals("") && cipherMethod != null
						&& !cipherMethod.equals("")) {
					try {
						secretKey = new SecretKeySpec(
								Strings.fromHexString(key), cipherMethod);
					} catch (Throwable t) {
						secretKey = null;
					}
				}
			}
		}
	}

	private String makeAbsolutePath(String path) {
		File file = new File(path);
		if (!file.isDirectory() && !file.isAbsolute()) {
			return FilenameUtils.concat(applicationBasePath, path);
		}

		return "";
	}

}
