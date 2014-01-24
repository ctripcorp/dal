package com.ctrip.platform.dal.service.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHookManager {
	private static final Logger logger = LoggerFactory
			.getLogger(ShutdownHookManager.class);
	private static final ShutdownHookManager MGR = new ShutdownHookManager();
	private final Set<HookEntry> hooks = Collections
			.synchronizedSet(new HashSet<HookEntry>());
	private AtomicBoolean shutdownInProgress = new AtomicBoolean(false);

	public enum Priority {
		HIGH(9), MIDDLE(8), LOW(1);
		private int priority;

		Priority(int priority) {
			this.priority = priority;
		}

		public int getPriority() {
			return priority;
		}
	}

	// private to constructor to ensure singularity
	private ShutdownHookManager() {
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				MGR.shutdownInProgress.set(true);
				for (Runnable hook : MGR.getShutdownHooksInOrder()) {
					try {
						hook.run();
					} catch (Throwable ex) {
						logger.warn("ShutdownHook '"
								+ hook.getClass().getSimpleName()
								+ "' failed, " + ex.toString(), ex);
					}
				}
			}
		});
	}

	/**
	 * Return ShutdownHookManager singleton.
	 * 
	 * @return ShutdownHookManager singleton.
	 */
	public static ShutdownHookManager get() {
		return MGR;
	}

	/**
	 * Private structure to store ShutdownHook and its priority.
	 */
	private static class HookEntry {
		Runnable hook;
		Priority priority;

		public HookEntry(Runnable hook, Priority priority) {
			this.hook = hook;
			this.priority = priority;
		}

		@Override
		public int hashCode() {
			return hook.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			boolean eq = false;
			if (obj != null) {
				if (obj instanceof HookEntry) {
					eq = (hook == ((HookEntry) obj).hook);
				}
			}
			return eq;
		}

	}

	public List<Runnable> getShutdownHooksInOrder() {
		List<HookEntry> list;
		synchronized (MGR.hooks) {
			list = new ArrayList<HookEntry>(MGR.hooks);
		}
		Collections.sort(list, new Comparator<HookEntry>() {
			@Override
			public int compare(HookEntry o1, HookEntry o2) {
				return o2.priority.getPriority() - o1.priority.getPriority();
			}
		});
		List<Runnable> ordered = new ArrayList<Runnable>();
		for (HookEntry entry : list) {
			ordered.add(entry.hook);
		}
		return ordered;
	}

	public void addShutdownHook(Runnable shutdownHook, Priority priority) {
		if (shutdownHook == null) {
			throw new IllegalArgumentException("shutdownHook cannot be NULL");
		}
		if (shutdownInProgress.get()) {
			throw new IllegalStateException(
					"Shutdown in progress, cannot add a shutdownHook");
		}
		hooks.add(new HookEntry(shutdownHook, priority));
	}

	public boolean removeShutdownHook(Runnable shutdownHook, Priority priority) {
		if (shutdownInProgress.get()) {
			throw new IllegalStateException(
					"Shutdown in progress, cannot remove a shutdownHook");
		}
		return hooks.remove(new HookEntry(shutdownHook, priority));
	}

	public boolean hasShutdownHook(Runnable shutdownHook, Priority priority) {
		return hooks.contains(new HookEntry(shutdownHook, priority));
	}

	public boolean isShutdownInProgress() {
		return shutdownInProgress.get();
	}

	public void removeAllShutdownHook() {
		hooks.clear();
	}
}
