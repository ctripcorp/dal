package com.ctrip.framework.dal.mysql.test.util;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ByteUtil {
  private static final char[] HEX_CHARS = new char[] {
      '0', '1', '2', '3', '4', '5', '6', '7',
      '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  public static byte int3(final int x) {
    return (byte) (x >> 24);
  }

  public static byte int2(final int x) {
    return (byte) (x >> 16);
  }

  public static byte int1(final int x) {
    return (byte) (x >> 8);
  }

  public static byte int0(final int x) {
    return (byte) (x);
  }

  public static String toHexString(byte[] bytes) {
    char[] chars = new char[bytes.length * 2];
    int i = 0;
    for (byte b : bytes) {
      chars[i++] = HEX_CHARS[b >> 4 & 0xF];
      chars[i++] = HEX_CHARS[b & 0xF];
    }
    return new String(chars);
  }
}
