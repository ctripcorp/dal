package com.ctrip.datasource.spring.integration;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface AppMapper {

  @Select("SELECT * FROM APP limit #{limit} offset #{offset}")
  List<App> queryAll(@Param("offset") int offset, @Param("limit") int limit);

  @Select("SELECT * FROM APP where id = #{id}")
  App queryById(@Param("id") long id);

  List<App> queryByIdList(@Param("idList") List<Long> idList);

  @Update("UPDATE APP SET name = #{appName} where id = #{id}")
  int updateAppName(@Param("id") long id, @Param("appName") String appName);

  // sql is defined in AppMapper.xml
  int insertApp(App app);

  // sql is defined in AppMapper.xml
  int insertAppList(List<App> appList);

  @Delete("delete from App where id = #{id}")
  int deleteAppById(Long id);
}
