package SimpleShardByDBOnSqlserver;

import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;

@Entity
@Database(name="SimpleShardByDBOnSqlserver")
@Table(name="People")
public class PeoplePeopleGenSimpleShardBySqlServerLlj implements DalPojo {
	
	@Id
	@Column(name="PeopleID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value=Types.BIGINT)
	@Sensitive(value=true)
	private Long peopleID;
	
	@Column(name="Name")
	@Type(value=Types.VARCHAR)
	@Sensitive(value=true)
	private String name;
	
	@Column(name="CityID")
	@Type(value=Types.INTEGER)
	@Sensitive(value=true)
	private Integer cityID;
	
	@Column(name="ProvinceID")
	@Type(value=Types.INTEGER)
	@Sensitive(value=true)
	private Integer provinceID;
	
	@Column(name="CountryID")
	@Type(value=Types.INTEGER)
	@Sensitive(value=true)
	private Integer countryID;

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

	public Integer getCountryID() {
		return countryID;
	}

	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
	}

}