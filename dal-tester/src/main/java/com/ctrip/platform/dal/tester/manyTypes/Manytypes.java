package com.ctrip.platform.dal.tester.manyTypes;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class Manytypes implements DalPojo {

	private Integer Id;
	private Integer TinyIntCol;
	private Integer SmallIntCol;
	private Integer IntCol;
	private Long BigIntCol;
	private BigDecimal DecimalCol;
	private Double DoubleCol;
	private Float FloatCol;
	private Boolean BitCol;
	private String CharCol;
	private String VarCharCol;
	private Date DateCol;
	private Timestamp DateTimeCol;
	private Time TimeCol;
	private Timestamp TimestampCol;
	private Date YearCol;
	private byte[] BinaryCol;
	private byte[] BlobCol;
	private byte[] LongBlobCol;
	private byte[] MediumBlobCol;
	private byte[] TinyBlobCol;
	private byte[] VarBinaryCol;
	private String LongTextCol;
	private String MediumTextCol;
	private String TextCol;
	private String TinyTextCol;

	public Integer getId(){
		return Id;
	}

	public void setId(Integer Id){
		this.Id = Id;
	}

	public Integer getTinyIntCol(){
		return TinyIntCol;
	}

	public void setTinyIntCol(Integer TinyIntCol){
		this.TinyIntCol = TinyIntCol;
	}

	public Integer getSmallIntCol(){
		return SmallIntCol;
	}

	public void setSmallIntCol(Integer SmallIntCol){
		this.SmallIntCol = SmallIntCol;
	}

	public Integer getIntCol(){
		return IntCol;
	}

	public void setIntCol(Integer IntCol){
		this.IntCol = IntCol;
	}

	public Long getBigIntCol(){
		return BigIntCol;
	}

	public void setBigIntCol(Long BigIntCol){
		this.BigIntCol = BigIntCol;
	}

	public BigDecimal getDecimalCol(){
		return DecimalCol;
	}

	public void setDecimalCol(BigDecimal DecimalCol){
		this.DecimalCol = DecimalCol;
	}

	public Double getDoubleCol(){
		return DoubleCol;
	}

	public void setDoubleCol(Double DoubleCol){
		this.DoubleCol = DoubleCol;
	}

	public Float getFloatCol(){
		return FloatCol;
	}

	public void setFloatCol(Float FloatCol){
		this.FloatCol = FloatCol;
	}

	public Boolean getBitCol(){
		return BitCol;
	}

	public void setBitCol(Boolean BitCol){
		this.BitCol = BitCol;
	}

	public String getCharCol(){
		return CharCol;
	}

	public void setCharCol(String CharCol){
		this.CharCol = CharCol;
	}

	public String getVarCharCol(){
		return VarCharCol;
	}

	public void setVarCharCol(String VarCharCol){
		this.VarCharCol = VarCharCol;
	}

	public Date getDateCol(){
		return DateCol;
	}

	public void setDateCol(Date DateCol){
		this.DateCol = DateCol;
	}

	public Timestamp getDateTimeCol(){
		return DateTimeCol;
	}

	public void setDateTimeCol(Timestamp DateTimeCol){
		this.DateTimeCol = DateTimeCol;
	}

	public Time getTimeCol(){
		return TimeCol;
	}

	public void setTimeCol(Time TimeCol){
		this.TimeCol = TimeCol;
	}

	public Timestamp getTimestampCol(){
		return TimestampCol;
	}

	public void setTimestampCol(Timestamp TimestampCol){
		this.TimestampCol = TimestampCol;
	}

	public Date getYearCol(){
		return YearCol;
	}

	public void setYearCol(Date YearCol){
		this.YearCol = YearCol;
	}

	public byte[] getBinaryCol(){
		return BinaryCol;
	}

	public void setBinaryCol(byte[] BinaryCol){
		this.BinaryCol = BinaryCol;
	}

	public byte[] getBlobCol(){
		return BlobCol;
	}

	public void setBlobCol(byte[] BlobCol){
		this.BlobCol = BlobCol;
	}

	public byte[] getLongBlobCol(){
		return LongBlobCol;
	}

	public void setLongBlobCol(byte[] LongBlobCol){
		this.LongBlobCol = LongBlobCol;
	}

	public byte[] getMediumBlobCol(){
		return MediumBlobCol;
	}

	public void setMediumBlobCol(byte[] MediumBlobCol){
		this.MediumBlobCol = MediumBlobCol;
	}

	public byte[] getTinyBlobCol(){
		return TinyBlobCol;
	}

	public void setTinyBlobCol(byte[] TinyBlobCol){
		this.TinyBlobCol = TinyBlobCol;
	}

	public byte[] getVarBinaryCol(){
		return VarBinaryCol;
	}

	public void setVarBinaryCol(byte[] VarBinaryCol){
		this.VarBinaryCol = VarBinaryCol;
	}

	public String getLongTextCol(){
		return LongTextCol;
	}

	public void setLongTextCol(String LongTextCol){
		this.LongTextCol = LongTextCol;
	}

	public String getMediumTextCol(){
		return MediumTextCol;
	}

	public void setMediumTextCol(String MediumTextCol){
		this.MediumTextCol = MediumTextCol;
	}

	public String getTextCol(){
		return TextCol;
	}

	public void setTextCol(String TextCol){
		this.TextCol = TextCol;
	}

	public String getTinyTextCol(){
		return TinyTextCol;
	}

	public void setTinyTextCol(String TinyTextCol){
		this.TinyTextCol = TinyTextCol;
	}

}
