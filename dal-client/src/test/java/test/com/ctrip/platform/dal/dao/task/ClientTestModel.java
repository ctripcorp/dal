package test.com.ctrip.platform.dal.dao.task;

import java.sql.Timestamp;

public class ClientTestModel {
	private Integer id;
	private Integer quantity;
	private Integer dbIndex;
	private Integer tableIndex;
	private Short type;
	private String address;
	private Timestamp lastChanged;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getDbIndex() {
		return dbIndex;
	}

	public void setDbIndex(Integer dbIndex) {
		this.dbIndex = dbIndex;
	}

	public Integer getTableIndex() {
		return tableIndex;
	}

	public void setTableIndex(Integer tableIndex) {
		this.tableIndex = tableIndex;
	}
	
	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Timestamp getLastChanged() {
		return lastChanged;
	}

	public void setLastChanged(Timestamp lastChanged) {
		this.lastChanged = lastChanged;
	}
}
