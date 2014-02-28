package com.ctrip.platform.tools;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class SDP_SH_PriceBatch_genDao {
	private DalTableDao<SDP_SH_PriceBatch_gen> client = new DalTableDao<SDP_SH_PriceBatch_gen>(new SDP_SH_PriceBatch_genParser());

	public SDP_SH_PriceBatch_gen queryByPk(Number id, DalHints hints)
			throws SQLException {
		return client.queryByPk(id, hints);
	}

	public SDP_SH_PriceBatch_gen queryByPk(SDP_SH_PriceBatch_gen pk, DalHints hints)
			throws SQLException {
		return client.queryByPk(pk, hints);
	}
	
	public List<SDP_SH_PriceBatch_gen> queryByPage(SDP_SH_PriceBatch_gen pk, int pageSize, int pageNo, DalHints hints)
			throws SQLException {
		// TODO to be implemented
		return null;
	}
	
	public void insert(DalHints hints, SDP_SH_PriceBatch_gen...daoPojos) throws SQLException {
		client.insert(hints, null, daoPojos);
	}

	public void insert(DalHints hints, KeyHolder keyHolder, SDP_SH_PriceBatch_gen...daoPojos) throws SQLException {
		client.insert(hints, keyHolder, daoPojos);
	}
	
	public void delete(DalHints hints, SDP_SH_PriceBatch_gen...daoPojos) throws SQLException {
		client.delete(hints, daoPojos);
	}
	
	public void update(DalHints hints, SDP_SH_PriceBatch_gen...daoPojos) throws SQLException {
		client.update(hints, daoPojos);
	}


	private static class SDP_SH_PriceBatch_genParser implements DalParser<SDP_SH_PriceBatch_gen> {
		public static final String DATABASE_NAME = "AssembleDB";
		public static final String TABLE_NAME = "SDP_SH_PriceBatch";
		private static final String[] COLUMNS = new String[]{
			"ID",
			"ProductID",
			"PackageID",
			"HotelID",
			"RoomID",
			"RoomPrice",
			"RoomPriceDate",
			"TicketID",
			"TicketPrice",
			"TicketPriceDate",
			"Version",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"ID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			-5,
			-5,
			-5,
			4,
			4,
			3,
			93,
			4,
			3,
			93,
			93,
		};
		
		@Override
		public SDP_SH_PriceBatch_gen map(ResultSet rs, int rowNum) throws SQLException {
			SDP_SH_PriceBatch_gen pojo = new SDP_SH_PriceBatch_gen();
			
			pojo.setID((Long)rs.getObject("ID"));
			pojo.setProductID((Long)rs.getObject("ProductID"));
			pojo.setPackageID((Long)rs.getObject("PackageID"));
			pojo.setHotelID((Integer)rs.getObject("HotelID"));
			pojo.setRoomID((Integer)rs.getObject("RoomID"));
			pojo.setRoomPrice((BigDecimal)rs.getObject("RoomPrice"));
			pojo.setRoomPriceDate((Timestamp)rs.getObject("RoomPriceDate"));
			pojo.setTicketID((Integer)rs.getObject("TicketID"));
			pojo.setTicketPrice((BigDecimal)rs.getObject("TicketPrice"));
			pojo.setTicketPriceDate((Timestamp)rs.getObject("TicketPriceDate"));
			pojo.setVersion((Timestamp)rs.getObject("Version"));
	
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
		public String[] getPrimaryKeyNames() {
			return PRIMARY_KEYS;
		}
		
		@Override
		public int[] getColumnTypes() {
			return COLUMN_TYPES;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(SDP_SH_PriceBatch_gen pojo) {
			return pojo.getID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(SDP_SH_PriceBatch_gen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ID", pojo.getID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(SDP_SH_PriceBatch_gen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ID", pojo.getID());
			map.put("ProductID", pojo.getProductID());
			map.put("PackageID", pojo.getPackageID());
			map.put("HotelID", pojo.getHotelID());
			map.put("RoomID", pojo.getRoomID());
			map.put("RoomPrice", pojo.getRoomPrice());
			map.put("RoomPriceDate", pojo.getRoomPriceDate());
			map.put("TicketID", pojo.getTicketID());
			map.put("TicketPrice", pojo.getTicketPrice());
			map.put("TicketPriceDate", pojo.getTicketPriceDate());
			map.put("Version", pojo.getVersion());
	
			return map;
		}
	}
}
