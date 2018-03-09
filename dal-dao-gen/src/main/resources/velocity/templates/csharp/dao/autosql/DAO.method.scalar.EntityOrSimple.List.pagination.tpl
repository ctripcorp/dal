##实体类型或简单类型且返回分页List
#foreach($method in $host.getExtraMethods())
#if(!$method.isFirstOrSingle() && $method.getCrud_type() == "select" && $method.isPaging())
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
		public IList<${host.getClassName()}> ${method.getName()}(${method.getParameterDeclaration()})
        {
            try
            {
                var query = baseDao.GetQuery<Entity.DataModel.${host.getClassName()}>();
#parse("templates/csharp/dao/autosql/common.constrain.parameters.tpl")		
				query.Paging(pageNo, pageSize, ${method.getOrderByExp()});
	            return baseDao.SelectList<Entity.DataModel.${host.getClassName()}>(query);
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
        }
#end
#end