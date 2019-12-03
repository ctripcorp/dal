package com.ctrip.datasource.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.List;

public class GsonUtils {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 创建GSON
	 */
	public static Gson getGson(){
		//.disableHtmlEscaping()
		return new GsonBuilder().serializeNulls().setDateFormat(DATE_FORMAT).create();
	}

	 
    /**
     * 将对象转化为字符串
     * @param obj
     * @return
     */
    public static String Object2Json(Object obj){
    	return getGson().toJson(obj);
    }

 

    /**
     * 将对象转化为字符串(泛型实现)
     * @param t
     * @return
     */
    public static <T> String t2Json(T t){
    	return getGson().toJson(t);
    }

 

    /**
     * 将字符转化为对象
     * @param <T>
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> T json2T(String jsonString , Class<T> clazz){
        return getGson().fromJson(jsonString,clazz);
    }

 

    /**
     * 将字符串数组转化为对象集合
     * @param <T>
     * @param jsonStr
     * @param type
     * @return
     */
    public static <T> List<T> json2Collection(String jsonStr, Type type){
        return getGson().fromJson(jsonStr,type);
    }



}
