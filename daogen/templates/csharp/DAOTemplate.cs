using System;
using System.Data;
using platform.dao.client;
using platform.dao.param;

namespace {{dao.namespace}}
{
    public class {{dao.class_name}}
    {
        public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
            "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database={{dao.db_name}};");

        public static IClient dasClient = ClientFactory.CreateDasClient("{{dao.db_name}}", "user=kevin;password=kevin");

        {% for method in dao.methods %}
        // {{method.comment}}
        public {% if method.action == "select" %}IDataReader{% else %}int{% end %} {{method.name}}({% for i, p in enumerate(method.parameters) %}{{p.ptype}} {{p.name}}{% if i != len(method.parameters) - 1 %}, {% end %}{% end %})
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });
                {% for p in method.parameters %}
                parameters.Add(new StatementParameter { 
                    Name = "@{{p.fieldName}}", 
                    Direction = ParameterDirection.Input, 
                    DbType = DbType.{{value_type[p.ptype]}}, 
                    Value = {{p.name}} 
                    });
                {% end %}
                
                string sql = "{{method.sql}}"   ;

                //return client.Fetch(sql, parameters);
                {% if method.action == "select" %}
                return dasClient.Fetch(sql, parameters);
                {% else %}
                return dasClient.Execute(sql, parameters);
                {% end %}
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        {% end %}

        {% for method in dao.sp_methods %}
        // {{method.comment}}
        public int {{method.method_name}}({% for i, p in enumerate(method.parameters) %}{{p.ptype}} {{p.name}}{% if i != len(method.parameters) - 1 %}, {% end %}{% end %})
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });
                {% for p in method.parameters %}
                    parameters.Add(new StatementParameter { 
                        Name = "@{{p.fieldName}}", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.{{value_type[p.ptype]}}, 
                        Value = {{p.name}} 
                        });
                {% end %}
                
                string sp = "{{method.sp_name}}"   ;

                //return client.Execute(sql, parameters);

                return dasClient.ExecuteSp(sp, parameters);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        {% end %}
    }
}
