
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
		[TestMethod]
        public void TestExec${host.getClassName()}()
        {
			//${host.getClassName()} ${WordUtils.uncapitalize(${host.getClassName()})};
            //int ret = DALFactory.${host.getClassName()}Dao.Exec${host.getClassName()}(${WordUtils.uncapitalize(${host.getClassName()})});
        }
        
    }
}
