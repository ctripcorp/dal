#if($method.hasParameters())
#set($bwVals = []) 
#set($first = "")
#foreach($p in $method.getParameters())
#if(${p.getConditionType()} == "Equal")
				query.$!{first}Constrain(p => p.${p.getName()}).Equal(${p.getNameToFirstLetterLower()});
#end	
#if(${p.getConditionType()} == "NotEqual" ) 
				query.$!{first}Constrain(p => p.${p.getName()}).NotEqual(${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Great" )
				query.$!{first}Constrain(p => p.${p.getName()}).Greater(${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "lessThan" ) 
				query.$!{first}Constrain(p => p.${p.getName()}).GreaterEqual(${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" ) 
				query.$!{first}Constrain(p => p.${p.getName()}).Less(${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "LessAndEqual" ) 
				query.$!{first}Constrain(p => p.${p.getName()}).LessEqual(${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Like" ) 
				query.$!{first}Constrain(p => p.${p.getName()}).Like(${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "In" ) 
				query.$!{first}Constrain(p => p.${p.getName()}).In(${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "IsNull" ) 
				query.$!{first}Constrain(p => p.${p.getName()}).Equal(null);
#end
#if(${p.getConditionType()} == "IsNotNull" ) 
				query.$!{first}Constrain(p => p.${p.getName()}).NotEqual(null);
#end
#if(${p.getConditionType()} == "Between")
#set($success = $bwVals.add(${p.getNameToFirstLetterLower()}))
#end
#if(${p.getConditionType()} == "Between" && $bwVals.size()==2)
				query.$!{first}Constrain(p => p.${p.getName()}).Between($bwVals.get(0), $bwVals.get(1));
#set($bwVals = [])		
#end
#if(${p.getConditionType()} == "Between" )
#if($first=="" && $bwVals.size()!=1 && $bwVals.size()!=2)
#set($first = "And().")
#end
#else
#set($first = "And().")
#end
#end
#end