package com.ctrip.platform.dal.dao.datasource.read.param;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class BlobParamContext extends ParamContext {

	private static final long serialVersionUID = 4357790552909332479L;

	/**
	 * @param index
	 * @param values
	 */
	public BlobParamContext(int index, Object[] values) {
		super(index, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void setParam(PreparedStatement stmt) throws SQLException {
		if (values.length == 1 && values[0] instanceof Blob) {
			stmt.setBlob(index, (Blob) values[0]);
		} else if (values.length == 1 && values[0] instanceof InputStream) {
			stmt.setBlob(index, (InputStream) values[0]);
		} else if (values.length == 2) {
			stmt.setBlob(index, (InputStream) values[0], (Integer) values[1]);
		}
	}
}
