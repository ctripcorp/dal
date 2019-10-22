package com.ctrip.platform.dal.application.entity;


import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Types;

@Entity
@Database(name="DalMySqlTest")
@Table(name="DALServiceTable")
public class DALServiceTable {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(value= Types.BIGINT)
	private Long iD;


	@Column(name="Name")
	@Type(value= Types.VARCHAR)
	private String name;

	@Column(name="Age")
	@Type(value= Types.INTEGER)
	private Integer age;

//	@Column(name="Birth")
//	@Type(value= Types.TIMESTAMP)
//	private Timestamp birth;

	public Long getID() {
		return iD;
	}

	public void setID(Long iD) {

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
//
//		this.birth = birth;
//	}

}