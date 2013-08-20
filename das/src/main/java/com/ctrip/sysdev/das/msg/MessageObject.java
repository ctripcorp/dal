package com.ctrip.sysdev.das.msg;

import java.util.List;
import java.util.Map;

import com.ctrip.sysdev.das.enums.ActionType;
import com.ctrip.sysdev.das.enums.MessageType;

/****
 * 
 * @author gawu
 *
 */
public class MessageObject {

  // public MyEnum action;

  public MessageType messageType; //always

  public ActionType actionType; //always

  public boolean useCache; //always

  public String SPName;
  
  public boolean batchOperation;

  public String SQL;
  
  public List<AvailableType> singleArgs; //always
  
  public List<List<AvailableType>> batchArgs;
  
  public int flags; //always
  
  /****
   * 
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
