package com.ctrip.platform.dal.service.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ctrip.platform.dal.service.utils.ShutdownHookManager.Priority.MIDDLE;

public class SingleInstanceDaemonTool {
	private static SingleInstanceDaemonTool instance = null;
	private static String name;
	private static boolean needInit = false;

	public synchronized static SingleInstanceDaemonTool createInstance(
			String name) {
		if (instance == null) {
			instance = new SingleInstanceDaemonTool(name);
		}
		return instance;
	}

	public synchronized void init() throws IOException {
		if (!needInit) {
			PidFile pidFile = new PidFile();
			ShutdownHookManager.get().addShutdownHook(pidFile, MIDDLE);
			needInit = true;
		}
	}

	private SingleInstanceDaemonTool(String watcherName) {
		name = watcherName;
	}

	public static void bailout(int status) {
		System.exit(status);
	}

	private static class PidFile extends Thread {
		private final static Logger logger = LoggerFactory
				.getLogger(PidFile.class);
		private static FileLock lock = null;
		private static FileOutputStream pidFileOutput = null;
		private static String pidFileName;

		public PidFile() throws IOException {
			try {
				init();
			} catch (IOException e) {
				clean();
				throw e;
			}
		}

		public void init() throws IOException {
			String pidLong = ManagementFactory.getRuntimeMXBean().getName();
			String[] items = pidLong.split("@");
			String pid = items[0];
			String homePath = System.getenv("WORK_HOME");
			if (homePath == null) {
				homePath = ".";
			}
			if (!homePath.endsWith("/")) {
				homePath = homePath + File.separator;
			}
			StringBuilder pidFilePath = new StringBuilder();
			pidFilePath.append(homePath).append(name).append(".pid");
			pidFileName = pidFilePath.toString();
			try {
				File pidFile = new File(pidFileName);
				pidFileOutput = new FileOutputStream(pidFile);
				pidFileOutput.write(pid.getBytes());
				pidFileOutput.flush();
				FileChannel channel = pidFileOutput.getChannel();
				PidFile.lock = channel.tryLock();
				if (PidFile.lock != null) {
					logger.debug("Initial pid file succeeded...");
				} else {
					throw (new IOException());
				}
			} catch (IOException ex) {
				logger.error("Initial pid file failed...", ex);
				throw ex;
			}
		}

		@Override
		public void run() {
			logger.info("Clean pid starting.....");
			clean();
		}

		private void clean() {
			File pidFile = new File(pidFileName);
			if (!pidFile.exists()) {
				logger.warn("Delete pid file, No such file or directory: "
						+ pidFileName);
			} else {
				try {
					lock.release();
					pidFileOutput.close();
				} catch (IOException e) {
					logger.error("Unable to release file lock: " + pidFileName);
				}
				if (!pidFile.delete()) {
					logger.warn("Delete pid file failed, " + pidFileName);
				}
			}
		}
	}
}
