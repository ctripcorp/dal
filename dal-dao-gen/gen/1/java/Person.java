
package com.ctrip.platform.tools;

import com.ctrip.platform.dal.dao.DalPojo;

public class Person implements DalPojo {

	
	private int ID;

	public int getID(){
		return ID;
	}

	//(表示(不会被Verlocity解析
	public void setID(int ID){
		this.ID = ID;
	}

	
	private String Address;

	public String getAddress(){
		return Address;
	}

	//(表示(不会被Verlocity解析
	public void setAddress(String Address){
		this.Address = Address;
	}

	
	private String Telephone;

	public String getTelephone(){
		return Telephone;
	}

	//(表示(不会被Verlocity解析
	public void setTelephone(String Telephone){
		this.Telephone = Telephone;
	}

	
	private String Name;

	public String getName(){
		return Name;
	}

	//(表示(不会被Verlocity解析
	public void setName(String Name){
		this.Name = Name;
	}

	
	private int Age;

	public int getAge(){
		return Age;
	}

	//(表示(不会被Verlocity解析
	public void setAge(int Age){
		this.Age = Age;
	}

	
	private int Gender;

	public int getGender(){
		return Gender;
	}

	//(表示(不会被Verlocity解析
	public void setGender(int Gender){
		this.Gender = Gender;
	}

	
	private Timestamp Birth;

	public Timestamp getBirth(){
		return Birth;
	}

	//(表示(不会被Verlocity解析
	public void setBirth(Timestamp Birth){
		this.Birth = Birth;
	}

	
}