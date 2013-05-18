package org.birch.cryptomainia;

import org.jasypt.util.text.BasicTextEncryptor;

import junit.framework.TestCase;

public class TestEncryption extends TestCase {

	public void testEncrypterPositionShort() throws Exception {
		EncryptArgv self = new EncryptArgv();
		String[] args = {"bbbb"};
		String cipher = self.encrypt(args);
		System.out.println(cipher);
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword("foobar789");
	
		assertEquals(textEncryptor.decrypt(cipher), args[0]);
	}
}
