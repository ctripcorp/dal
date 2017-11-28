package com.ctrip.framework.dal.mysql.test.service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.time.FastDateFormat;

import com.ctrip.framework.dal.mysql.test.util.ByteUtil;
import com.ctrip.framework.dal.mysql.test.util.MachineUtil;
import com.google.common.base.Joiner;

public class UniqueKeyGenerator {

  private static final FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");
  private static final AtomicInteger counter = new AtomicInteger(new SecureRandom().nextInt());
  private static final Joiner KEY_JOINER = Joiner.on("-");

  public static String generate() {
    String hexIdString =
        ByteUtil.toHexString(toByteArray(MachineUtil.getMachineIdentifier(), counter.incrementAndGet()));

    return KEY_JOINER.join(TIMESTAMP_FORMAT.format(new Date()), hexIdString);

  }

  protected static byte[] toByteArray(int machineIdentifier, int counter) {
    byte[] bytes = new byte[8];
    bytes[0] = ByteUtil.int3(machineIdentifier);
    bytes[1] = ByteUtil.int2(machineIdentifier);
    bytes[2] = ByteUtil.int1(machineIdentifier);
    bytes[3] = ByteUtil.int0(machineIdentifier);
    bytes[4] = ByteUtil.int3(counter);
    bytes[5] = ByteUtil.int2(counter);
    bytes[6] = ByteUtil.int1(counter);
    bytes[7] = ByteUtil.int0(counter);
    return bytes;
  }


}
