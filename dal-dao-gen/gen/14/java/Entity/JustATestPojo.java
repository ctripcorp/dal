package DAL;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class JustATestPojo implements DalPojo {
	private Integer iD;
	private String name;
	private Integer age;
	private Timestamp birth;

	public Integer getID() {
		return iD;
	}

	public void setID(Integer iD) {
		this.iD = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Timestamp getBirth() {
		return birth;
	}

	public void setBirth(Timestamp birth) {
		this.birth = birth;
	}

}