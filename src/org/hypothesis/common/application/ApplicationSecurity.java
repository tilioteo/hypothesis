/**
 * 
 */
package org.hypothesis.common.application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class ApplicationSecurity {

	private static Cipher encryptCipher = null;
	private static Cipher decryptCipher = null;

	private static Object decryptSerialized(byte[] bytes) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(decryptCipher.doFinal(bytes)));
			Object object = in.readObject();
			in.close();

			return object;

		} catch (Throwable t) {
			return null;
		}
	}

	private static byte[] encryptSerialized(Object object) {
		// Serialize to a byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(object);
			out.close();

			// encrypt serialized object
			return encryptCipher.doFinal(bos.toByteArray());

		} catch (Throwable t) {
			return null;
		}
	}

	public static Object getSessionObject(HttpSession session, String name) {
		if (session != null && name != null && !name.equals("")) {
			if (isSecurityInitialized()) {
				byte[] encryptedBytes = (byte[]) session.getAttribute(name);
				if (encryptedBytes != null && encryptedBytes.length > 0)
					return decryptSerialized(encryptedBytes);
			} else {
				return session.getAttribute(name);
			}
		}
		return null;
	}

	public static void init(SecretKeySpec secretKey, String method) {
		try {
			encryptCipher = Cipher.getInstance(method);
			encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

			decryptCipher = Cipher.getInstance(method);
			decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);

		} catch (Throwable t) {
			encryptCipher = null;
			decryptCipher = null;
		}
	}

	public static boolean isSecurityInitialized() {
		return encryptCipher != null && decryptCipher != null;
	}

	public static void setSessionObject(HttpSession session, String name,
			Object object) {
		if (session != null && name != null && !name.equals("")
				&& object != null) {
			if (isSecurityInitialized()) {
				byte[] encryptedBytes = encryptSerialized(object);
				if (encryptedBytes != null && encryptedBytes.length > 0)
					session.setAttribute(name, encryptedBytes);
			} else {
				session.setAttribute(name, object);
			}
		}
	}
}
