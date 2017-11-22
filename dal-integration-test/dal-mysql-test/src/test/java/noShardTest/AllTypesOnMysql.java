package noShardTest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import java.sql.Types;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

@Entity
@Database(name="noShardTestOnMysql")
@Table(name="all_types")
public class AllTypesOnMysql implements DalPojo {

	@Id
	@Column(name="idAll_Types")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value=Types.INTEGER)
	private Integer idallTypes;

    //IntCol
	@Column(name="IntCol")
	@Type(value=Types.INTEGER)
	private Integer intCol;

	@Column(name="BigIntCol")
	@Type(value=Types.BIGINT)
	private Long bigIntCol;

	@Column(name="MediumIntCol")
	@Type(value=Types.INTEGER)
	private Integer mediumIntCol;

	@Column(name="SmallIntCol")
	@Type(value=Types.SMALLINT)
	private Integer smallIntCol;

	@Column(name="TinyIntCol")
	@Type(value=Types.TINYINT)
	private Integer tinyIntCol;

	@Column(name="FloatCol")
	@Type(value=Types.REAL)
	private Float floatCol;

	@Column(name="DoubleCol")
	@Type(value=Types.DOUBLE)
	private Double doubleCol;

	@Column(name="DecimalCol")
	@Type(value=Types.DECIMAL)
	private BigDecimal decimalCol;

	@Column(name="CharCol")
	@Type(value=Types.CHAR)
	private String charCol;

	@Column(name="VarCharCol")
	@Type(value=Types.VARCHAR)
	private String varCharCol;

	@Column(name="DateCol")
	@Type(value=Types.DATE)
	private Date dateCol;

	@Column(name="DateTimeCol")
	@Type(value=Types.TIMESTAMP)
	private Timestamp dateTimeCol;

	@Column(name="TimeCol")
	@Type(value=Types.TIME)
	private Time timeCol;

	@Column(name="TimeStampCol",insertable=false,updatable=false)
	@Type(value=Types.TIMESTAMP)
	private Timestamp timeStampCol;

	@Column(name="YearCol")
	@Type(value=Types.DATE)
	private Date yearCol;

	@Column(name="LongTextCol")
	@Type(value=Types.LONGVARCHAR)
	private String longTextCol;

	@Column(name="MediumTextCol")
	@Type(value=Types.LONGVARCHAR)
	private String mediumTextCol;

	@Column(name="TextCol")
	@Type(value=Types.LONGVARCHAR)
	private String textCol;

	@Column(name="TinyTextCol")
	@Type(value=Types.LONGVARCHAR)
	private String tinyTextCol;

	@Column(name="BitCol")
	@Type(value=Types.BIT)
	private Boolean bitCol;

	@Column(name="EnumCol")
	@Type(value=Types.CHAR)
	private String enumCol;

	@Column(name="SetCol")
	@Type(value=Types.CHAR)
	private String setCol;

	@Column(name="BinaryCol")
	@Type(value=Types.BINARY)
	private byte[] binaryCol;

	@Column(name="BlobCol")
	@Type(value=Types.LONGVARBINARY)
	private byte[] blobCol;

	@Column(name="LongBlobCol")
	@Type(value=Types.LONGVARBINARY)
	private byte[] longBlobCol;

	@Column(name="MediumBlobCol")
	@Type(value=Types.LONGVARBINARY)
	private byte[] mediumBlobCol;

	@Column(name="TinyBlobCol")
	@Type(value=Types.VARBINARY)
	private byte[] tinyBlobCol;

	@Column(name="VarBinaryCol")
	@Type(value=Types.VARBINARY)
	private byte[] varBinaryCol;

	@Column(name="GeometryCol")
	@Type(value=Types.BINARY)
	private byte[] geometryCol;

	@Column(name="TimeStampCol2")
	@Type(value=Types.TIMESTAMP)
	private Timestamp timeStampCol2;

	public Integer getIdallTypes() {
		return idallTypes;
	}

	public void setIdallTypes(Integer idallTypes) {
		this.idallTypes = idallTypes;
	}

	public Integer getIntCol() {
		return intCol;
	}

	public void setIntCol(Integer intCol) {
		this.intCol = intCol;
	}

	public Long getBigIntCol() {
		return bigIntCol;
	}

	public void setBigIntCol(Long bigIntCol) {
		this.bigIntCol = bigIntCol;
	}

	public Integer getMediumIntCol() {
		return mediumIntCol;
	}

	public void setMediumIntCol(Integer mediumIntCol) {
		this.mediumIntCol = mediumIntCol;
	}

	public Integer getSmallIntCol() {
		return smallIntCol;
	}

	public void setSmallIntCol(Integer smallIntCol) {
		this.smallIntCol = smallIntCol;
	}

	public Integer getTinyIntCol() {
		return tinyIntCol;
	}

	public void setTinyIntCol(Integer tinyIntCol) {
		this.tinyIntCol = tinyIntCol;
	}

	public Float getFloatCol() {
		return floatCol;
	}

	public void setFloatCol(Float floatCol) {
		this.floatCol = floatCol;
	}

	public Double getDoubleCol() {
		return doubleCol;
	}

	public void setDoubleCol(Double doubleCol) {
		this.doubleCol = doubleCol;
	}

	public BigDecimal getDecimalCol() {
		return decimalCol;
	}

	public void setDecimalCol(BigDecimal decimalCol) {
		this.decimalCol = decimalCol;
	}

	public String getCharCol() {
		return charCol;
	}

	public void setCharCol(String charCol) {
		this.charCol = charCol;
	}

	public String getVarCharCol() {
		return varCharCol;
	}

	public void setVarCharCol(String varCharCol) {
		this.varCharCol = varCharCol;
	}

	public Date getDateCol() {
		return dateCol;
	}

	public void setDateCol(Date dateCol) {
		this.dateCol = dateCol;
	}

	public Timestamp getDateTimeCol() {
		return dateTimeCol;
	}

	public void setDateTimeCol(Timestamp dateTimeCol) {
		this.dateTimeCol = dateTimeCol;
	}

	public Time getTimeCol() {
		return timeCol;
	}

	public void setTimeCol(Time timeCol) {
		this.timeCol = timeCol;
	}

	public Timestamp getTimeStampCol() {
		return timeStampCol;
	}

	public void setTimeStampCol(Timestamp timeStampCol) {
		this.timeStampCol = timeStampCol;
	}

	public Date getYearCol() {
		return yearCol;
	}

	public void setYearCol(Date yearCol) {
		this.yearCol = yearCol;
	}

	public String getLongTextCol() {
		return longTextCol;
	}

	public void setLongTextCol(String longTextCol) {
		this.longTextCol = longTextCol;
	}

	public String getMediumTextCol() {
		return mediumTextCol;
	}

	public void setMediumTextCol(String mediumTextCol) {
		this.mediumTextCol = mediumTextCol;
	}

	public String getTextCol() {
		return textCol;
	}

	public void setTextCol(String textCol) {
		this.textCol = textCol;
	}

	public String getTinyTextCol() {
		return tinyTextCol;
	}

	public void setTinyTextCol(String tinyTextCol) {
		this.tinyTextCol = tinyTextCol;
	}

	public Boolean getBitCol() {
		return bitCol;
	}

	public void setBitCol(Boolean bitCol) {
		this.bitCol = bitCol;
	}

	public String getEnumCol() {
		return enumCol;
	}

	public void setEnumCol(String enumCol) {
		this.enumCol = enumCol;
	}

	public String getSetCol() {
		return setCol;
	}

	public void setSetCol(String setCol) {
		this.setCol = setCol;
	}

	public byte[] getBinaryCol() {
		return binaryCol;
	}

	public void setBinaryCol(byte[] binaryCol) {
		this.binaryCol = binaryCol;
	}

	public byte[] getBlobCol() {
		return blobCol;
	}

	public void setBlobCol(byte[] blobCol) {
		this.blobCol = blobCol;
	}

	public byte[] getLongBlobCol() {
		return longBlobCol;
	}

	public void setLongBlobCol(byte[] longBlobCol) {
		this.longBlobCol = longBlobCol;
	}

	public byte[] getMediumBlobCol() {
		return mediumBlobCol;
	}

	public void setMediumBlobCol(byte[] mediumBlobCol) {
		this.mediumBlobCol = mediumBlobCol;
	}

	public byte[] getTinyBlobCol() {
		return tinyBlobCol;
	}

	public void setTinyBlobCol(byte[] tinyBlobCol) {
		this.tinyBlobCol = tinyBlobCol;
	}

	public byte[] getVarBinaryCol() {
		return varBinaryCol;
	}

	public void setVarBinaryCol(byte[] varBinaryCol) {
		this.varBinaryCol = varBinaryCol;
	}

	public byte[] getGeometryCol() {
		return geometryCol;
	}

	public void setGeometryCol(byte[] geometryCol) {
		this.geometryCol = geometryCol;
	}

	public Timestamp getTimeStampCol2() {
		return timeStampCol2;
	}

	public void setTimeStampCol2(Timestamp timeStampCol2) {
		this.timeStampCol2 = timeStampCol2;
	}

}