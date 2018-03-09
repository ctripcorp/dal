package com.ctrip.platform.dal.dao.sqlbuilder;

/**
 * The match pattern for like operation.
 * 
 * "head" will add % in front of the parameter; 
 * "tail" will add % behind the parameter;
 * "both" will add % at both end of the parameter;
 * "none" will not add and % to the parameter;
 * 
 * Eg.
 * If the parameter is "abc", then header changed the 
 * final value in sql to "%abc"; tail changes it to "abc%"
 * and both changes it "%abc%"
 * 
 * @author jhhe
 *
 */
public enum MatchPattern {
    END_WITH,
    BEGIN_WITH,
    CONTAINS,
    USER_DEFINED;
    
    public String process(String value) {
        if(value == null)
            return null;
        
        switch (this) {
            case END_WITH:
                return "%" + value;
            case BEGIN_WITH:
                return value + "%";
            case CONTAINS:
                return "%" + value + "%";
            case USER_DEFINED:
                return value;
            default:
                throw new IllegalStateException("Not supported yet");
        }
    }
}
