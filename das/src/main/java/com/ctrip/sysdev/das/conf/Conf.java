package com.ctrip.sysdev.das.conf;

import com.ctrip.sysdev.das.utils.Configuration;

public class Conf {
	public static void initConfiguration() {
		Configuration.addResource("conf.properties");
	}
}
