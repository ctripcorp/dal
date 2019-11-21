package entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name="dal_sharding_cluster")
@Table(name="strategy_meta")
public class StrategyMeta implements DalPojo {
	
	@Id
	@Column(name="id")
	@Type(value=Types.INTEGER)
	private Integer id;
	
	@Column(name="strategy_id")
	@Type(value=Types.INTEGER)
	private Integer sid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

}