using System;
using System.Data;
using platform.dao.client;
using platform.dao.param;

namespace {{dao.namespace}}
{
    public class {{dao.class_name}}
    {
        //public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
        //   "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database={{dao.db_name}};");

        public static IClient client = ClientFactory.CreateDasClient("{{dao.db_name}}", "user=kevin;password=kevin");

        {% for method in dao.methods %}
        // {{method.comment}}
        public {% if method.action == "select" %}IDataReader{% else %}int{% end %} {{method.name}}({% for i, p in enumerate(method.parameters) %}{{p.ptype}} {{p.name}}{% if i != len(method.parameters) - 1 %}, {% end %}{% end %})
        {
            try
            {
                {% set result_params = [] %}
                {% for p in method.parameters  %}
                {% set result_params.append("%sParam" % p.name) %}
                IParameter {{p.name}}Param = ParameterFactory.CreateValue(
                        "@{{p.fieldName}}",
                        {{p.name}},
                        direction : ParameterDirection.Input,
                        index : 0,
                        nullable :false,
                        sensitive : false,
                        size  :50
                    );
                {% end %}
                
                string sql = "{{method.sql}}"   ;

                //return client.Fetch(sql, parameters);
                {% if method.action == "select" %}
                return client.Fetch(sql, {{",".join(result_params)}});
                {% else %}
                return client.Execute(sql, {{",".join(result_params)}});
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
                {% set result_params = [] %}
                {% for p in method.parameters %}
                {% set result_params.append("%sParam" % p.name) %}
                IParameter {{p.name}}Param = ParameterFactory.CreateValue(
                        "@{{p.fieldName}}",
                        {{p.name}},
                        direction : ParameterDirection.Input,
                        index  :0,
                        nullable :false,
                        sensitive : false,
                        size  :50
                    );
                {% end %}
                
                string sp = "{{method.sp_name}}"   ;

                //return client.Execute(sql, parameters);

                return client.ExecuteSp(sp, {{",".join(result_params)}});
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        {% end %}
    }
}
