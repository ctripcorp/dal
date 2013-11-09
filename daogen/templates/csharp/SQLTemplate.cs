using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace {{dao.namespace}}
{
    public class {{dao.class_name}} : AbstractDAO
    {
        public {{dao.class_name}}()
        {
            //注释掉此行或者赋值为string.Empty，然后配置connectionString来直连数据库
            PhysicDbName = "{{dao.db_name}}";
            ServicePort = 9000;
            CredentialID = "30303";
            base.Init();
        }

        {% for method in dao.methods %}
        // {{method.comment}}
        public IDataReader {{method.name}}({% for i, p in enumerate(method.parameters) %}{{p.ptype}} {{p.name}}{% if i != len(method.parameters) - 1 %}, {% end %}{% end %})
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
 
                {% for p in method.parameters %}
                parameters.Add(new ConcreteParameter() { 
                    DbType = DbType.{{value_type[p.ptype]}},
                    Name = "@{{p.fieldName}}",
                    Direction = ParameterDirection.Input,
                    Index = 0,
                    IsNullable =false,
                    IsSensitive = false,
                    Size  = 50,
                    Value = {{p.name}}
                    });
                {% end %}
                
                string sql = "{{method.sql}}";

                return this.Fetch(sql, parameters);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        {% end %}
    }
}
