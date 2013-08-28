package com.ctrip.sysdev.das;

import com.ctrip.sysdev.das.factory.GuiceObjectFactory;
import com.google.common.util.concurrent.Service;

public class DalMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GuiceObjectFactory aGuiceObjectFactory = new GuiceObjectFactory();

		try {
			final Service server = aGuiceObjectFactory
					.getInstance(Service.class);
			server.start();

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					server.stopAndWait();
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
