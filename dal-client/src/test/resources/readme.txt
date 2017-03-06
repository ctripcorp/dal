To setup DB
* install oracle at 1521, mysql at 3306 and sqlserver at 1433 on one server
* create DB on the server with the given DB type and the name defined by connectionStrings in dal.config

To run test:
* copy database.properties.tpl to d:\dal and rename it to database.properties 
* replace testDbSvrIp with real server ip
* provide user name, password for each database
* set user name, password, connection url for each database
* run test.com.ctrip.platform.dal.dao.AllTest as test case
* it usually takes 10 to 20 minutes to finish all the tests
* have fun!