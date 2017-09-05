package org.birch.cryptomainia;

import java.util.Map;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.Runtime;

public class DecryptSubprocess extends Mainia {

	private String environmentVariableName;
	private String encryptedPassword;
	private String decryptedPassword;

	public DecryptSubprocess(String environmentVariableName,
			String encryptedPassword) {
		this.environmentVariableName = environmentVariableName;
		this.encryptedPassword = encryptedPassword;
		cryptoProperties = new Properties();
	}

	public void decyptPassword() throws Exception {
		decryptedPassword = decypher(encryptedPassword);
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			print_error_and_quit("Usage: <environment variable name> <encrypted password> <command line and args>...");
		}
		DecryptSubprocess self = new DecryptSubprocess(args[0], args[1]);
		String[] commandLine = new String[args.length - 2];
		System.arraycopy(args, 2, commandLine, 0, commandLine.length);
		try {
			self.readKey();
			self.decyptPassword();
			self.spawnSubprocess(commandLine);
		} catch (Exception e) {
			e.printStackTrace();
			print_error_and_quit(e.getMessage());
		}

	}

	private void spawnSubprocess(String[] commandLine) throws IOException,
			InterruptedException {
		Runtime rt = Runtime.getRuntime();
		String[] newEnv = makeNewEnvironment();
		Process proc = rt.exec(commandLine, newEnv);
		int exitVal = proc.waitFor();

		outputResults(System.out, proc.getInputStream());
		outputResults(System.err, proc.getErrorStream());

		if (exitVal != 0) {
			System.out.println("Exited with error code " + exitVal);
		}
	}

	private void outputResults(PrintStream out, InputStream inputStream) throws IOException {
		BufferedReader standard_out = new BufferedReader(new InputStreamReader(
				inputStream));

		String line = null;
		while ((line = standard_out.readLine()) != null) {
			out.println(line);
			out.flush();
		}
	}

	private String[] makeNewEnvironment() {
		Map<String, String> env = System.getenv();
		int newEnvLength = env.size() + 1;
		if (env.containsKey(environmentVariableName)) {
			print_error_and_quit("Environment already contains variable " + environmentVariableName);
		}
		String[] newEnv = new String[newEnvLength];
		newEnv[0] = environmentVariableName + "=" + decryptedPassword;
		int count = 1;
		for (String envName : env.keySet()) {
			newEnv[count] = envName + "=" + env.get(envName);
			count += 1;
		}
		return newEnv;
	}
}
