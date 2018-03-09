##实体类型或简单类型且返回List
#foreach($method in $host.getExtraMethods())
#if(!$method.isFirstOrSingle() && $method.getCrud_type() == "select")
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
		IList<${host.getClassName()}> ${method.getName()}(${method.getParameterDeclaration()});
#end
#end

