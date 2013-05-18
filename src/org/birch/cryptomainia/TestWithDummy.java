package org.birch.cryptomainia;

import junit.framework.TestCase;

public class TestWithDummy extends TestCase {

	public void testDummy() {
		String[] args = {"A","B","C"};
		DummyTarget.main(args);
		assertEquals(DummyTarget.capturedArgs, args); 
	}

	private void assertArrayEquals(String[] a, String[] b) {
		for ( int i=0; i< a.length; i++) {
			assertEquals(b[i], a[i]);
		}
	}
	public void testEncrypter() throws Exception {
		String[] args = {"org.birch.cryptomainia.DummyTarget","--password=", "A","--password=Vv3obVj2aCNfRbFr6xp7eQ==","3"};
		String[] expected = {"A","--password=bbbb","3"};
		EncryptArgv.main(args);
		
		assertArrayEquals(DummyTarget.capturedArgs, expected); 
	}

	public void testMultipleEncrypter()  throws Exception {
		String[] args = {"org.birch.cryptomainia.DummyTarget","--password=", "--password=ZGXmpDNKPzj54hyZWi+9Kw==","--password=ZGXmpDNKPzj54hyZWi+9Kw==","--password=CCCC"};
		String[] expected = {"--password=BBBB","--password=ZGXmpDNKPzj54hyZWi+9Kw==","--password=CCCC"};
		EncryptArgv.main(args);
		assertArrayEquals(DummyTarget.capturedArgs, expected); 
	}

	public void testEncrypterMissedPrefix()  throws Exception {
		String[] args = {"org.birch.cryptomainia.DummyTarget","--Dfoo=", "A","--DFoo=BBBB","3"};
		String[] expected = {"A","--DFoo=BBBB","3"};
		EncryptArgv.main(args);
		assertArrayEquals(DummyTarget.capturedArgs, expected); 
	}

	public void testEncrypterPosition()  throws Exception {
		String[] args = {"org.birch.cryptomainia.DummyTarget","2", "A","5xXi1rFwqO5HWoIFJJ7walN9iWE4oIDCBGkmJtbQhKw=","C", "D", "E", "F", "G"};
		String[] expected = {"A","MYENCRYPTEDPASSWORD","C", "D", "E", "F", "G"};
		EncryptArgv.main(args);
		assertArrayEquals(DummyTarget.capturedArgs, expected); 
	}

	public void testEncrypterPositionShort()  throws Exception {
		String[] args = {"org.birch.cryptomainia.DummyTarget","1", "5xXi1rFwqO5HWoIFJJ7walN9iWE4oIDCBGkmJtbQhKw="};
		String[] expected = {"MYENCRYPTEDPASSWORD"};
		EncryptArgv.main(args);
		assertArrayEquals(DummyTarget.capturedArgs, expected); 
	}

	public void testEncrypterBadPosition() {
		String[] args = {"org.birch.cryptomainia.DummyTarget","99", "A","MYENCRYPTEDPASSWORD","C", "D", "E", "F", "G"};
		try{ 
			EncryptArgv.main(args);
		} catch (Exception e) {
			assertEquals("Bad position value: 99", e.getMessage());
			return;
		}
		fail("Was expecting an exception.");
	}

}
