package com.ctrip.framework.dal.dbconfig.plugin.context;

import com.google.common.base.Strings;

/**
 * @author c7ch23en
 */
public class EnvProfile {

    private String env;
    private String subEnv;

    public EnvProfile(String env) {
        this(env, null);
    }

    public EnvProfile(String env, String subEnv) {
        this.env = env;
        this.subEnv = subEnv;
    }

    public String formatProfile() {
        String profile = env;
        if(env != null){
            if(!env.contains(":")){
                profile = env.toLowerCase() + ":";
                if(!Strings.isNullOrEmpty(subEnv)){
                    profile += subEnv;
                }
            }else{
                String[] entryArray = env.split(":");
                if(entryArray.length >= 2) {
                    profile = entryArray[0].toLowerCase() + ":" + entryArray[1];
                }
            }
        }
        //to lowercase  //subEnv is case sensitive for qconfig client.    [2017-11-03]
//        if(profile != null) {
//            profile = profile.toLowerCase();
//        }
        return profile;
    }

    //format profile. eg: fat:fat2   ->  fat:
    public String formatTopProfile() {
        String profile = formatProfile();
        if(profile != null && profile.contains(":")){
            int index = profile.indexOf(":");
            profile = profile.substring(0, index+1); //include ':'
        }
        return profile;
    }

    //raw profile. eg: uat:   ->  uat
    public String formatEnv(){
        String profile = formatProfile();
        String env = profile;
        if(profile != null && profile.contains(":")){
            env = profile.split(":")[0];
        }
        return env;
    }

    //get sub env. eg: fat:fat2 ->  fat2
    public String formatSubEnv(){
        String profile = formatProfile();
        String subenv = "";
        if(profile != null && profile.contains(":")){
            String[] envEntry = profile.split(":");
            if(envEntry != null && envEntry.length >= 2){
                subenv = envEntry[1];
            }
        }
        return subenv;
    }

}
