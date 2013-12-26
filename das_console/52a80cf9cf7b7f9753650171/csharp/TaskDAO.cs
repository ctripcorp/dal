using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace com.ctrip.flight.intl.engine
{
    public class TaskDAO : AbstractDAO
    {
        public TaskDAO()
        {
            //注释掉此行或者赋值为string.Empty，然后配置connectionString来直连数据库
            PhysicDbName = "Work";
            ServicePort = 9000;
            CredentialID = "30303";
            base.Init();
        }

        public IDataReader get(int TID, ${p.getType()} CompletionTime, ${p.getType()} Operator, ${p.getType()} Title) {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.Int32,
                        Name = "@TID",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = TID
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@CompletionTime",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = CompletionTime
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Operator",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Operator
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Title",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Title
                    });
                                
                return this.Fetch("SELECT TID,CompletionTime,Operator,Title,Detail FROM Task     WHERE  TID = @TID AND CompletionTime <= @CompletionTime AND Operator = @Operator AND Title Like @Title ", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
            }
}
