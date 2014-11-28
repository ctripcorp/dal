package intest;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import microsoft.sql.DateTimeOffset;

import com.ctrip.platform.dal.dao.DalPojo;

public class TestTable implements DalPojo {
	private Integer iD;
	private Timestamp datetime;
	private Timestamp datetime2;
	private Timestamp smalldatetime;
	private Date date;
	private DateTimeOffset datetimeoffset;
	private Time time;
	private Short smallint;
	private Short tinyint;
	private Long bigint;
	private BigDecimal money;
	private BigDecimal smallmoney;
	private Double _float;
	private String text;
	private String ntext;
	private String xml;
	private String _char;
	private String varchar;
	private String nchar;
	private String nvarchar;
	private Float real;
	private BigDecimal decimal;
	private Boolean bit;
	private BigDecimal numeric;
	private byte[] binary;
	private String guid;
	private byte[] image;
	private byte[] timestamp;
	private String charone;
	public Integer getID() {
		return iD;
	}

	public void setID(Integer iD) {
		this.iD = iD;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

	public Timestamp getDatetime2() {
		return datetime2;
	}

	public void setDatetime2(Timestamp datetime2) {
		this.datetime2 = datetime2;
	}

	public Timestamp getSmalldatetime() {
		return smalldatetime;
	}

	public void setSmalldatetime(Timestamp smalldatetime) {
		this.smalldatetime = smalldatetime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public DateTimeOffset getDatetimeoffset() {
		return datetimeoffset;
	}

	public void setDatetimeoffset(DateTimeOffset datetimeoffset) {
		this.datetimeoffset = datetimeoffset;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Short getSmallint() {
		return smallint;
	}

	public void setSmallint(Short smallint) {
		this.smallint = smallint;
	}

	public Short getTinyint() {
		return tinyint;
	}

	public void setTinyint(Short tinyint) {
		this.tinyint = tinyint;
	}

	public Long getBigint() {
		return bigint;
	}

	public void setBigint(Long bigint) {
		this.bigint = bigint;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public BigDecimal getSmallmoney() {
		return smallmoney;
	}

	public void setSmallmoney(BigDecimal smallmoney) {
		this.smallmoney = smallmoney;
	}

	public Double getFloat() {
		return _float;
	}

	public void setFloat(Double _float) {
		this._float = _float;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getNtext() {
		return ntext;
	}

	public void setNtext(String ntext) {
		this.ntext = ntext;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getChar() {
		return _char;
	}

	public void setChar(String _char) {
		this._char = _char;
	}

	public String getVarchar() {
		return varchar;
	}

	public void setVarchar(String varchar) {
		this.varchar = varchar;
	}

	public String getNchar() {
		return nchar;
	}

	public void setNchar(String nchar) {
		this.nchar = nchar;
	}

	public String getNvarchar() {
		return nvarchar;
	}

	public void setNvarchar(String nvarchar) {
		this.nvarchar = nvarchar;
	}

	public Float getReal() {
		return real;
	}

	public void setReal(Float real) {
		this.real = real;
	}

	public BigDecimal getDecimal() {
		return decimal;
	}

	public void setDecimal(BigDecimal decimal) {
		this.decimal = decimal;
	}

	public Boolean getBit() {
		return bit;
	}

	public void setBit(Boolean bit) {
		this.bit = bit;
	}

	public BigDecimal getNumeric() {
		return numeric;
	}

	public void setNumeric(BigDecimal numeric) {
		this.numeric = numeric;
	}

	public byte[] getBinary() {
		return binary;
	}

	public void setBinary(byte[] binary) {
		this.binary = binary;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public byte[] getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(byte[] timestamp) {
		this.timestamp = timestamp;
	}

	public String getCharone() {
		return charone;
	}

	public void setCharone(String charone) {
		this.charone = charone;
	}

}