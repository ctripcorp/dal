#if($method.hasParameters())
#set($bwVals = []) 
#set($first = "")
#foreach($p in $method.getParameters())
#if(${p.getConditionType()} == "Equal" && ${p.isNullable()})
				query.$!{first}EqualNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Equal" && !${p.isNullable()})
				query.$!{first}Equal("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "NotEqual" && ${p.isNullable()})
				query.$!{first}NotEqualNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "NotEqual" && !${p.isNullable()})
				query.$!{first}NotEqual("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Great" && ${p.isNullable()})
				query.$!{first}GreaterThanNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Great" && !${p.isNullable()})
				query.$!{first}GreaterThan("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "lessThan" && ${p.isNullable()})
				query.$!{first}LessThanNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "lessThan" && !${p.isNullable()})
				query.$!{first}LessThan("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && ${p.isNullable()})
				query.$!{first}GreaterThanEqualsNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && !${p.isNullable()})
				query.$!{first}GreaterThanEquals("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && ${p.isNullable()})
				query.$!{first}LessThanEqualsNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && !${p.isNullable()})
				query.$!{first}LessThanEquals("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Like" && ${p.isNullable()})
				query.$!{first}LikeNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Like" && !${p.isNullable()})
				query.$!{first}Like("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "In" && ${p.isNullable()})
				query.$!{first}InNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "In" && !${p.isNullable()})
				query.$!{first}In("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "IsNull")
				query.$!{first}Equal("${p.getName()}", null);
#end
#if(${p.getConditionType()} == "IsNotNull")
				query.$!{first}NotEqual("${p.getName()}", null);
#end
#if(${p.getConditionType()} == "Between")
#set($success = $bwVals.add(${p.getNameToFirstLetterLower()}))
#end
#if(${p.getConditionType()} == "Between" && $bwVals.size()==2)
				query.$!{first}Constrain("${p.getName()}").Between($bwVals.get(0), $bwVals.get(1));
#set($bwVals = [])		
#end
#if(${p.getConditionType()} == "Between")
#if($first=="" && $bwVals.size()!=1 && $bwVals.size()!=2)
#set($first = "And().")
#end
#else
#set($first = "And().")
#end
#if(${p.getConditionType()} == "And")
#set($first = "And().")
#end
#if(${p.getConditionType()} == "Or")
#set($first = "Or().")
#end
#if(${p.getConditionType()} == "Not")
#set($first = "Not().")
#end
#if(${p.getConditionType()} == "LeftBracket")
#set($first = "LeftBracket().")
#end
#if(${p.getConditionType()} == "RightBracket")
#set($first = "RightBracket().")
#end
#end
#end