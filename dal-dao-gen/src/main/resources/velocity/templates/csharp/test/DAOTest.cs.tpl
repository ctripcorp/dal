using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using ${host.getNameSpace()}.Entity.DataModel;
using ${host.getNameSpace()}.Interface.IDao;
using ${host.getNameSpace()}.Dao;

namespace ${host.getNameSpace()}.Test
{
    public class ${host.getClassName()}Test
    {
        public static void Test()
        {
            //以下方法的主要目的是教会您如何使用DAL
            //在实际使用的时候，您需要根据不同的情形
            //反注释相应的代码，并传入合法的参数
            //-------其他可用的方法，VS的intellisense会告诉您的---------
            I${host.getClassName()}Dao ${WordUtils.uncapitalize($host.getClassName())}Dao = DALFactory.${host.getClassName()}Dao;

#if($host.isTable())

            //${host.getClassName()} orm = ${WordUtils.uncapitalize($host.getClassName())}Dao.OrmByHand("select * from table");

            //int insertResult = ${WordUtils.uncapitalize($host.getClassName())}Dao.Insert${host.getClassName()}(new ${host.getClassName()}());

#if($host.getPrimaryKeys().size() == 0)
        /*由于没有PK，不能生成Update和Delete方法
#end
            //int updateResult = ${WordUtils.uncapitalize($host.getClassName())}Dao.Update${host.getClassName()}(new ${host.getClassName()}());

            //int deleteResult = ${WordUtils.uncapitalize($host.getClassName())}Dao.Delete${host.getClassName()}(new ${host.getClassName()}());

#if($host.isSpa())
            //int deleteByFieldResult = ${WordUtils.uncapitalize($host.getClassName())}Dao.Delete${host.getClassName()}(#foreach($p in $host.getPrimaryKeys())#if($p.isValueType())0#{else}null#if($foreach.count != $host.getPrimaryKeys().size()),#end#end#end);
#end

#if($host.getPrimaryKeys().size() == 0)
        */
#end

#if($host.getPrimaryKeys().size() >= 1)
            //var resultsByPk = ${WordUtils.uncapitalize($host.getClassName())}Dao.FindByPk(#foreach($p in $host.getPrimaryKeys())#if($p.isValueType())0#{else}null#if($foreach.count != $host.getPrimaryKeys().size()),#end#end#end);
#end
#end
            //var entities = ${WordUtils.uncapitalize($host.getClassName())}Dao.GetAll();

            //long count = ${WordUtils.uncapitalize($host.getClassName())}Dao.Count();

#if($host.isHasPagination())
            //var listByPage = ${WordUtils.uncapitalize($host.getClassName())}Dao.GetListByPage(null, 0, 0);

#end
#if($host.isHasSptI())
            //int bulkInsertResult = ${WordUtils.uncapitalize($host.getClassName())}Dao.BulkInsert${host.getClassName()}(new List<${host.getClassName()}>());

#end
#if($host.isHasSptU())
            //int bulkUpdateResult = ${WordUtils.uncapitalize($host.getClassName())}Dao.BulkUpdate${host.getClassName()}(new List<${host.getClassName()}>());

#end
#if($host.isHasSptD())
            //int bulkDeleteResult = ${WordUtils.uncapitalize($host.getClassName())}Dao.BulkDelete${host.getClassName()}(new List<${host.getClassName()}>());

#end
#foreach($method in $host.getExtraMethods())
            //var ${method.getName()}Result =  ${WordUtils.uncapitalize($host.getClassName())}Dao.${method.getName()}(#foreach($p in $method.getParameters())#if($p.isValueType())0#{else}null#if($foreach.count != $host.getPrimaryKeys().size()),#end#end#end);

#end
        }
    }
}
