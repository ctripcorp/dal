package entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name="")
@Table(name="")
public class TestAlias implements DalPojo {

	@Column(name="myName")
	@Type(value=Types.VARCHAR)
	private String myName;

	@Column(name="num")
	@Type(value=Types.BIGINT)
	private Long num;

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

}