#foreach($method in $host.getExtraMethods())
#if($method.getCrud_type() != "select")
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
		int ${method.getName()}(#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getAlias())}#if($foreach.count != $method.getParameters().size()),#end#end);
#end
#end