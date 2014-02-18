package com.ctrip.platform.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;

public class TblresultsDalParser implements DalParser<Tblresults> {
	public static final String DATABASE_NAME = "SysDalTest";
	public static final String TABLE_NAME = "tblResults";
	private static final String[] COLUMNS = new String[]{
	$set($sepatate = ,
)
		"name"${sepatate}		"status"${sepatate}		"indid"${sepatate}		"OrigFillFactor"${sepatate}		"IndCol1"${sepatate}		"IndCol2"${sepatate}		"IndCol3"${sepatate}		"IndCol4"${sepatate}		"IndCol5"${sepatate}		"IndCol6"${sepatate}		"IndCol7"${sepatate}		"IndCol8"${sepatate}		"IndCol9"${sepatate}		"IndCol10"${sepatate}		"IndCol11"${sepatate}		"IndCol12"${sepatate}		"IndCol13"${sepatate}		"IndCol14"${sepatate}		"IndCol15"${sepatate}		"IndCol16"${sepatate}		"SegName"${sepatate}		"FullTextKey"${sepatate}		"Descending"${sepatate}		"Computed"${sepatate}		"IsTable"${sepatate}	};
	
	@Override
	public Tblresults map(ResultSet rs, int rowNum) throws SQLException {
		Tblresults pojo = new Tblresults;
		pojo.setname(rs.getString("name"));
		pojo.setstatus(rs.getInt("status"));
		pojo.setindid(rs.getInt("indid"));
		pojo.setOrigFillFactor(rs.getInt("OrigFillFactor"));
		pojo.setIndCol1(rs.getString("IndCol1"));
		pojo.setIndCol2(rs.getString("IndCol2"));
		pojo.setIndCol3(rs.getString("IndCol3"));
		pojo.setIndCol4(rs.getString("IndCol4"));
		pojo.setIndCol5(rs.getString("IndCol5"));
		pojo.setIndCol6(rs.getString("IndCol6"));
		pojo.setIndCol7(rs.getString("IndCol7"));
		pojo.setIndCol8(rs.getString("IndCol8"));
		pojo.setIndCol9(rs.getString("IndCol9"));
		pojo.setIndCol10(rs.getString("IndCol10"));
		pojo.setIndCol11(rs.getString("IndCol11"));
		pojo.setIndCol12(rs.getString("IndCol12"));
		pojo.setIndCol13(rs.getString("IndCol13"));
		pojo.setIndCol14(rs.getString("IndCol14"));
		pojo.setIndCol15(rs.getString("IndCol15"));
		pojo.setIndCol16(rs.getString("IndCol16"));
		pojo.setSegName(rs.getString("SegName"));
		pojo.setFullTextKey(rs.getInt("FullTextKey"));
		pojo.setDescending(rs.getInt("Descending"));
		pojo.setComputed(rs.getInt("Computed"));
		pojo.setIsTable(rs.getInt("IsTable"));
		return pojo;
	}

	@Override
	public String getDatabaseName() {
		return DATABASE_NAME;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String[] getColumnNames() {
		return COLUMNS;
	}

	@Override
	public Map<String, ?> getFields(Tblresults pojo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", pojo.getname());
		map.put("status", pojo.getstatus());
		map.put("indid", pojo.getindid());
		map.put("OrigFillFactor", pojo.getOrigFillFactor());
		map.put("IndCol1", pojo.getIndCol1());
		map.put("IndCol2", pojo.getIndCol2());
		map.put("IndCol3", pojo.getIndCol3());
		map.put("IndCol4", pojo.getIndCol4());
		map.put("IndCol5", pojo.getIndCol5());
		map.put("IndCol6", pojo.getIndCol6());
		map.put("IndCol7", pojo.getIndCol7());
		map.put("IndCol8", pojo.getIndCol8());
		map.put("IndCol9", pojo.getIndCol9());
		map.put("IndCol10", pojo.getIndCol10());
		map.put("IndCol11", pojo.getIndCol11());
		map.put("IndCol12", pojo.getIndCol12());
		map.put("IndCol13", pojo.getIndCol13());
		map.put("IndCol14", pojo.getIndCol14());
		map.put("IndCol15", pojo.getIndCol15());
		map.put("IndCol16", pojo.getIndCol16());
		map.put("SegName", pojo.getSegName());
		map.put("FullTextKey", pojo.getFullTextKey());
		map.put("Descending", pojo.getDescending());
		map.put("Computed", pojo.getComputed());
		map.put("IsTable", pojo.getIsTable());
		return map;
	}

	@Override
	public boolean hasIdentityColumn() {
		return false;
	}

	@Override
	public String getIdentityColumnName() {
		return null;
	}

	@Override
	public Number getIdentityValue(Tblresults pojo) {
		return null;
	}

	@Override
	public Map<String, ?> getPk(Tblresults pojo) {
		Map<String, Object> map = new HashMap<String, Object>();

		return map;
	}
}
