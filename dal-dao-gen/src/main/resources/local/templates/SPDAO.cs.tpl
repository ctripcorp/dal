using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace $namespace
{
    public class $dao_name : AbstractDAO
    {
        public $dao_name()
        {
            //注释掉此行或者赋值为string.Empty，然后配置connectionString来直连数据库
            PhysicDbName = "$database";
            ServicePort = 9000;
            CredentialID = "30303";
            base.Init();
        }

        #foreach( $method in $sp_methods )
        #set($parameters = $method.getParameters())
public #if( $method.getAction() == "select" )IDataReader#{else}int#end ${method.getMethodName()}#[[(]]##foreach($p in $parameters)${p.getType()} ${p.getName()}#if($foreach.count != $parameters.size()), #end#end#[[)]]# {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                #foreach($p in $parameters)
                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@${p.getFieldName()}",
                        Direction = #if( $p.getParamMode() == "OUT" )ParameterDirection.InputOutput#{else}ParameterDirection.Input#end,
                        Index = ${p.getPosition()},
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = ${p.getName()}
                    });
                #end
                
                return this.#if( $method.getAction() == "select" )FetchBySP#{else}ExecuteSP#end("${method.getSqlSPName()}", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        #end
    }
}
