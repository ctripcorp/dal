package test.com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by lilj on 2018/5/3.
 */
public class LoggerHelperTest {
    @Test
    public void testToJSon() throws Exception{
        String finalString="{\"HasSql\":\"1\",\"Hash\":\"\",\"SqlTpl\":\"/*100008424-DRTestDao.insert[MSG_ID:100008424-0a201579-423702-100004]*/INSERT INTO `testTable` (`Name`) VALUES(?)\",\"Param\":\"aXuR5QP4NhJhsYi3H+rKEdHG/06mtU6m\",\"IsSuccess\":\"1\",\"ErrorMsg\":\"\",\"CostDetail\":\"{'Decode'='16', 'Connect'='517', 'Prepare'='1', 'Execute'='11', 'ClearUp'='2'}\"}";

        Map<String, String> costDetailMap = new LinkedHashMap();
        costDetailMap.put("'Decode'", "'" + Long.toString(16l) + "'");
        costDetailMap.put("'Connect'", "'" + Long.toString(517l) + "'");
        costDetailMap.put("'Prepare'", "'"+Long.toString(1l)+"'");
        costDetailMap.put("'Execute'", "'" + Long.toString(11l) + "'");
        costDetailMap.put("'ClearUp'", "'" + Long.toString(2l) + "'");


        Map<String,String> logMap=new LinkedHashMap<>();
        logMap.put("HasSql","1");
        logMap.put("Hash","");
        logMap.put("SqlTpl","/*100008424-DRTestDao.insert[MSG_ID:100008424-0a201579-423702-100004]*/INSERT INTO `testTable` (`Name`) VALUES(?)");
        logMap.put("Param","aXuR5QP4NhJhsYi3H+rKEdHG/06mtU6m");
        logMap.put("IsSuccess","1" );
        logMap.put("ErrorMsg", "");
        logMap.put("CostDetail", costDetailMap.toString());

        assertEquals(finalString, LoggerHelper.toJson(logMap));
    }
}
