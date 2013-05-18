package org.birch.cryptomainia;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

public class EncryptArgv {

	private final String propertyFileName = "encryptargv.properties";
	private String targetMainClass;
	private String prefixPattern;
	private Properties cryptoProperties;
	private int position = -1;

	public EncryptArgv(String targetMainClass, String prefixPattern) {
		this.targetMainClass = targetMainClass;
		this.prefixPattern = prefixPattern;
		cryptoProperties = new Properties();
		try {
			position = Integer.parseInt(prefixPattern);
		} catch (NumberFormatException ignore) {
		}
		;
	}

	private void callTarget(String[] params) throws Exception {

		encryptArgs(params);

		Class<?> mainClass = Class.forName(targetMainClass);
		Method targetMain = mainClass.getMethod("main", String[].class);
		targetMain.invoke(null, (Object) params); // static method doesn't
													// have an instance

	}

	private void encryptArgs(String[] params) throws Exception {
		if (position > 0) {
			encryptArgAtPosition(params);
		} else {
			encryptFirstArgWithPrefix(params);
		}

	}

	private void readKey() throws Exception {

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

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			error("Usage: <target class> <pattern spec> <passthrough arg>...");
		}
		EncryptArgv self = new EncryptArgv(args[0], args[1]);
		String[] params = new String[args.length - 2];
		System.arraycopy(args, 2, params, 0, params.length);
		try {
			self.readKey();
			self.callTarget(params);
		} catch (Exception e) {
			e.printStackTrace();
			error(e.getMessage());
		}

	}


	private static void error(String message) throws Exception {
		System.err.println(message);
		throw new Exception(message);
	}

	private void encryptArgAtPosition(String[] params) throws Exception {
		if (position > 0 && position <= params.length) {
			params[position-1] = decypher(params[position-1]);
		} else {
			throw new Exception("Bad position value: " + position );
		}
	}

	private void encryptFirstArgWithPrefix(String[] params) throws Exception {
		for (int i = 0; i < params.length; i++) {
			if (params[i].startsWith(prefixPattern)) {
				String cipherText = params[i].substring(prefixPattern.length(),
						params[i].length());
				String plainText = decypher(cipherText);
				params[i] = prefixPattern + plainText;
				return;
			}

		}
	}

	private String decypher(String cipherText) throws Exception {
		if (cryptoProperties.getProperty("algorithm").equals("tolower")) {
			return cipherText.toLowerCase();
		}
		error("unknown algorithm" + cryptoProperties.getProperty("algorithm"));
		return null;
	}
}
