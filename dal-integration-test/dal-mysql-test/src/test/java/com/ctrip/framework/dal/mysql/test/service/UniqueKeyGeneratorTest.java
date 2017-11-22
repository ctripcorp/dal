package com.ctrip.framework.dal.mysql.test.service;

import static org.junit.Assert.*;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class UniqueKeyGeneratorTest {
  @Test
  public void testGenerateUniqueKey() throws Exception {
    int generateTimes = 50000;
    Set<String> uniqueKeys = Sets.newConcurrentHashSet();

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(1);

    executorService.submit(generateUniqueKeysTask(uniqueKeys, generateTimes, latch));
    executorService.submit(generateUniqueKeysTask(uniqueKeys, generateTimes, latch));

    latch.countDown();

    executorService.shutdown();
    executorService.awaitTermination(10, TimeUnit.SECONDS);

    // make sure keys are unique
    assertEquals(generateTimes * 2, uniqueKeys.size());
  }

  private Runnable generateUniqueKeysTask(final Set<String> releaseKeys, final int generateTimes,
      final CountDownLatch latch) {
    return new Runnable() {
      @Override
      public void run() {
        try {
          latch.await();
        } catch (InterruptedException e) {
          // ignore
        }
        for (int i = 0; i < generateTimes; i++) {
          releaseKeys.add(UniqueKeyGenerator.generate());
        }
      }
    };
  }

}
