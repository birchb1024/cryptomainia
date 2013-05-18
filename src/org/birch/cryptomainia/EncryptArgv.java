package org.birch.cryptomainia;

import java.util.Properties;

import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptArgv extends Mainia {


	public EncryptArgv() {
		cryptoProperties = new Properties();
	}

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			EncryptArgv self = new EncryptArgv();
			self.readKey();
			System.out.println(self.encrypt(args));
		}
	}

	public String encrypt(String[] args) throws Exception {
		if (cryptoProperties.getProperty("algorithm").equals("tolower")) {
			return args[0].toUpperCase();
		} else if (cryptoProperties.getProperty("algorithm").equals(
				"org.jasypt.util.text.BasicTextEncryptor")) {
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword(getKey());
			String myEncryptedText = textEncryptor.encrypt(args[0]);
			return myEncryptedText;
		}
		return null;
	}

}
