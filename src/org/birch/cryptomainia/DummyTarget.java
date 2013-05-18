package org.birch.cryptomainia;

public class DummyTarget {


	public static String[] capturedArgs;
	
	private static void print(String msg) {
		System.out.print(msg);
	}
	public static void main(String[] args) {
		capturedArgs = args;
		print("DummyTarget: ");
		for( String a : args) {
			print(a + " ");
		}
		print("\n");
	}

}
