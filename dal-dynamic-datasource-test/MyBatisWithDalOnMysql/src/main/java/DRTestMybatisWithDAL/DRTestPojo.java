package DRTestMybatisWithDAL;

import com.ctrip.platform.dal.dao.UpdatableEntity;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name="noShardTestOnMysql")
@Table(name="testTable")
public class DRTestPojo extends UpdatableEntity {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value= Types.INTEGER)
	private Integer iD;


	@Column(name="Name")
	@Type(value= Types.VARCHAR)
	private String name;

	@Column(name="Age")
	@Type(value= Types.INTEGER)
	private Integer age;

	@Column(name="Birth")
	@Type(value= Types.TIMESTAMP)
	private Timestamp birth;

	public Integer getID() {
		return iD;
	}

	public void setID(Integer iD) {
		update("ID");
		this.iD = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		update("Name");
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		update("Age");
		this.age = age;
	}

	public Timestamp getBirth() {
		return birth;
	}

	public void setBirth(Timestamp birth) {
		update("Birth");
		this.birth = birth;
	}

}