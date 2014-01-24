package com.ctrip.sysdev.das.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBeanUtil {
	private static Logger logger = LoggerFactory.getLogger(MBeanUtil.class);

	/**
	 * Register the MBean using our standard MBeanName format
	 * "hadoop:service=<serviceName>,name=<nameName>" Where the <serviceName>
	 * and <nameName> are the supplied parameters
	 * 
	 * @param serviceName
	 * @param nameName
	 * @param theMbean
	 *            - the MBean to register
	 * @return the named used to register the MBean
	 */
	public static ObjectName registerMBean(final String serviceName,
			final String nameName, final Object theMbean) {
		final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = getMBeanName(serviceName, nameName);
		try {
			mbs.registerMBean(theMbean, name);
			return name;
		} catch (InstanceAlreadyExistsException ie) {
			// Ignore if instance already exists
		} catch (Exception e) {
			logger.error("register MBean meet error", e);
		}
		return null;
	}

	static public void unregisterMBean(ObjectName mbeanName) {
		final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		if (mbeanName == null)
			return;
		try {
			mbs.unregisterMBean(mbeanName);
		} catch (InstanceNotFoundException e) {
			// ignore
		} catch (Exception e) {
			logger.error("unregister MBean meet error", e);
		}
	}

	static public ObjectName getMBeanName(final String serviceName,
			final String nameName) {
		ObjectName name = null;
		try {
			name = new ObjectName("freeway:" + "service=" + serviceName
					+ ",name=" + nameName);
		} catch (MalformedObjectNameException e) {
			logger.error("get ObjectName meet error", e);
		}
		return name;
	}
}
