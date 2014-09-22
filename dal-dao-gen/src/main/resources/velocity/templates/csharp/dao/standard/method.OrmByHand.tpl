#if($host.isTable())      
        //特别注意，如果是可空类型，建议以如下方式使用：
        // var data = reader["field"];
        // entity.stringData = data == null ? data : data.ToString();
        //如需要手工映射，请反注释如下代码，并注意转换类型
        /*
        /// <summary>
        /// 手工映射，建议使用1.2.0.5版本以上的VisitDataReader
        /// </summary>
        /// <returns>结果</returns>
        public ${host.getClassName()} OrmByHand(string sql)
        {
            try
            {
                return baseDao.VisitDataReader<${host.getClassName()}>(sql, (reader) =>
                {
                    ${host.getClassName()} entity = new ${host.getClassName()}();
					if(reader.Read())
					{
#foreach($column in $host.getColumns())
                        entity.#if($WordUtils.capitalize($column.getName()) == $host.getClassName())${host.getClassName()}_Gen#{else}${WordUtils.capitalize($column.getName())}#end = reader["$column.getName()"];
#end
                    }
                    return entity;
                });

                //${host.getClassName()} entity = new ${host.getClassName()}();
                //using(var reader = baseDao.SelectDataReader(sql))
                //{
					//if(reader.Read())
					//{
#foreach($column in $host.getColumns())
                        //entity.#if($WordUtils.capitalize($column.getName()) == $host.getClassName())${host.getClassName()}_Gen#{else}${WordUtils.capitalize($column.getName())}#end = reader["$column.getName()"];
#end
	                //}
                //}
                //return entity;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问OrmByHand时出错", ex);
            }
        }
        */
#end
