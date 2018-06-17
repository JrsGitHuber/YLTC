 /**************************************************************************************************                                      
 *                                               ∞Ê»®πÈUDSÀ˘”–£¨2015
 **************************************************************************************************                             
 *  
 *        Function Description
 *        
 **************************************************************************************************
 * Date           Author                   History  
 * 29-Oct-2015    ChenChun/ZhangYang               Initial
 * 
 **************************************************************************************************/


package com.uds.yl.herb;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentVariableReader {
	private static Map<String,String> variables;
	private static final String REGEX="^\"*(.*?)\"*$";
	static{
		variables=new HashMap<String,String>();
		for(Map.Entry<String, String> map:System.getenv().entrySet())
		{
			Matcher matcher=Pattern.compile(REGEX).matcher(map.getValue());
			if(matcher.find())
				variables.put(map.getKey(), matcher.group(1));
			else
				variables.put(map.getKey(), map.getValue());
		}
	}
	public static Map<String,String> getEnvironmentVariables(){
		return variables;
	}
	public static String getEnvironmentVariable(String key){
		for(Map.Entry<String, String> entry:variables.entrySet())
		{
			if(entry.getKey().equalsIgnoreCase(key))
				return entry.getValue();
		}
		return null;
	}
/*	public static void main(String[] args) {
		System.out.println(getEnvironmentVariables());
		System.out.println(getEnvironmentVariable("comSpec"));
		System.out.println(getEnvironmentVariable("Com"));
		String regex="^\"*(.*?)\"*$";
		//\"ab,ab\",a\"b,\"ab\",a\"b\",a\"\"b,\"a\"b\",\"ab\"\",a\"\"b\",ab\"\"\"
		Matcher matcher=Pattern.compile(regex).matcher("ab\"\"\"");
		if(matcher.find())
			System.out.println(matcher.group(1));
	}*/
}
