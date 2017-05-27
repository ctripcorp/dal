package com.ctrip.platform.dal.dao.helper;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;

public interface HintsAwareExtractor<T> {
    DalResultSetExtractor<T> extractWith(DalHints hints) throws SQLException;
}
