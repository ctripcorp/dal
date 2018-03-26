##简单类型并且返回值是First
#foreach($method in $host.getExtraMethods())
#if($method.isFirstOrSingle() && $method.isSampleType())
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>	
		object ${method.getName()} (${method.getParameterDeclaration()});
#end
#end