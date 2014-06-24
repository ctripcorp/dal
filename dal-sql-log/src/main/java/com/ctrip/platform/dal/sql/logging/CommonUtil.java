package com.ctrip.platform.dal.sql.logging;

public class CommonUtil {
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
}
