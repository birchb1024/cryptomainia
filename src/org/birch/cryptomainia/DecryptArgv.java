package org.birch.cryptomainia;

import java.lang.reflect.Method;
import java.util.Properties;

import org.jasypt.util.text.BasicTextEncryptor;

public class DecryptArgv extends Mainia {

	private String targetMainClass;
	private String prefixPattern;
	private int position = -1;

	public DecryptArgv(String targetMainClass, String prefixPattern) {
		this.targetMainClass = targetMainClass;
		this.prefixPattern = prefixPattern;
		cryptoProperties = new Properties();
		try {
			position = Integer.parseInt(prefixPattern);
		} catch (NumberFormatException ignore) {
		}
	}

	private void callTarget(String[] params) throws Exception {
		decryptArgs(params);

		Class<?> mainClass = Class.forName(targetMainClass);
		Method targetMain = mainClass.getMethod("main", String[].class);
		targetMain.invoke(null, (Object) params);
	}

	private void decryptArgs(String[] params) throws Exception {
		if (position > 0) {
			decryptArgAtPosition(params);
		} else {
			decryptFirstArgWithPrefix(params);
		}

	}

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			error("usage <main class> <argument #>|<prefix pattern> <args>...");
		}
		DecryptArgv self = new DecryptArgv(args[0], args[1]);
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

	private void decryptArgAtPosition(String[] params) throws Exception {
		if (position > 0 && position <= params.length) {
			params[position - 1] = decypher(params[position - 1]);
		} else {
			throw new Exception("Bad position value: " + position);
		}
	}

	private void decryptFirstArgWithPrefix(String[] params) throws Exception {
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
		} else if (cryptoProperties.getProperty("algorithm").equals(
				"org.jasypt.util.text.BasicTextEncryptor")) {
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword(getKey());
			return textEncryptor.decrypt(cipherText);
		}
		error("unknown algorithm" + cryptoProperties.getProperty("algorithm"));
		return null;
	}
}
