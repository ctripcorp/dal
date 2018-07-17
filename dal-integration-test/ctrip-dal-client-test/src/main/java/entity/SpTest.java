package entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name="noShardTestOnSqlServer")
@Table(name="spTest")
public class SpTest implements DalPojo {

//	@Column(name="column3")
//	@Type(value=Types.DECIMAL)
//	private BigDecimal column3;

	@Column(name="column4")
	@Type(value=Types.TIMESTAMP)
	private Timestamp column4;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value=Types.INTEGER)
	private Integer iD;

	@Column(name="Column1")
	@Type(value=Types.VARCHAR)
	private String column1;

	@Column(name="column2")
	@Type(value=Types.INTEGER)
	private Integer column2;

//	@Column(name="addColumn")
//	@Type(value=Types.INTEGER)
//	private Integer addColumn;





	public Integer getID() {
		return iD;
	}

	public void setID(Integer iD) {
		this.iD = iD;
	}

	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public Integer getColumn2() {
		return column2;
	}

	public void setColumn2(Integer column2) {
		this.column2 = column2;
	}

//	public Integer getaddColumn() {
//		return addColumn;
//	}
//
//	public void setaddColumn(Integer addColumn) {
//		this.addColumn = addColumn;
//	}

//	public BigDecimal getColumn3() {
//		return column3;
//	}
//
//	public void setColumn3(BigDecimal column3) {
//		this.column3 = column3;
//	}

	public Timestamp getColumn4() {
		return column4;
	}

	public void setColumn4(Timestamp column4) {
		this.column4 = column4;
	}

}