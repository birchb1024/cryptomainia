package org.birch.cryptomainia;

import java.io.InputStream;
import java.util.Properties;

public class Mainia {
	protected final String propertyFileName = "cryptomainia.properties";
	protected Properties cryptoProperties;

	protected void readKey() throws Exception {

		InputStream in = EncryptArgv.class.getClassLoader()
				.getResourceAsStream(propertyFileName);

		if (in == null) {
			error("Could not access key file in classpath: " + propertyFileName);
		}
		cryptoProperties.load(in);

		assertProperty("key");
		assertProperty("algorithm");
	}

	private void assertProperty(String key) throws Exception {
		if (cryptoProperties.getProperty(key) == null) {
			error("missing crypto propety: " + key);
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

	protected static void error(String message) throws Exception {
		System.err.println(message);
		throw new Exception(message);
	}

}
