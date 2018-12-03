package com.ctrip.datasource.util;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DalEncrypterTest {
  private String someEncryptionKey = LoggerAdapter.DEFAULT_SECRET_KEY;
  private DalEncrypter dalEncrypter;

  @Before
  public void setUp() throws Exception {
    dalEncrypter = new DalEncrypter(someEncryptionKey);
  }

  @Test
  public void desEncrypt() throws Exception {
    assertEquals("f9cupnx8Tuw/a9M+6ti8lboIBKLUr4nS", dalEncrypter.desEncrypt("offset=0,limit=10"));
    assertEquals("Nci9Qj6cUzE=", dalEncrypter.desEncrypt("id=7"));
  }

  @Test
  public void desDecrypt() throws Exception {
    assertEquals("offset=0,limit=10", dalEncrypter.desDecrypt("f9cupnx8Tuw/a9M+6ti8lboIBKLUr4nS"));
    assertEquals("id=7", dalEncrypter.desDecrypt("Nci9Qj6cUzE="));
  }

}
