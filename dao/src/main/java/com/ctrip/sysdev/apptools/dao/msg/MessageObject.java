package com.ctrip.sysdev.apptools.dao.msg;

import java.util.List;

import com.ctrip.sysdev.apptools.dao.enums.ActionType;
import com.ctrip.sysdev.apptools.dao.enums.MessageType;


public class MessageObject {

  public MessageType messageType; //always

  public ActionType actionType; //always

  public boolean useCache; //always

  public String SPName;
  
  public boolean batchOperation;

  public String SQL;
  
  public List<AvailableType> singleArgs;
  
  public List<List<AvailableType>> batchArgs;
  
  public int flags; //always
  
  /**
   * Get the count of valid properties
   * @return
   */
  public int propertyCount(){
	  
	  int result = 0;
	  int alwaysProperty = 4;
	  
	  if(messageType == MessageType.SP){
		  result += 2;
	  }else{
		  result += 3;
	  }
	  
	  result += alwaysProperty;
	  
	  return result;
  }

}
