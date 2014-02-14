
package hjhTest;

import com.ctrip.platform.dal.dao.DalPojo;

public class Person implements DalPojo {
	private int ID;
	private String Address;
	private String Telephone;
	private String Name;
	private int Age;
	private int Gender;
	private Timestamp Birth;
			
	public int getID(){
		return ID;
	}

	public void setID(int ID){
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
		
	public int getAge(){
		return Age;
	}

	public void setAge(int Age){
		this.Age = Age;
	}
		
	public int getGender(){
		return Gender;
	}

	public void setGender(int Gender){
		this.Gender = Gender;
	}
		
	public Timestamp getBirth(){
		return Birth;
	}

	public void setBirth(Timestamp Birth){
		this.Birth = Birth;
	}
	
}