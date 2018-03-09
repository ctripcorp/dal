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
		${host.getClassName()} ${method.getName()} (${method.getParameterDeclaration()});
#end
#end