package org.birch.cryptomainia;

import java.io.InputStream;
import java.util.Properties;

import org.jasypt.util.text.BasicTextEncryptor;

public class Mainia {
	protected final String propertyFileName = "cryptomainia.properties";
	protected Properties cryptoProperties;

	protected void readKey() throws Exception {

		InputStream in = EncryptArgv.class.getClassLoader()
				.getResourceAsStream(propertyFileName);

		if (in == null) {
			print_error_and_quit("Could not access key file in classpath: " + propertyFileName);
		}
		cryptoProperties.load(in);

		assertProperty("key");
		assertProperty("algorithm");
	}

	private void assertProperty(String key) throws Exception {
		if (cryptoProperties.getProperty(key) == null) {
			print_error_and_quit("missing crypto propety: " + key);
		}
	}

	protected String getKey() {
		return cryptoProperties.getProperty("key").trim();
	}

	protected String getAlgorithm() {
		return cryptoProperties.getProperty("algorithm").trim();
	}

	protected boolean isAlgorithm(String other) {
		return cryptoProperties.getProperty("algorithm").trim().equals(other);
	}

	protected String decypher(String cipherText) throws Exception {
		if (isAlgorithm("tolower")) {
			return cipherText.toLowerCase();
		} else if (isAlgorithm(
				"org.jasypt.util.text.BasicTextEncryptor")) {
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword(getKey());
			return textEncryptor.decrypt(cipherText);
		}
		print_error_and_quit("unknown algorithm" + cryptoProperties.getProperty("algorithm"));
		return null;
	}

	protected static void print_error_and_quit(String message) {
		System.err.println(message);
		System.exit(1);
	}

}
