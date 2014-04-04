package com.rcg.admin.runner;

import java.io.File;
import java.io.IOException;

public final class JavaProcess {

	private JavaProcess() {
	}

	public static int exec(Class<?> classObj) throws IOException, InterruptedException {
		String javaBin = (new StringBuilder(System.getProperty("java.home"))).append(File.separator).append("bin").append(File.separator).append("java").toString();
		String classpath = System.getProperty("java.class.path");
		String className = classObj.getCanonicalName();
		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
		Process process = builder.start();
		process.waitFor();
		return process.exitValue();
	}

}