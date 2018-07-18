#foreach($method in $host.getExtraMethods())
#if($method.getCrud_type() != "select")
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
		int ${method.getName()}(${method.getParameterDeclaration()});
#end
#end