## below is build sql method unit test case
#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "delete" || $method.getCrud_type() == "insert")
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
#if (!${p.isOperator()})
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#end
	    //int ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "update")
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getUpdateSetParameters())
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#foreach($p in $method.getParameters())
#if (!${p.isOperator()})
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#end
	    //int ret = dao.${method.getName()}(${method.getUpdateParameterNames("")});
	}
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型且返回First
#if($method.isReturnFirst() && !$method.isSampleType())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
#if (!${p.isOperator()})
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#end
	    //${host.getPojoClassName()} ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型并且返回值为List
#if($method.isReturnList() && !$method.isSampleType())

	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
#if (!${p.isOperator()})
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#end
	    //List<${host.getPojoClassName()}> ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型且返回Single
#if($method.isReturnSingle() && !$method.isSampleType())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
#if (!${p.isOperator()})
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#end
	    //${host.getPojoClassName()} ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
#if (!${p.isOperator()})
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#end
	    //${host.getPojoClassName()} ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end
##
#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##简单类型并且返回值是List
#if($method.isSampleType() && $method.isReturnList())

	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
#if (!${p.isOperator()})
		//${p.getClassDisplayName()} ${p.getAlias()} = ${p.getValidationValue()};// Test value here
#end
#end
	    //List<${method.getPojoClassName()}> ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end

#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
	
	@Test
	public void test${method.getName()}() throws Exception {
#foreach($p in $method.getParameters())
#if (!${p.isOperator()})
		//${p.getClassDisplayName()} ${p.getAlias()} = $!{p.getValidationValue()};// Test value here
#end
#end
	    //${method.getPojoClassName()} ret = dao.${method.getName()}(${method.getParameterNames("")});
	}
#end
#end
#end