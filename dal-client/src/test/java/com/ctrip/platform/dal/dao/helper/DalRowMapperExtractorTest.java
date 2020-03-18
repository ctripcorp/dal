package com.ctrip.platform.dal.dao.helper;

import org.junit.Test;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public class DalRowMapperExtractorTest {

    @Test
    public void testOOMForLargeCount() throws SQLException {
        DalRowMapperExtractor<Integer> extractor = new DalRowMapperExtractor<>(new DalObjectRowMapper<>(Integer.class), 999999999);
        extractor.extract(new MockResultSet());
    }

}
