package noShardTest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import com.ctrip.platform.dal.dao.annotation.Type;
import java.sql.Types;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import microsoft.sql.DateTimeOffset;

import com.ctrip.platform.dal.dao.DalPojo;

@Entity
@Database(name="noShardTestOnSqlServer")
@Table(name="All_Types")
public class AllTypesOnSqlServer implements DalPojo {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value=Types.INTEGER)
	private Integer iD;

	//switch index with varcharcol
	@Column(name="IntCol")
	@Type(value=Types.INTEGER)
	private Integer intCol;

	@Column(name="SmallIntCol")
	@Type(value=Types.SMALLINT)
	private Short smallIntCol;

	@Column(name="TinyIntCol")
	@Type(value=Types.TINYINT)
	private Short tinyIntCol;

	@Column(name="BigIntCol")
	@Type(value=Types.BIGINT)
	private Long bigIntCol;

	@Column(name="BitCol")
	@Type(value=Types.BIT)
	private Boolean bitCol;

	@Column(name="BinaryCol")
	@Type(value=Types.BINARY)
	private byte[] binaryCol;

	@Column(name="VarBinaryCol")
	@Type(value=Types.VARBINARY)
	private byte[] varBinaryCol;

	@Column(name="VarBinaryMaxCol")
	@Type(value=Types.VARBINARY)
	private byte[] varBinaryMaxCol;

	@Column(name="NumericCol")
	@Type(value=Types.NUMERIC)
	private BigDecimal numericCol;

	@Column(name="FloatCol")
	@Type(value=Types.DOUBLE)
	private Double floatCol;

	@Column(name="DecimalCol")
	@Type(value=Types.DECIMAL)
	private BigDecimal decimalCol;

	@Column(name="RealCol")
	@Type(value=Types.REAL)
	private Float realCol;

	@Column(name="CharCol")
	@Type(value=Types.CHAR)
	private String charCol;

	@Column(name="NcharCol")
	@Type(value=Types.NCHAR)
	private String ncharCol;

	@Column(name="NvarcharCol")
	@Type(value=Types.NVARCHAR)
	private String nvarcharCol;

	@Column(name="NvarcharMaxCol")
	@Type(value=Types.NVARCHAR)
	private String nvarcharMaxCol;

    
	//swicth index with intcol
	@Column(name="VarcharCol")
	@Type(value=Types.VARCHAR)
	private String varcharCol;

	@Column(name="VarcharmaxCol")
	@Type(value=Types.VARCHAR)
	private String varcharmaxCol;

	@Column(name="DateCol")
	@Type(value=Types.DATE)
	private Date dateCol;

	@Column(name="DatetimeCol")
	@Type(value=Types.TIMESTAMP)
	private Timestamp datetimeCol;

	@Column(name="Datetime2Col")
	@Type(value=Types.TIMESTAMP)
	private Timestamp datetime2Col;

	@Column(name="DatetimeOffsetCol")
	@Type(value=microsoft.sql.Types.DATETIMEOFFSET)
	private DateTimeOffset datetimeOffsetCol;

	@Column(name="SmallDatetimeCol")
	@Type(value=Types.TIMESTAMP)
	private Timestamp smallDatetimeCol;

	@Column(name="TimeCol")
	@Type(value=Types.TIME)
	private Time timeCol;

	@Column(name="TimestampCol")
	@Type(value=Types.BINARY)
	private byte[] timestampCol;

	@Column(name="GeographyCol")
	@Type(value=Types.VARBINARY)
	private byte[] geographyCol;

	@Column(name="ImageCol")
	@Type(value=Types.LONGVARBINARY)
	private byte[] imageCol;

	@Column(name="GeometryCol")
	@Type(value=Types.VARBINARY)
	private byte[] geometryCol;

	@Column(name="HierarchidCol")
	@Type(value=Types.VARBINARY)
	private byte[] hierarchidCol;

	@Column(name="MoneyCol")
	@Type(value=Types.DECIMAL)
	private BigDecimal moneyCol;

	@Column(name="SmallMoneyCol")
	@Type(value=Types.DECIMAL)
	private BigDecimal smallMoneyCol;

	@Column(name="NtextCol")
	@Type(value=Types.LONGNVARCHAR)
	private String ntextCol;

	@Column(name="TextCol")
	@Type(value=Types.LONGVARCHAR)
	private String textCol;

	@Column(name="UniqueidentifierCol")
	@Type(value=Types.CHAR)
	private String uniqueidentifierCol;

	@Column(name="XmlCol")
	@Type(value=Types.LONGNVARCHAR)
	private String xmlCol;

	public Integer getID() {
		return iD;
	}

	public void setID(Integer iD) {
		this.iD = iD;
	}

	public String getVarcharCol() {
		return varcharCol;
	}

	public void setVarcharCol(String varcharCol) {
		this.varcharCol = varcharCol;
	}

	public Short getSmallIntCol() {
		return smallIntCol;
	}

	public void setSmallIntCol(Short smallIntCol) {
		this.smallIntCol = smallIntCol;
	}

	public Short getTinyIntCol() {
		return tinyIntCol;
	}

	public void setTinyIntCol(Short tinyIntCol) {
		this.tinyIntCol = tinyIntCol;
	}

	public Long getBigIntCol() {
		return bigIntCol;
	}

	public void setBigIntCol(Long bigIntCol) {
		this.bigIntCol = bigIntCol;
	}

	public Boolean getBitCol() {
		return bitCol;
	}

	public void setBitCol(Boolean bitCol) {
		this.bitCol = bitCol;
	}

	public byte[] getBinaryCol() {
		return binaryCol;
	}

	public void setBinaryCol(byte[] binaryCol) {
		this.binaryCol = binaryCol;
	}

	public byte[] getVarBinaryCol() {
		return varBinaryCol;
	}

	public void setVarBinaryCol(byte[] varBinaryCol) {
		this.varBinaryCol = varBinaryCol;
	}

	public byte[] getVarBinaryMaxCol() {
		return varBinaryMaxCol;
	}

	public void setVarBinaryMaxCol(byte[] varBinaryMaxCol) {
		this.varBinaryMaxCol = varBinaryMaxCol;
	}

	public BigDecimal getNumericCol() {
		return numericCol;
	}

	public void setNumericCol(BigDecimal numericCol) {
		this.numericCol = numericCol;
	}

	public Double getFloatCol() {
		return floatCol;
	}

	public void setFloatCol(Double floatCol) {
		this.floatCol = floatCol;
	}

	public BigDecimal getDecimalCol() {
		return decimalCol;
	}

	public void setDecimalCol(BigDecimal decimalCol) {
		this.decimalCol = decimalCol;
	}

	public Float getRealCol() {
		return realCol;
	}

	public void setRealCol(Float realCol) {
		this.realCol = realCol;
	}

	public String getCharCol() {
		return charCol;
	}

	public void setCharCol(String charCol) {
		this.charCol = charCol;
	}

	public String getNcharCol() {
		return ncharCol;
	}

	public void setNcharCol(String ncharCol) {
		this.ncharCol = ncharCol;
	}

	public String getNvarcharCol() {
		return nvarcharCol;
	}

	public void setNvarcharCol(String nvarcharCol) {
		this.nvarcharCol = nvarcharCol;
	}

	public String getNvarcharMaxCol() {
		return nvarcharMaxCol;
	}

	public void setNvarcharMaxCol(String nvarcharMaxCol) {
		this.nvarcharMaxCol = nvarcharMaxCol;
	}

	public Integer getIntCol() {
		return intCol;
	}

	public void setIntCol(Integer intCol) {
		this.intCol = intCol;
	}

	public String getVarcharmaxCol() {
		return varcharmaxCol;
	}

	public void setVarcharmaxCol(String varcharmaxCol) {
		this.varcharmaxCol = varcharmaxCol;
	}

	public Date getDateCol() {
		return dateCol;
	}

	public void setDateCol(Date dateCol) {
		this.dateCol = dateCol;
	}

	public Timestamp getDatetimeCol() {
		return datetimeCol;
	}

	public void setDatetimeCol(Timestamp datetimeCol) {
		this.datetimeCol = datetimeCol;
	}

	public Timestamp getDatetime2Col() {
		return datetime2Col;
	}

	public void setDatetime2Col(Timestamp datetime2Col) {
		this.datetime2Col = datetime2Col;
	}

	public DateTimeOffset getDatetimeOffsetCol() {
		return datetimeOffsetCol;
	}

	public void setDatetimeOffsetCol(DateTimeOffset datetimeOffsetCol) {
		this.datetimeOffsetCol = datetimeOffsetCol;
	}

	public Timestamp getSmallDatetimeCol() {
		return smallDatetimeCol;
	}

	public void setSmallDatetimeCol(Timestamp smallDatetimeCol) {
		this.smallDatetimeCol = smallDatetimeCol;
	}

	public Time getTimeCol() {
		return timeCol;
	}

	public void setTimeCol(Time timeCol) {
		this.timeCol = timeCol;
	}

	public byte[] getTimestampCol() {
		return timestampCol;
	}

	public void setTimestampCol(byte[] timestampCol) {
		this.timestampCol = timestampCol;
	}

	public byte[] getGeographyCol() {
		return geographyCol;
	}

	public void setGeographyCol(byte[] geographyCol) {
		this.geographyCol = geographyCol;
	}

	public byte[] getImageCol() {
		return imageCol;
	}

	public void setImageCol(byte[] imageCol) {
		this.imageCol = imageCol;
	}

	public byte[] getGeometryCol() {
		return geometryCol;
	}

	public void setGeometryCol(byte[] geometryCol) {
		this.geometryCol = geometryCol;
	}

	public byte[] getHierarchidCol() {
		return hierarchidCol;
	}

	public void setHierarchidCol(byte[] hierarchidCol) {
		this.hierarchidCol = hierarchidCol;
	}

	public BigDecimal getMoneyCol() {
		return moneyCol;
	}

	public void setMoneyCol(BigDecimal moneyCol) {
		this.moneyCol = moneyCol;
	}

	public BigDecimal getSmallMoneyCol() {
		return smallMoneyCol;
	}

	public void setSmallMoneyCol(BigDecimal smallMoneyCol) {
		this.smallMoneyCol = smallMoneyCol;
	}

	public String getNtextCol() {
		return ntextCol;
	}

	public void setNtextCol(String ntextCol) {
		this.ntextCol = ntextCol;
	}

	public String getTextCol() {
		return textCol;
	}

	public void setTextCol(String textCol) {
		this.textCol = textCol;
	}

	public String getUniqueidentifierCol() {
		return uniqueidentifierCol;
	}

	public void setUniqueidentifierCol(String uniqueidentifierCol) {
		this.uniqueidentifierCol = uniqueidentifierCol;
	}

	public String getXmlCol() {
		return xmlCol;
	}

	public void setXmlCol(String xmlCol) {
		this.xmlCol = xmlCol;
	}

}
