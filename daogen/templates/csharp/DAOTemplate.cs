using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao.client;
using platform.dao.param;

namespace {{dao.namespace}}
{
    public class {{dao.class_name}} : AbstractDAO
    {
        public PersonDAO()
        {
            base.Init();
        }

        {% for method in dao.methods %}
        // {{method.comment}}
        public {% if method.action == "select" %}IDataReader{% else %}int{% end %} {{method.name}}({% for i, p in enumerate(method.parameters) %}{{p.ptype}} {{p.name}}{% if i != len(method.parameters) - 1 %}, {% end %}{% end %})
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                {% for p in method.parameters  %}
                IParameter {{p.name}}Param = ParameterFactory.CreateValue(
                        "@{{p.fieldName}}",
                        {{p.name}},
                        direction : ParameterDirection.Input,
                        index : 0,
                        nullable :false,
                        sensitive : false,
                        size  :50
                    );
                parameters.Add({{p.name}}Param);
                {% end %}
                
                string sql = "{{method.sql}}"   ;

                //return client.Fetch(sql, parameters);
                {% if method.action == "select" %}
                return this.Fetch(sql, parameters.ToArray());
                {% else %}
                return this.Execute(sql, parameters.ToArray());
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
                IList<IParameter> parameters = new List<IParameter>();
                {% for p in method.parameters  %}
                IParameter {{p.name}}Param = ParameterFactory.CreateValue(
                        "@{{p.fieldName}}",
                        {{p.name}},
                        direction : ParameterDirection.Input,
                        index  :0,
                        nullable :false,
                        sensitive : false,
                        size  :50
                    );
                parameters.Add({{p.name}}Param);
                {% end %}
                
                string sp = "{{method.sp_name}}"   ;

                //return client.Execute(sql, parameters);

                return this.ExecuteSp(sp, parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        {% end %}
    }
}
