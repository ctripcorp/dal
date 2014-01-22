package com.ctrip.platform.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.client.Client;


public class BaseQueryDao {
	private static final String SQL_FIND_BY_PK = "SELECT * FROM %s WHERE %s";
	private DirectClientFactory factory;
	private ResultSetVisitor rsVisitor;
	private PojoParser pojoParser;

	public BaseQueryDao(DirectClientFactory factory, PojoParser pojoParser,
			ResultSetVisitor rsVisitor) {
		this.factory = factory;
		this.pojoParser = pojoParser;
		this.rsVisitor = rsVisitor;
	}

	public DaoPojo selectByPk(DaoPojo pk, Map keywordParameters)
			throws SQLException {
		return selectFisrt(SQL_FIND_BY_PK, pojoParser.getPk(pk),
				keywordParameters);
	}

	public List<DaoPojo> selectAll(String sql,
			List<StatementParameter> parameters, Map keywordParameters)
			throws SQLException {
		Client client = factory.getClient();

		ResultSet rs = client.fetch(sql, parameters, keywordParameters);

		List<DaoPojo> pojoList = new ArrayList<DaoPojo>();
		while (rs.next()) {
			pojoList.add(rsVisitor.visit(rs));
		}
		client.closeConnection();
		return pojoList;
	}

	public DaoPojo selectFisrt(String sql, List<StatementParameter> parameters,
			Map keywordParameters) throws SQLException {
		DaoPojo pojo = null;
		Client client = factory.getClient();

		ResultSet rs = client.fetch(sql, parameters, keywordParameters);
		if (rs.next())
			pojo = rsVisitor.visit(rs);

		client.closeConnection();
		return pojo;
	}

	public List<DaoPojo> selectTop(String sql,
			List<StatementParameter> parameters, Map keywordParameters,
			int count) throws SQLException {
		Client client = factory.getClient();

		ResultSet rs = client.fetch(sql, parameters, keywordParameters);

		List<DaoPojo> pojoList = new ArrayList<DaoPojo>();
		int i = 0;
		while (i++ < count && rs.next()) {
			pojoList.add(rsVisitor.visit(rs));
		}
		client.closeConnection();
		return pojoList;
	}

	public List<DaoPojo> selectFrom(String sql,
			List<StatementParameter> parameters, Map keywordParameters,
			int start, int count) throws SQLException {
		Client client = factory.getClient();

		ResultSet rs = client.fetch(sql, parameters, keywordParameters);

		List<DaoPojo> pojoList = new ArrayList<DaoPojo>();
		rs.absolute(start - 1);
		int i = 0;
		while (i++ < count && rs.next()) {
			pojoList.add(rsVisitor.visit(rs));
		}
		client.closeConnection();
		return pojoList;
	}
}
