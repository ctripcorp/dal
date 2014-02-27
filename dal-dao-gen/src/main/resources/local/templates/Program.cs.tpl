using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using ${namespace};
using ${namespace}IDao;

namespace ${namespace}Dao
{
    class Program
    {
        static void Main(string[] args)
        {
        //以下方法的主要目的是教会您如何使用DAL
        //在实际使用的时候，您需要根据不同的情形
        //反注释相应的代码，并传入合法的参数
        //-------其他可用的方法，VS的intellisense会告诉您的---------
#foreach($clazz in $clazzList)
			I${clazz}Dao ${WordUtils.uncapitalize($clazz)}Dao = DALFactory.${clazz}Dao;

			//int result = ${WordUtils.uncapitalize($clazz)}Dao.Insert${clazz}(new ${clazz}());
			//int result = ${WordUtils.uncapitalize($clazz)}Dao.Update${clazz}(new ${clazz}());
			//int result = ${WordUtils.uncapitalize($clazz)}Dao.Delete${clazz}(new ${clazz}());
			//$clazz entity = ${WordUtils.uncapitalize($clazz)}Dao.FindByPk(id);
			//IList<${clazz}> entities = ${WordUtils.uncapitalize($clazz)}Dao.GetAll();
			//long count = ${WordUtils.uncapitalize($clazz)}Dao.Count();
			//IList<${clazz}> listByPage = ${WordUtils.uncapitalize($clazz)}Dao.GetListByPage(obj, pagesize, pageno);
#end
        }
    }
}
