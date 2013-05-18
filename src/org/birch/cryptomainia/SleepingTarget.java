package org.birch.cryptomainia;

import java.io.IOException;

public class SleepingTarget { 

	/**
	 * Wait for user to hit enter.
	 */
	public static String[] capturedArgs;
	
	private static void print(String msg) {
		System.out.print(msg);
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		capturedArgs = args;
		print("SleepingTarget: ");
		for( String a : args) {
			print(a + " ");
		}
		print("\nSleeping for 10 seconds...\n");
		 Thread.sleep(10*1000);
	}

}
