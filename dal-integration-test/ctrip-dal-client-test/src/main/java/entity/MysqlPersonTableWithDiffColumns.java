package entity;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Types;

/**
 * Created by lilj on 2018/5/8.
 */
@Entity
@Database(name="ShardColModShardByDBOnMysql")
@Table(name="person")
public class MysqlPersonTableWithDiffColumns {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value= Types.INTEGER)
    private Integer iD;

    @Column(name="Name")
    @Type(value=Types.VARCHAR)
    private String name;

    @Column(name="Age")
    @Type(value=Types.INTEGER)
    private Integer age;

//	@Column(name="Birth")
//	@Type(value=Types.TIMESTAMP)
//	private Timestamp birth;

    @Column(name="IsCompany")
    @Type(value=Types.CHAR)
    private String isCompany;

    public String getIsCompany() {
        return isCompany;
    }

    public void setIsCompany(String isCompany) {
        this.isCompany = isCompany;
    }

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

//	public Timestamp getBirth() {
//		return birth;
//	}
//
//	public void setBirth(Timestamp birth) {
//		this.birth = birth;
//	}

}
