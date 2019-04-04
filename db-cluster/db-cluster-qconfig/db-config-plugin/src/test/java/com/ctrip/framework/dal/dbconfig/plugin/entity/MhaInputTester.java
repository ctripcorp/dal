package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.MhaInputBasicData;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.MhaInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.google.common.base.Strings;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzyan on 2017/8/23.
 */
public class MhaInputTester {

    @Test
    public void testObj2Json(){
        MhaInputBasicData mibd_1 = new MhaInputBasicData("testKey_1", "127.0.0.1", 28747);
        MhaInputBasicData mibd_2 = new MhaInputBasicData("testKey_2", "127.0.0.2", 28747);

        String env = "pro";
        List<MhaInputBasicData> data = new ArrayList<MhaInputBasicData>();
        data.add(mibd_1);
        data.add(mibd_2);
        MhaInputEntity mhaInputEntity = new MhaInputEntity(env, data);
        String mhaInput = GsonUtils.t2Json(mhaInputEntity);
        System.out.println(mhaInput);
        assert(!Strings.isNullOrEmpty(mhaInput));
    }

    @Test
    public void testJson2Obj(){
        String mhaInput = "{\"env\":\"pro\",\"data\":[{\"keyname\":\"testKey_1\",\"server\":\"127.0.0.1\",\"port\":28747},{\"keyname\":\"testKey_2\",\"server\":\"127.0.0.2\",\"port\":28747}]}";
        MhaInputEntity mhaInputEntity = GsonUtils.json2T(mhaInput,  MhaInputEntity.class);
        System.out.println("mhaInputEntity=\n" + mhaInputEntity);
        assert(mhaInputEntity != null);
    }

}
