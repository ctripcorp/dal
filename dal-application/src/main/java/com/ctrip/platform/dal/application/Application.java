package com.ctrip.platform.dal.application;

import com.ctrip.platform.dal.application.dao.DALServiceDao;
import com.ctrip.platform.dal.application.entity.DALServiceTable;
import com.ctrip.platform.dal.application.service.DALService;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by lilj on 2018/6/2.
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Application extends SpringBootServletInitializer{
    private static Logger log = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) throws Exception{
//        SpringApplication.run(Application.class,args);
        DALServiceDao dao=new DALServiceDao();
        while(true){
            try {
                log.info(String.format("current hostname: %s", dao.selectHostname(new DalHints())));
            }catch (Exception e){
                log.error("select hostname error",e);
            }finally {
                Thread.sleep(3000);
            }
        }
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(Application.class);
    }
}
