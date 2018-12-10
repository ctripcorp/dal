package IDAutoGenerator;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * @author llj李龙姣
 * @date 2018-10-15
 */
@Entity
@Database(name = "noShardTestOnMysql")
@Table(name = "bigintidtable")
public class TableWithBigintIdentity implements DalPojo {

    @Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value = Types.BIGINT)
	private BigInteger iD;

	@Column(name = "Name")
	@Type(value = Types.VARCHAR)
	private String name;

	@Column(name = "Age")
	@Type(value = Types.INTEGER)
	private Integer age;

	@Column(name = "Birth")
	@Type(value = Types.TIMESTAMP)
	private Timestamp birth;

	public BigInteger getID() {
		return iD;
	}

	public void setID(BigInteger iD) {
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