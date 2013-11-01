package com.ctrip.sysdev.das.console;

import java.util.List;

import com.ctrip.sysdev.das.console.domain.SalveDB;
import com.ctrip.sysdev.das.console.domain.Node;
import com.ctrip.sysdev.das.console.domain.Port;

public class DasConfigureManager {
	public List<SalveDB> getAllDB(){
		return null;
	}
	
	public SalveDB getDBByName(String dbLogicName){
		return null;
	}

	public void updateDB(SalveDB db){
		
	}
	
	public void deleteDbByName(String dbLogicName) {
	}		
	
//*****************************************************
	public List<Node> getAllNode(){
		return null;
	}
	
	public Node getNodeByName(String nodeName){
		return null;
	}

	public void updateNode(Node node){
		
	}
	
	public void deleteNodeByName(String nodeName) {
	}
	
//*****************************************************
	public Port getAllPort(){
		return null;
	}
	
	public void addPort(Integer number){
		
	}
	
	public void deletePortByNumber(Integer number) {
	}		
}
