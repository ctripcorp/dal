
package hjhTest;

import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalPojo;

public class Person implements DalPojo {

	private Integer ID;
	private String Address;
	private String Telephone;
	private String Name;
	private Integer Age;
	private Integer Gender;
	private Timestamp Birth;

	public Integer getID(){
		return ID;
	}

	public void setID(Integer ID){
		this.ID = ID;
	}

	public String getAddress(){
		return Address;
	}

	public void setAddress(String Address){
		this.Address = Address;
	}

	public String getTelephone(){
		return Telephone;
	}

	public void setTelephone(String Telephone){
		this.Telephone = Telephone;
	}

	public String getName(){
		return Name;
	}

	public void setName(String Name){
		this.Name = Name;
	}

	public Integer getAge(){
		return Age;
	}

	public void setAge(Integer Age){
		this.Age = Age;
	}

	public Integer getGender(){
		return Gender;
	}

	public void setGender(Integer Gender){
		this.Gender = Gender;
	}

	public Timestamp getBirth(){
		return Birth;
	}

	public void setBirth(Timestamp Birth){
		this.Birth = Birth;
	}

}