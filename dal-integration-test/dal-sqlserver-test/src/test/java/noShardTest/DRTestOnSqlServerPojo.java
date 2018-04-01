package noShardTest;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Types;

@Entity
@Database(name="noShardTestOnSqlServer")
@Table(name="testTable")
public class DRTestOnSqlServerPojo implements DalPojo {

	@Id
	@Column(name="PeopleID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value=Types.BIGINT)
	private Long peopleID;

	@Column(name="Name")
	@Type(value=Types.VARCHAR)
	private String name;

	@Column(name="CityID")
	@Type(value=Types.INTEGER)
	private Integer cityID;

	@Column(name="ProvinceID")
	@Type(value=Types.INTEGER)
	private Integer provinceID;

	@Column(name="CountryID")
	@Type(value=Types.INTEGER)
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