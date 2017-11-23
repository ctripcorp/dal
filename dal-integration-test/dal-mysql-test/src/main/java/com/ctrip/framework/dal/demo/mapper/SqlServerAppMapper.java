package com.ctrip.framework.dal.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ctrip.framework.dal.demo.entity.App;

public interface SqlServerAppMapper {
  @Select("SELECT Id, AppId, Name, DataChange_CreatedBy, DataChange_CreatedTimeWithOffSet as DataChange_CreatedTime, "
      + "DataChange_LastModifiedBy, DataChange_LastTime "
      + "FROM APP (nolock) order by id offset #{offset} rows fetch next #{limit} rows only")
  List<App> queryAll(@Param("offset") int offset, @Param("limit") int limit);

  @Select("SELECT * FROM APP (nolock) where id = #{id}")
  App queryById(@Param("id") long id);

  List<App> queryByIdList(@Param("idList") List<Long> idList);

  // sql is defined in AppMapper.xml
  int insertApp(App app);

  // sql is defined in AppMapper.xml
  int insertAppList(List<App> appList);

  // sql is defined in AppMapper.xml
  int deleteAppById(Long id);

  // sql is defined in AppMapper.xml
  int updateAppName(@Param("id") long id, @Param("name") String appName);
}
