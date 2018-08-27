package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.CtripServerConfig;
import com.ctrip.framework.idgen.server.config.ServerConfig;
import com.ctrip.platform.idgen.service.api.IdSegment;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;



/**
 * Created by lilj on 2018/8/22.
 */
public class SnowflakeWorkerTest {
    private static ServerConfig serverConfig = new CtripServerConfig();

    static {
        serverConfig.importConfig(getMap());
    }

    @Test
    public void generateIdPoolOrdinaryTest() throws Exception {
        SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        List<IdSegment> idSegmentList = worker.generateIdPool(200, 2);
        assertEquals(1, idSegmentList.size());
        for (IdSegment idSegment : idSegmentList) {
            assertEquals(200, (idSegment.getEnd().longValue() - idSegment.getStart().longValue() + 1L));
        }
    }

    @Test
    public void generateIdPoolOverMaxRequireSizeTest() throws Exception {
        SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        List<IdSegment> idSegmentList = worker.generateIdPool(10000, 2);
        assertEquals(1, idSegmentList.size());
        for (IdSegment idSegment : idSegmentList) {
            System.out.println(idSegment.getStart() + "," + idSegment.getEnd());
            assertEquals(5000, (idSegment.getEnd().longValue() - idSegment.getStart().longValue() + 1L));
        }
    }

    //        ！fallbackLocked and sequenceStart not overflowed
    @Test
    public void generateIdPoolBackwardsTest1() throws Exception {
        SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        worker.setLastTimestamp(System.currentTimeMillis() + 10000);
        worker.setFallbackLocked(false);
//        sequenceStart = (sequence + 1) & config.getSequenceMask()=65501
        worker.setSequence(65500);

        List<IdSegment> idSegmentList = worker.generateIdPool(1000, 2);
        assertEquals(1, idSegmentList.size());
        for (IdSegment idSegment : idSegmentList) {
            assertEquals(35, (idSegment.getEnd().longValue() - idSegment.getStart().longValue() + 1L));
        }
    }

    //        ！fallbackLocked and sequenceStart overflowed
    @Test
    public void generateIdPoolBackwardsTest2() throws Exception {
        SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        worker.setLastTimestamp(System.currentTimeMillis() + 10000);
        worker.setFallbackLocked(false);
//        sequenceStart = (sequence + 1) & config.getSequenceMask()=0
        worker.setSequence(65535);
        try {
            List<IdSegment> idSegmentList = worker.generateIdPool(1000, 2);
            fail();
        } catch (Exception e) {
            assertEquals("Time fallback locked", e.getMessage());
        }
    }

    //        fallbackLocked
    @Test
    public void generateIdPoolBackwardsTest3() throws Exception {
        SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        worker.setLastTimestamp(System.currentTimeMillis() + 10000);
        worker.setFallbackLocked(true);
        try {
            List<IdSegment> idSegmentList = worker.generateIdPool(1000, 2);
            fail();
        } catch (Exception e) {
            assertEquals("Time fallback locked", e.getMessage());
        }
    }

    //    timestamp==LastTimestamp, !fallBackLocked, sequenceStart not overFlowed
    @Test
    public void generateIdPoolAtLastTimestampTest1() throws Exception {
        SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        worker.setLastTimestamp(System.currentTimeMillis());
        worker.setFallbackLocked(false);
//        sequenceStart = (sequence + 1) & config.getSequenceMask()=65501
        worker.setSequence(65500);
        List<IdSegment> idSegmentList = worker.generateIdPool(1000, 2);
        assertEquals(2, idSegmentList.size());
        assertEquals(35, (idSegmentList.get(0).getEnd().longValue() - idSegmentList.get(0).getStart().longValue() + 1L));
        assertEquals(965, (idSegmentList.get(1).getEnd().longValue() - idSegmentList.get(1).getStart().longValue() + 1L));
    }

    //    timestamp==LastTimestamp, !fallBackLocked, sequenceStart overFlowed
    @Test
    public void generateIdPoolAtLastTimestampTest2() throws Exception {
        SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        worker.setLastTimestamp(System.currentTimeMillis());
        worker.setFallbackLocked(false);
//        sequenceStart = (sequence + 1) & config.getSequenceMask()=0
        worker.setSequence(65535);
        List<IdSegment> idSegmentList = worker.generateIdPool(1000, 2);
        assertEquals(1, idSegmentList.size());
        assertEquals(1000, (idSegmentList.get(0).getEnd().longValue() - idSegmentList.get(0).getStart().longValue() + 1L));
    }

    //    timestamp==LastTimestamp, fallBackLocked
    @Test
    public void generateIdPoolAtLastTimestampTest3() throws Exception {
        SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        worker.setLastTimestamp(System.currentTimeMillis());
        worker.setFallbackLocked(true);

        List<IdSegment> idSegmentList = worker.generateIdPool(1000, 2);
        assertEquals(1, idSegmentList.size());
        assertEquals(1000, (idSegmentList.get(0).getEnd().longValue() - idSegmentList.get(0).getStart().longValue() + 1L));
    }

