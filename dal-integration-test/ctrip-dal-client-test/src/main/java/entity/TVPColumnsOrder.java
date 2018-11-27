package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

import java.sql.Timestamp;
import java.sql.Types;

import com.ctrip.platform.dal.dao.DalPojo;

/**
 * @author llj李龙姣
 * @date 2018-11-02
 */
@Entity
@Database(name = "noShardTestOnSqlServer")
@Table(name = "TVP_Columns_Order")
public class TVPColumnsOrder implements DalPojo {

    @Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value = Types.INTEGER)
	private Integer iD;

	@Column(name = "column_test")
	@Type(value = Types.NCHAR)
	private String columnTest;

	@Column(name = "column1")
	@Type(value = Types.NCHAR)
	private String column1;

	@Column(name = "column2")
	@Type(value = Types.NCHAR)
	private String column2;

	@Column(name = "column3")
	@Type(value = Types.NCHAR)
	private String column3;

	@Column(name = "DataChange_LastTime")
	@Type(value = Types.TIMESTAMP)
	private Timestamp datachangeLasttime;

	public Integer getID() {
		return iD;
	}

	public void setID(Integer iD) {
		this.iD = iD;
	}

	public String getColumnTest() {
		return columnTest;
	}

	public void setColumnTest(String columnTest) {
		this.columnTest = columnTest;
	}

	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public String getColumn2() {
		return column2;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}

	public String getColumn3() {
		return column3;
	}

	public void setColumn3(String column3) {
		this.column3 = column3;
	}

	public Timestamp getDatachangeLasttime() {
		return datachangeLasttime;
	}

	public void setDatachangeLasttime(Timestamp datachangeLasttime) {
		this.datachangeLasttime = datachangeLasttime;
	}
}