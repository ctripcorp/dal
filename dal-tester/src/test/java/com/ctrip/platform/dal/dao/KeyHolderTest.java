package com.ctrip.platform.dal.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class KeyHolderTest {

	private static final String KEY = "key";
	private Map<String, Object> buildKey(int value) {
		Map<String, Object> key = new HashMap<>();
		key.put(KEY, value);
		return key;
	}
	
	@Test
	public void testSize() {
		KeyHolder test = new KeyHolder();
		assertEquals(0, test.size());
		test.getKeyList().add(buildKey(1));
		assertEquals(1, test.size());
	}

	@Test
	public void testGetKey() {
		KeyHolder test = new KeyHolder();
		test.getKeyList().add(buildKey(1));
		try {
			assertEquals(1, test.getKey().longValue());
		} catch (SQLException e) {
			fail();
		}
		try {
			test.getKeyList().add(buildKey(1));
			assertEquals(2, test.getKey().longValue());
			fail();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testGetKeyInt() {
		KeyHolder test = new KeyHolder();
		test.getKeyList().add(buildKey(1));
		try {
			assertEquals(1, test.getKey(0).longValue());
			test.getKeyList().add(buildKey(10));
			assertEquals(10, test.getKey(1).longValue());
		} catch (SQLException e) {
			fail();
		}
	}

	@Test
	public void testGetKeys() {
		KeyHolder test = new KeyHolder();
		test.getKeyList().add(buildKey(1));
		try {
			assertEquals(1, test.getKeys().get(KEY));
		} catch (SQLException e) {
			fail();
		}
		try {
			test.getKeyList().add(buildKey(1));
			test.getKeys();
			fail();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testGetKeyList() {
		KeyHolder test = new KeyHolder();
		test.getKeyList().add(buildKey(1));
		assertEquals(1, test.getKeyList().size());
		test.getKeyList().add(buildKey(1));
		assertEquals(2, test.getKeyList().size());
	}

	@Test
	public void testGetIdList() {
		KeyHolder test = new KeyHolder();
		test.getKeyList().add(buildKey(1));
		try {
			assertEquals(1, test.getIdList().size());
			test.getKeyList().add(buildKey(10));
			assertEquals(2, test.getIdList().size());
			
			assertEquals(1, test.getIdList().get(0));
			assertEquals(10, test.getIdList().get(1));
		} catch (SQLException e) {
			fail();
		}
	}

	@Test
	public void testAddPatial() {
		KeyHolder test = new KeyHolder();
		KeyHolder tmpHolder = new KeyHolder();
		tmpHolder.getKeyList().add(buildKey(0));
		tmpHolder.getKeyList().add(buildKey(1));
		tmpHolder.getKeyList().add(buildKey(2));
		test.addPatial(new Integer[]{0, 1, 2}, tmpHolder);
		
		tmpHolder = new KeyHolder();
		tmpHolder.getKeyList().add(buildKey(3));
		tmpHolder.getKeyList().add(buildKey(4));
		tmpHolder.getKeyList().add(buildKey(5));
		test.addPatial(new Integer[]{3, 4, 5}, tmpHolder);
	}

	@Test
	public void testMerge() {
		ExecutorService service = null;
		
		service = new ThreadPoolExecutor(5, 50, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		KeyHolder test = new KeyHolder();
		
		
		KeyHolder tmpHolder = new KeyHolder();
		tmpHolder.getKeyList().add(buildKey(0));
		tmpHolder.getKeyList().add(buildKey(1));
		tmpHolder.getKeyList().add(buildKey(2));
		test.addPatial(new Integer[]{0, 1, 2}, tmpHolder);
		
		tmpHolder = new KeyHolder();
		tmpHolder.getKeyList().add(buildKey(3));
		tmpHolder.getKeyList().add(buildKey(4));
		tmpHolder.getKeyList().add(buildKey(5));
		test.addPatial(new Integer[]{3, 4, 5}, tmpHolder);
		
		test.merge();
		
		test = new KeyHolder();
		try {
			int i = 0;
			for(Number value: test.getIdList()){
				assertEquals(i++, value);
			}
		} catch (SQLException e) {
			fail();
		}
		
		List<Future<Boolean>> fList = new ArrayList<>();
		for(int i = 0; i < 10; i++){
			fList.add(service.submit(new KeyHolderTask(test, new Integer[]{i*3, i*3 + 1, i*3 + 2})));
		}
		
		for(Future<Boolean> f: fList)
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				fail();
			}
		
		test.merge();
		try {
			List<Number> ids = test.getIdList();
			for(int i = 0; i < 30; i++)
				assertEquals(i, ids.get(i));
		} catch (SQLException e) {
			fail();
		}
		service.shutdown();
	}
	
	private class KeyHolderTask implements Callable<Boolean>{
		private KeyHolder kh;
		private Integer[] index;
		
		private KeyHolderTask(KeyHolder kh, Integer[] index) {
			this.kh = kh;
			this.index = index;
		}
		
		@Override
		public Boolean call() {
			KeyHolder tmpKH = new KeyHolder();
			for(Integer i: index)
				tmpKH.getKeyList().add(buildKey(i));
			kh.addPatial(index, tmpKH);
			
			return true;
		}
	}
}
