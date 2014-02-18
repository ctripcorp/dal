
package com.ctrip.platform.tools;

import com.ctrip.platform.dal.dao.DalPojo;

public class Project implements DalPojo {

	private int id;
	private String user_id;
	private String name;
	private String namespace;

	public int getid(){
		return id;
	}

	public void setid(int id){
		this.id = id;
	}

	public String getuser_id(){
		return user_id;
	}

	public void setuser_id(String user_id){
		this.user_id = user_id;
	}

	public String getname(){
		return name;
	}

	public void setname(String name){
		this.name = name;
	}

	public String getnamespace(){
		return namespace;
	}

	public void setnamespace(String namespace){
		this.namespace = namespace;
	}


}