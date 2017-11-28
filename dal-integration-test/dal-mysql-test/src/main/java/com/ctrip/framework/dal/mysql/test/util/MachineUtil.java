package com.ctrip.framework.dal.mysql.test.util;

import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class MachineUtil {
  private static final Logger logger = LoggerFactory.getLogger(MachineUtil.class);
  private static final int MACHINE_IDENTIFIER = createMachineIdentifier();

  public static int getMachineIdentifier() {
    return MACHINE_IDENTIFIER;
  }

  /**
   * Get the machine identifier from mac address
   *
   * @see <a href=https://github.com/mongodb/mongo-java-driver/blob/master/bson/src/main/org/bson/types/ObjectId.java>ObjectId.java</a>
   */
  private static int createMachineIdentifier() {
    // build a 2-byte machine piece based on NICs info
    int machinePiece;
    try {
      StringBuilder sb = new StringBuilder();
      Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

      if (e != null){
        while (e.hasMoreElements()) {
          NetworkInterface ni = e.nextElement();
          sb.append(ni.toString());
          byte[] mac = ni.getHardwareAddress();
          if (mac != null) {
            ByteBuffer bb = ByteBuffer.wrap(mac);
            try {
              sb.append(bb.getChar());
              sb.append(bb.getChar());
              sb.append(bb.getChar());
            } catch (BufferUnderflowException shortHardwareAddressException) { //NOPMD
              // mac with less than 6 bytes. continue
            }
          }
        }
      }

      machinePiece = sb.toString().hashCode();
    } catch (Throwable ex) {
      // exception sometimes happens with IBM JVM, use random
      machinePiece = (new SecureRandom().nextInt());
      logger.warn(
          "Failed to get machine identifier from network interface, using random number instead",
          ex);
    }
    return machinePiece;
  }
}
