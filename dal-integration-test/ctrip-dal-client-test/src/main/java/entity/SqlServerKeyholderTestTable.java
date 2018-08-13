package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import java.sql.Types;
import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

/**
 * @author llj李龙姣
 * @date 2018-08-13
 */
@Entity
@Database(name = "noShardTestOnSqlServer")
@Table(name = "keyholdertest")
public class SqlServerKeyholderTestTable implements DalPojo {

    @Id
	@Column(name = "PeopleID")
	@Type(value = Types.BIGINT)
	private Long peopleID;

	@Column(name = "Name")
	@Type(value = Types.VARCHAR)
	private String name;

	@Column(name = "CityID")
	@Type(value = Types.INTEGER)
	private Integer cityID;

	@Column(name = "ProvinceID")
	@Type(value = Types.INTEGER)
	private Integer provinceID;

	@Column(name = "DataChange_LastTime")
	@Type(value = Types.TIMESTAMP)
	private Timestamp datachangeLasttime;

	public Long getPeopleID() {
		return peopleID;
	}

	public void setPeopleID(Long peopleID) {
		this.peopleID = peopleID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCityID() {
		return cityID;
	}

	public void setCityID(Integer cityID) {
		this.cityID = cityID;
	}

	public Integer getProvinceID() {
		return provinceID;
	}

	public void setProvinceID(Integer provinceID) {
		this.provinceID = provinceID;
	}

	public Timestamp getDatachangeLasttime() {
		return datachangeLasttime;
	}

	public void setDatachangeLasttime(Timestamp datachangeLasttime) {
		this.datachangeLasttime = datachangeLasttime;
	}

}