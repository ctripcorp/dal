#if($method.hasParameters())
#set($bwVals = []) 
#set($first = "")
#foreach($p in $method.getParameters())
#set($wrapFielName=${p.wrapField(${host.getDatabaseCategory().name()},${p.getName()})})
#if(${p.getConditionType()} == "Equal" && ${p.isNullable()} )
		index = builder.addConstrant().$!{first}equalNullable($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end	
#if(${p.getConditionType()} == "Equal" && !${p.isNullable()} )
		index = builder.addConstrant().$!{first}equal($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "NotEqual" && ${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}notEqualNullable($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "NotEqual" && !${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}notEqual($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "Great" && ${p.isNullable()} )
		index = builder.addConstrant().$!{first}greaterThanNullable($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "Great" && !${p.isNullable()} )
		index = builder.addConstrant().$!{first}greaterThan($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "lessThan" && ${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}lessThanNullable($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "lessThan" && !${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}lessThan($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && ${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}greaterThanEqualsNullable($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && !${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}greaterThanEquals($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && ${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}lessThanEqualsNullable($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && !${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}lessThanEquals($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "Like" && ${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}likeNullable($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "Like" && !${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}like($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "In" && ${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}inNullable($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "In" && !${p.isNullable()} ) 
		index = builder.addConstrant().$!{first}in($wrapFielName, ${p.getAlias()}, parameters, index, ${p.getJavaTypeDisplay()});
#end
#if(${p.getConditionType()} == "IsNull") 
		builder.addConstrant().$!{first}isNull($wrapFielName);
#end
#if(${p.getConditionType()} == "IsNotNull") 
		builder.addConstrant().$!{first}isNotNull($wrapFielName);
#end
#if(${p.getConditionType()} == "Between")
#set($success = $bwVals.add(${p.getAlias()}))
#end
#if(${p.getConditionType()} == "Between" && ${p.isNullable()} && $bwVals.size()==2)
		index = builder.addConstrant().$!{first}betweenNullable( $wrapFielName, $bwVals.get(0), $bwVals.get(1), parameters, index, ${p.getJavaTypeDisplay()});
#set($bwVals = [])		
#end
#if(${p.getConditionType()} == "Between" && !${p.isNullable()} && $bwVals.size()==2)
		index = builder.addConstrant().$!{first}between( $wrapFielName, $bwVals.get(0), $bwVals.get(1), parameters, index, ${p.getJavaTypeDisplay()});
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
