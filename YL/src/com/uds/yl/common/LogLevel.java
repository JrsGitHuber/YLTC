package com.uds.yl.common;



public enum LogLevel {
	INFO("INFO"),
	DEBUG("DEUBG"),
	ERROE("ERROR");
	String value;
	LogLevel(String value){
		this.value= value;
	}
	
	public String getValue(){
		return this.value;
	}


}
