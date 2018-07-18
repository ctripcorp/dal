#if($method.hasParameters())
#set($bwVals = []) 
#foreach($p in $method.getParameters())
#if(${p.getConditionType()} == "LeftBracket")
				query.LeftBracket();
#end
#if(${p.getConditionType()} == "RightBracket")
				query.RightBracket();
#end
#if(${p.getConditionType()} == "And")
				query.And();
#end
#if(${p.getConditionType()} == "Or")
				query.Or();
#end
#if(${p.getConditionType()} == "Not")
				query.Not();
#end
#if(${p.getConditionType()} == "Equal" && ${p.isNullable()})
				query.EqualNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Equal" && !${p.isNullable()})
				query.Equal("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "NotEqual" && ${p.isNullable()})
				query.NotEqualNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "NotEqual" && !${p.isNullable()})
				query.NotEqual("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Great" && ${p.isNullable()})
				query.GreaterThanNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Great" && !${p.isNullable()})
				query.GreaterThan("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Less" && ${p.isNullable()})
				query.LessThanNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Less" && !${p.isNullable()})
				query.LessThan("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && ${p.isNullable()})
				query.GreaterThanEqualsNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && !${p.isNullable()})
				query.GreaterThanEquals("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && ${p.isNullable()})
				query.LessThanEqualsNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && !${p.isNullable()})
				query.LessThanEquals("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Like" && ${p.isNullable()})
				query.LikeNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "Like" && !${p.isNullable()})
				query.Like("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "In" && ${p.isNullable()})
				query.InNullable("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "In" && !${p.isNullable()})
				query.In("${p.getName()}", ${p.getNameToFirstLetterLower()});
#end
#if(${p.getConditionType()} == "IsNull")
				query.IsNull("${p.getName()}");
#end
#if(${p.getConditionType()} == "IsNotNull")
				query.IsNotNull("${p.getName()}");
#end
#if(${p.getConditionType()} == "Between")
#set($success = $bwVals.add(${p.getNameToFirstLetterLower()}))
#end
#if(${p.getConditionType()} == "Between" && ${p.isNullable()} && $bwVals.size()==2)
				query.BetweenNullable("${p.getName()}", $bwVals.get(0), $bwVals.get(1));
#set($bwVals = [])
#end
#if(${p.getConditionType()} == "Between" && !${p.isNullable()} && $bwVals.size()==2)
				query.Between("${p.getName()}", $bwVals.get(0), $bwVals.get(1));
#set($bwVals = [])
#end
#end
#end