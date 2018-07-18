##实体类型且返回First
#foreach($method in $host.getExtraMethods())
#if($method.isFirstOrSingle() && !$method.isSampleType())
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
		public ${host.getClassName()} ${method.getName()} (${method.getParameterDeclaration()})
        {
            try
            {
				var query = baseDao.GetQuery<Entity.DataModel.${host.getClassName()}>();
#parse("templates/csharp/dao/autosql/common.constrain.parameters.tpl")		
#if($method.getOrderByExp()!="")
			    query.Order(${method.getOrderByExp()});
#end
#if($host.getDatabaseCategory().name() == "MySql")
	            query.Limit(0,1);
#else
	            query.Limit(1);
#end	
	            return baseDao.SelectFirst<Entity.DataModel.${host.getClassName()}>(query);

            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
        }
#end
#end