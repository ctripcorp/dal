<dal name="dal.prize.test">
  <databaseSets>
#foreach($db in $dbs.keySet())
    <databaseSet name="$db" provider="${dbs.get($db)}">
      <add  name="${db}_M" databaseType="Master" sharding="" connectionString="请将这段字符串替换为All-In-One中的name或者Database.config中的name"/>
    </databaseSet>
#end
  </databaseSets>

  <databaseProviders>
    <add name="sqlProvider" type="Arch.Data.DbEngine.Providers.SqlDatabaseProvider,Arch.Data"/>
    <add name="mySqlProvider" type="Arch.Data.MySqlProvider.MySqlDatabaseProvider,Arch.Data.MySqlProvider"/>
  </databaseProviders>

  <logListeners>
    <add name="clog" type="Arch.Data.Common.Logging.Listeners.CentralLoggingListener,Arch.Data" level="Information" setting=""/>
    <add name="textfile" type="Arch.Data.Common.Logging.Listeners.TextFileListener,Arch.Data" level="Information" setting="FileSize=4;FilePath=D:\log;FileName={0:yyyy_MM_dd_HH_mm_ss}.log;"/>
  </logListeners>

  <metrics name="centrallogging"/>

</dal>
