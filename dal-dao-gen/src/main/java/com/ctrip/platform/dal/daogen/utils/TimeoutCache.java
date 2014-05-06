package com.ctrip.platform.dal.daogen.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Timeout cache, current not that good
 * @author gawu
 *
 * @param <K>
 * @param <V>
 */
public class TimeoutCache<K, V> {

	private final ReadWriteLock _lock = new ReentrantReadWriteLock();
	private final Lock readLock = _lock.readLock();
	private final Lock writeLock = _lock.writeLock();

	private Map<K, V> _cache = new ConcurrentHashMap<K, V>();

	// holds the last time the cache has been refreshed in millis
	private volatile long lastRefreshDate = System.currentTimeMillis();

	// indicates that cache is currently refreshing entries
	private volatile boolean cacheCurrentlyRefreshing = false;

	// default 30 minutes
	private volatile long REFRESH_INTERVAL = 30 * 60 * 1000;

	public void put(K key, V value) {
		if (cacheNeedsRefresh()) {
			refresh();
		}

		writeLock.lock();
		try {
			_cache.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}

	public V get(K key) {
		if (cacheNeedsRefresh()) {
			refresh();
		}

		readLock.lock();
		try {
			return _cache.get(key);
		} finally {
			readLock.unlock();
		}
	}

	public V remove(K key) {
		if (cacheNeedsRefresh()) {
			refresh();
		}

		writeLock.lock();
		try {
			return _cache.remove(key);
		} finally {
			writeLock.unlock();
		}
	}

	public boolean containsKey(K key) {
		if (cacheNeedsRefresh()) {
			refresh();
		}

		readLock.lock();
		try {
			return _cache.containsKey(key);
		} finally {
			readLock.unlock();
		}
	}

	private boolean cacheNeedsRefresh() {
		// make sure that cache is not currently being refreshed by some
		// other thread.
		if (cacheCurrentlyRefreshing) {
			return false;
		}
		return (System.currentTimeMillis() - lastRefreshDate) >= REFRESH_INTERVAL;
	}

	private void refresh() {
		// make sure the cache did not start refreshing between
		// cacheNeedsRefresh()
		// and refresh() by some other thread.
		if (cacheCurrentlyRefreshing) {
			return;
		}

		// signal to other threads that cache is currently being refreshed.
		cacheCurrentlyRefreshing = true;

		try {
			// refresh your cache contents here
			writeLock.lock();
			try {
				_cache.clear();
			} finally {
				writeLock.unlock();
			}
		} finally {
			// set the lastRefreshDate and signal that cache has finished
			// refreshing to other threads.
			lastRefreshDate = System.currentTimeMillis();
			cacheCurrentlyRefreshing = false;
		}
	}

}
