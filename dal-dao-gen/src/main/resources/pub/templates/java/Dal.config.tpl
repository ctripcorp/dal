<dal name="dal.prize.test">
  <databaseSets>
#foreach($db in $dbs.keySet())
    <databaseSet name="$db" provider="${dbs.get($db)}">
      <add  name="${db}_M" databaseType="Master" sharding="" connectionString="$db"/>
      <!--add  name="${db}_S" databaseType="Slave" sharding="" connectionString="$db"/-->
    </databaseSet>
#end
  </databaseSets>
</dal>
