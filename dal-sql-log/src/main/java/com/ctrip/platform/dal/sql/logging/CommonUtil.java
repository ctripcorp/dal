package com.ctrip.platform.dal.sql.logging;

import java.util.regex.Pattern;

public class CommonUtil {
    private static final String tableNamePattern = "";
    private static final Pattern tableNameRegex;
    static {
        tableNameRegex = Pattern.compile(tableNamePattern, Pattern.CASE_INSENSITIVE);
    }

	public static int GetHashCode(String str) {
		int hash, i;
		char[] arr = str.toCharArray();
		for (hash = i = 0; i < arr.length; ++i) {
			hash += arr[i];
			hash += (hash << 12);
			hash ^= (hash >> 4);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		return hash;
	}
	
	public static String null2NA(String str)
    {
    	return null != str ? str : "NA";
    }
	
	public static String string2Json(String s) {
		if(null == s) return "";
        StringBuffer sb = new StringBuffer();        
        for (int i=0; i<s.length(); i++) {  
            char c = s.charAt(i);    
             switch (c){  
             case '\"':        
                 sb.append("\\\"");        
                 break;        
             case '\\':        
                 sb.append("\\\\");        
                 break;        
             case '/':        
                 sb.append("\\/");        
                 break;        
             case '\b':        
                 sb.append("\\b");        
                 break;        
             case '\f':        
                 sb.append("\\f");        
                 break;        
             case '\n':        
                 sb.append("\\n");        
                 break;        
             case '\r':        
                 sb.append("\\r");        
                 break;        
             case '\t':        
                 sb.append("\\t");        
                 break;        
             default:        
                 sb.append(c);     
             }  
         }      
        return sb.toString();     
	}

    public static String parseTableName(String... sql){
         return "";
    }
}
