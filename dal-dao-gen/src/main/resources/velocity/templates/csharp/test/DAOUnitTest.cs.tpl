using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using ${host.getNameSpace()}.Entity.DataModel;
using ${host.getNameSpace()}.Interface.IDao;
using ${host.getNameSpace()}.Dao;


namespace ${host.getNameSpace()}.Test
{
	//在实际使用的时候，您需要根据不同的情形初始化参数值并反注释函数来运行test case
	[TestClass]
    public class ${host.getClassName()}UnitTest
    {

		I${host.getClassName()}Dao ${WordUtils.uncapitalize($host.getClassName())}Dao = DALFactory.${host.getClassName()}Dao;

### standard dao unit test begin
#if($host.generateAPI(59) && $host.isHasSptD())
		[TestMethod]
        public void TestBulkDelete${host.getClassName()}()
        {
            //IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List;
			//int ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.BulkDelete${host.getClassName()}(${WordUtils.uncapitalize($host.getClassName())}List);
        }
		
#end
#if($host.generateAPI(54) && $host.isHasSptI())
	    [TestMethod]
        public void TestBulkInsert${host.getClassName()}()
        {
			//IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List;
			//int ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.BulkInsert${host.getClassName()}(${WordUtils.uncapitalize($host.getClassName())}List);
        }
		
#end
#if($host.generateAPI(56) && $host.isHasSptU())
	    [TestMethod]
        public void TestBulkUpdate${host.getClassName()}()
        {
			//IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List;
            //int ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.BulkUpdate${host.getClassName()}(${WordUtils.uncapitalize($host.getClassName())}List);
        }
		
#end		
#if($host.generateAPI(44,51))
        [TestMethod]
        public void TestCount()
        {
            long ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.Count();
        }
		
#end
#if($host.isTable() && $host.getPrimaryKeys().size() != 0 && $host.generateAPI(48,57,58,59))  
	    [TestMethod]
        public void TestDelete${host.getClassName()}1()
        {
            //${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())};
			//int ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.Delete${host.getClassName()}(${WordUtils.uncapitalize($host.getClassName())});
        }
		
#end
#if($host.isTable() && $host.getPrimaryKeys().size() != 0 && $host.isSpa() && $host.getSpaDelete().isExist() && $host.generateAPI(48,57,58))  
	    [TestMethod]
        public void TestDelete${host.getClassName()}2()
        {
#foreach ($p in $host.getSpaDelete().getParameters())
		    //${p.getType()} ${WordUtils.uncapitalize($p.getName().replace("@",""))};
#end
            //int ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.Delete${host.getClassName()}(#foreach ($p in $host.getSpaDelete().getParameters()) ${WordUtils.uncapitalize($p.getName().replace("@",""))}#if($foreach.count != $host.getSpaDelete().getParameters().size()),#end#end);
        }
		
#end
#if($host.isTable() && $host.generateAPI(42,49))      
#if($host.getPrimaryKeys().size() == 1)
#set($pk = $host.getPrimaryKeys().get(0))
	    [TestMethod]
        public void TestFindByPk()
        {
            //${pk.getType()} ${WordUtils.uncapitalize($pk.getName())};
			//${host.getClassName()} obj = ${WordUtils.uncapitalize($host.getClassName())}Dao.FindByPk(${WordUtils.uncapitalize($pk.getName())});
        }
		
#elseif($host.getPrimaryKeys().size() >= 2)
	    [TestMethod]
        public void TestFindByPk()
        {
#foreach ($cpk in $host.getPrimaryKeys())
		     //${cpk.getType()} ${WordUtils.uncapitalize($cpk.getName())};
#end
	         //${host.getClassName()} obj = ${WordUtils.uncapitalize($host.getClassName())}Dao.FindByPk(#foreach ($cpk in $host.getPrimaryKeys())${WordUtils.uncapitalize($cpk.getName())}#if($foreach.count != $host.getPrimaryKeys().size()),#end#end);
		}
		
#end
#end
#if($host.generateAPI(43,50))
	    [TestMethod]
        public void TestGetAll()
        {
            IList<${host.getClassName()}> obj = ${WordUtils.uncapitalize($host.getClassName())}Dao.GetAll();
        }
		
#end		
#if($host.generateAPI(45,52) && $host.isHasPagination())
        [TestMethod]
        public void TestGetListByPage()
        {
            //${host.getClassName()} obj;
			//int pagesize;
			//int pageNo;
			//IList<${host.getClassName()}> ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.GetListByPage(obj, pagesize, pageNo);
        }
		
#end
#if($host.isTable() && $host.generateAPI(46,53) && $host.isSpa())   
	    //sp insert
	    [TestMethod]
        public void TestInsert${host.getClassName()}()
        {
			//${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())};
            //int ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.Insert${host.getClassName()}(${WordUtils.uncapitalize($host.getClassName())});
        }
		
#end
#if($host.isTable() && $host.generateAPI(46,53) && !$host.isSpa())
	    //not sp insert
	    [TestMethod]
        public void TestInsert${host.getClassName()}()
        {
            //${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())};
			//${WordUtils.uncapitalize($host.getClassName())}Dao.Insert${host.getClassName()}(${WordUtils.uncapitalize($host.getClassName())});
        }
		
#end		
#if($host.isTable() && $host.getPrimaryKeys().size() != 0 && $host.generateAPI(47,55))  
        [TestMethod]
        public void TestUpdate${host.getClassName()}()
        {
			//${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())};
            //int ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.Update${host.getClassName()}(${WordUtils.uncapitalize($host.getClassName())});
        }
		
#end
### build sql dao unit test begin
## build sql for cud
#foreach($method in $host.getExtraMethods())
#if($method.getCrud_type() != "select")
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
##实体类型且返回First
#foreach($method in $host.getExtraMethods())
#if($method.isFirstOrSingle() && !$method.isSampleType())
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
			//${host.getClassName()} ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach ($p in $method.getParameters())${WordUtils.uncapitalize($p.getAlias().replace("@",""))}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging()),pageNo,pageSize#end);
		}
		
#end
#end
##实体类型或简单类型且返回分页List
#foreach($method in $host.getExtraMethods())
#if(!$method.isFirstOrSingle() && $method.getCrud_type() == "select" && $method.isPaging())
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
	        //IList<${host.getClassName()}> ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach ($p in $method.getParameters())${WordUtils.uncapitalize($p.getAlias().replace("@",""))}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging()),pageNo,pageSize#end);
		}
		
#end
#end	
##实体类型或简单类型且返回List
#foreach($method in $host.getExtraMethods())
#if(!$method.isFirstOrSingle() && $method.getCrud_type() == "select" && !$method.isPaging())
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
	        //IList<${host.getClassName()}> ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach ($p in $method.getParameters())${WordUtils.uncapitalize($p.getAlias().replace("@",""))}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging()),pageNo,pageSize#end);
		}
		
#end
#end	
##简单类型并且返回值是First
#foreach($method in $host.getExtraMethods())
#if($method.isFirstOrSingle() && $method.isSampleType())
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
	        //object ret = ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach ($p in $method.getParameters())${WordUtils.uncapitalize($p.getAlias().replace("@",""))}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging()),pageNo,pageSize#end);
		}
		
#end
#end	
    }
	
}
