using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using ${host.getNameSpace()}.Entity.DataModel;
using ${host.getNameSpace()}.Dao;

namespace ${host.getNameSpace()}.Test
{
	//在实际使用的时候，您需要根据不同的情形初始化参数值并反注释函数来运行test case
	[TestClass]
    public class ${host.getClassName()}UnitTest
    {
		${host.getClassName()}Dao ${WordUtils.uncapitalize($host.getClassName())}Dao = DALFactory.${host.getClassName()}Dao;

##free sql cud		
#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="update")
        [TestMethod]
        public void Test${method.getName()}()
		{
#foreach ($p in $method.getParameters())
#if ($method.paramTypeIsNotNull($p))
		    //${p.getType()} ${WordUtils.uncapitalize($p.getAlias().replace("@",""))};
#end
#end
#if ($method.isPaging())
		    //int pageNo;
			//int pageSize;
#end
	        //int ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach ($p in $method.getParameters())${WordUtils.uncapitalize($p.getAlias().replace("@",""))}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging()),pageNo,pageSize#end);
		}
		
#end	
#end
##free sql firstOrSingle
#foreach($method in $host.getMethods())
#if($method.isFirstOrSingle() && $method.getCrud_type()!="update")
		[TestMethod]
        public void Test${method.getName()}()
		{
#foreach ($p in $method.getParameters())
#if ($method.paramTypeIsNotNull($p))
		    //${p.getType()} ${WordUtils.uncapitalize($p.getAlias().replace("@",""))};
#end
#end
#if ($method.isPaging())
		    //int pageNo;
			//int pageSize;
#end
#if($method.isSampleType())
	        //object ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach ($p in $method.getParameters())${WordUtils.uncapitalize($p.getAlias().replace("@",""))}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging()),pageNo,pageSize#end);
#else
		    //${method.getPojoName()} ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach ($p in $method.getParameters())${WordUtils.uncapitalize($p.getAlias().replace("@",""))}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging()),pageNo,pageSize#end);
#end	
		}
		
#end	
#end
##free sql list
#foreach($method in $host.getMethods())
#if(!$method.isFirstOrSingle() && $method.getCrud_type()!="update")
		[TestMethod]
        public void Test${method.getName()}()
		{
#foreach ($p in $method.getParameters())
#if ($method.paramTypeIsNotNull($p))
		    //${p.getType()} ${WordUtils.uncapitalize($p.getAlias().replace("@",""))};
#end
#end
#if ($method.isPaging())
		    //int pageNo;
			//int pageSize;
#end
	        //IList<${method.getPojoName()}> ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach ($p in $method.getParameters())${WordUtils.uncapitalize($p.getAlias().replace("@",""))}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging()),pageNo,pageSize#end);
		}
		
#end	
#end	
    }
}