    @Test
    public void generateIdPoolConcurrentTest() throws Exception {
        final List<IdSegment> idSegments = Collections.synchronizedList(new LinkedList<IdSegment>());

        int threadCount = 500;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final SnowflakeWorker worker = new SnowflakeWorker("test1", serverConfig);
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<IdSegment> idSegments1=worker.generateIdPool(2000, 1000);
//                        long count=0;
//                        for(IdSegment idSegment:idSegments1){
//                            count+= (idSegment.getEnd().longValue()-idSegment.getStart().longValue()+1L);
//                        }
//                        if(idSegments1.size()==1)
//                            System.out.println(idSegments1.size()+", count: "+count+", segment0 start: "+idSegments1.get(0).getStart().longValue()+
//                                    ", segment0 end: "+idSegments1.get(0).getEnd().longValue());
//                        else
//                        System.out.println(idSegments1.size()+", count: "+count+", segment0 start: "+idSegments1.get(0).getStart().longValue()+
//                                                                                 ", segment0 end: "+idSegments1.get(0).getEnd().longValue()+
//                                        ", segment1 start: "+idSegments1.get(1).getStart().longValue()+
//                                        ", segment1 end: "+idSegments1.get(1).getEnd().longValue());
                        idSegments.addAll(idSegments1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }
        latch.await();

//        check count
        List<Long> ids = new LinkedList<>();
        for (IdSegment idSegment : idSegments) {
            for (long i = idSegment.getStart().longValue(); i <= idSegment.getEnd().longValue(); i++) {
                ids.add(i);
            }
        }
        System.out.println(ids.size());
//        assertEquals(threadCount * 2000, ids.size());

//        check duplicate
        Set<Long> set = new TreeSet<>();
        set.addAll(ids);
        assertEquals(ids.size(), set.size());
    }

    @Test
    public void generateIdSegmentsTest() throws Exception {
        ServerConfig serverConfig = new CtripServerConfig();
        serverConfig.importConfig(getMap());

        Class<?> refWorker = Class.forName("com.ctrip.framework.idgen.server.service.SnowflakeWorker");
        Constructor constructor = refWorker.getConstructor(new Class[]{String.class, ServerConfig.class});

        SnowflakeWorker worker = (SnowflakeWorker) constructor.newInstance("test1", serverConfig);

        Method generateIdSegments = refWorker.getDeclaredMethod("generateIdSegments", long.class, long.class, int.class, long.class, int.class, boolean.class);
        generateIdSegments.setAccessible(true);

//        cross millisecond and loop
        List<IdSegment> idSegmentList = (List<IdSegment>) generateIdSegments.invoke(worker, 65500, System.currentTimeMillis(), 1000, System.nanoTime(), 3, true);
        assertEquals(2, idSegmentList.size());

        assertEquals(36, (idSegmentList.get(0).getEnd().longValue() - idSegmentList.get(0).getStart().longValue() + 1L));
        assertEquals(964, (idSegmentList.get(1).getEnd().longValue() - idSegmentList.get(1).getStart().longValue() + 1L));

//        nonCross millisecond and loop
        List<IdSegment> idSegmentList2 = (List<IdSegment>) generateIdSegments.invoke(worker, 0, System.currentTimeMillis(), 1000, System.nanoTime(), 3, true);
        assertEquals(1,idSegmentList2.size());
        assertEquals(1000, (idSegmentList2.get(0).getEnd().longValue() - idSegmentList2.get(0).getStart().longValue() + 1L));

//        cross millisecond and nonloop
        List<IdSegment> idSegmentList3 = (List<IdSegment>) generateIdSegments.invoke(worker, 65500, System.currentTimeMillis(), 1000, System.nanoTime(), 3, false);
        assertEquals(1,idSegmentList3.size());
        assertEquals(36, (idSegmentList3.get(0).getEnd().longValue() - idSegmentList3.get(0).getStart().longValue() + 1L));

//        corss millisecond timeout
        List<IdSegment> idSegmentList4= (List<IdSegment>) generateIdSegments.invoke(worker, 65500, System.currentTimeMillis(), 1000, System.nanoTime()-3000000, 3, true);
        assertEquals(1,idSegmentList4.size());
        assertEquals(36, (idSegmentList4.get(0).getEnd().longValue() - idSegmentList4.get(0).getStart().longValue() + 1L));
    }

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put("workerId_10.32.21.121", "23");
        map.put("timestampBits", "40");
        map.put("sequenceBits", "16");
        map.put("sequenceInitRange", "200");
        map.put("dateReference", "2018-08-08 00:00:00");
        return map;
    }
}
