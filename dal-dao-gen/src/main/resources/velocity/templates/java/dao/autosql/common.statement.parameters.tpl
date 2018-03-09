#if($method.hasParameters())
#set($bwVals = [])
#foreach($p in $method.getParameters())
#if(${p.getConditionType()} == "LeftBracket")
		builder.leftBracket();
#end
#if(${p.getConditionType()} == "RightBracket")
		builder.rightBracket();
#end
#if(${p.getConditionType()} == "And")
		builder.and();
#end
#if(${p.getConditionType()} == "Or")
		builder.or();
#end
#if(${p.getConditionType()} == "Not")
		builder.not();
#end
#if(${p.getConditionType()} == "Equal" && ${p.isNullable()})
		builder.equalNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end	
#if(${p.getConditionType()} == "Equal" && !${p.isNullable()})
		builder.equal("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "NotEqual" && ${p.isNullable()})
		builder.notEqualNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "NotEqual" && !${p.isNullable()})
		builder.notEqual("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Great" && ${p.isNullable()})
		builder.greaterThanNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Great" && !${p.isNullable()})
		builder.greaterThan("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Less" && ${p.isNullable()})
		builder.lessThanNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Less" && !${p.isNullable()})
		builder.lessThan("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && ${p.isNullable()})
		builder.greaterThanEqualsNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "GreatAndEqual" && !${p.isNullable()})
		builder.greaterThanEquals("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && ${p.isNullable()})
		builder.lessThanEqualsNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "LessAndEqual" && !${p.isNullable()})
		builder.lessThanEquals("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Like" && ${p.isNullable()})
		builder.likeNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "Like" && !${p.isNullable()})
		builder.like("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "In" && ${p.isNullable()})
		builder.inNullable("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "In" && !${p.isNullable()})
		builder.in("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#end
#if(${p.getConditionType()} == "IsNull")
		builder.isNull("${p.getName()}");
#end
#if(${p.getConditionType()} == "IsNotNull")
		builder.isNotNull("${p.getName()}");
#end
#if(${p.getConditionType()} == "Between")
#set($success = $bwVals.add(${p.getAlias()}))
#end
#if(${p.getConditionType()} == "Between" && ${p.isNullable()} && $bwVals.size()==2)
		builder.betweenNullable("${p.getName()}", $bwVals.get(0), $bwVals.get(1), ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#set($bwVals = [])		
#end
#if(${p.getConditionType()} == "Between" && !${p.isNullable()} && $bwVals.size()==2)
		builder.between("${p.getName()}", $bwVals.get(0), $bwVals.get(1), ${p.getJavaTypeDisplay()}, ${p.isSensitive()});
#set($bwVals = [])		
#end
#end
#end
