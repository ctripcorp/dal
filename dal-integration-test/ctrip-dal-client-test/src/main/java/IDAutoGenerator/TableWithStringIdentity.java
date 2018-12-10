package IDAutoGenerator;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name="noShardTestOnMysql_3")
@Table(name="person")
public class TableWithStringIdentity implements DalPojo {

	@Column(name="ID")
	@Type(value=Types.BIGINT)
	private Long iD;

	@Id
	@Column(name="Name")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value=Types.VARCHAR)
	private String name;
	
	@Column(name="Age")
	@Type(value=Types.INTEGER)
	private Integer age;
	
	@Column(name="Birth")
	@Type(value=Types.TIMESTAMP)
	private Timestamp birth;

	public Long getID() {
		return iD;
	}

	public void setID(Long iD) {
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