
package hjhTest;

import com.ctrip.platform.dal.dao.DalPojo;

public class Person implements DalPojo {

	
	private int ID;

	public int getID(){
		return ID;
	}

	//(��ʾ(���ᱻVerlocity����
	public void setID(int ID){
		this.ID = ID;
	}

	
	private String Address;

	public String getAddress(){
		return Address;
	}

	//(��ʾ(���ᱻVerlocity����
	public void setAddress(String Address){
		this.Address = Address;
	}

	
	private String Telephone;

	public String getTelephone(){
		return Telephone;
	}

	//(��ʾ(���ᱻVerlocity����
	public void setTelephone(String Telephone){
		this.Telephone = Telephone;
	}

	
	private String Name;

	public String getName(){
		return Name;
	}

	//(��ʾ(���ᱻVerlocity����
	public void setName(String Name){
		this.Name = Name;
	}

	
	private int Age;

	public int getAge(){
		return Age;
	}

	//(��ʾ(���ᱻVerlocity����
	public void setAge(int Age){
		this.Age = Age;
	}

	
	private int Gender;

	public int getGender(){
		return Gender;
	}

	//(��ʾ(���ᱻVerlocity����
	public void setGender(int Gender){
		this.Gender = Gender;
	}

	
	private Timestamp Birth;

	public Timestamp getBirth(){
		return Birth;
	}

	//(��ʾ(���ᱻVerlocity����
	public void setBirth(Timestamp Birth){
		this.Birth = Birth;
	}

	
}