#if($method.hasParameters())
#set($bwVals = []) 
#set($first = "")
#foreach($p in $method.getParameters())
#if(${p.getConditionType()} == "Equal" && ${p.isNullable()} )
		builder.$!{first}equalNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end	
#if(${p.getConditionType()} == "Equal" && !${p.isNullable()} )
		builder.$!{first}equal("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "NotEqual" && ${p.isNullable()} ) 
		builder.$!{first}notEqualNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "NotEqual" && !${p.isNullable()} ) 
		builder.$!{first}notEqual("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Great" && ${p.isNullable()} )
		builder.$!{first}greaterThanNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Great" && !${p.isNullable()} )
		builder.$!{first}greaterThan("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "lessThan" && ${p.isNullable()} ) 
		builder.$!{first}lessThanNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "lessThan" && !${p.isNullable()} ) 
		builder.$!{first}lessThan("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && ${p.isNullable()} ) 
		builder.$!{first}greaterThanEqualsNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && !${p.isNullable()} ) 
		builder.$!{first}greaterThanEquals("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && ${p.isNullable()} ) 
		builder.$!{first}lessThanEqualsNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && !${p.isNullable()} ) 
		builder.$!{first}lessThanEquals("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Like" && ${p.isNullable()} ) 
		builder.$!{first}likeNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Like" && !${p.isNullable()} ) 
		builder.$!{first}like("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "In" && ${p.isNullable()} ) 
		builder.$!{first}inNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "In" && !${p.isNullable()} ) 
		builder.$!{first}in("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "IsNull") 
		builder.$!{first}isNull("${p.getName()}");
#end
#if(${p.getConditionType()} == "IsNotNull") 
		builder.$!{first}isNotNull("${p.getName()}");
#end
#if(${p.getConditionType()} == "Between")
#set($success = $bwVals.add(${p.getAlias()}))
#end
#if(${p.getConditionType()} == "Between" && ${p.isNullable()} && $bwVals.size()==2)
		builder.$!{first}betweenNullable( "${p.getName()}", $bwVals.get(0), $bwVals.get(1), ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#set($bwVals = [])		
#end
#if(${p.getConditionType()} == "Between" && !${p.isNullable()} && $bwVals.size()==2)
		builder.$!{first}between( "${p.getName()}", $bwVals.get(0), $bwVals.get(1), ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#set($bwVals = [])		
#end
#if(${p.getConditionType()} == "Between" )
#if($first=="" && $bwVals.size()!=1 && $bwVals.size()!=2)
#set($first = "and().")
#end
#else
#set($first = "and().")
#end
#end
#end
