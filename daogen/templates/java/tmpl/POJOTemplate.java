package {{product_line}}.{{domain}}.{{app_name}}.dao.entity;

public class {{TableName}}Entity {
	
	{% for field in fields %}

	private {{field.type}} {{field.name}};

	public void set{{field.name.capitalize()}}({{field.type}} {{field.name}}){
		this.{{field.name}} = {{field.name}};
	}

	public {{field.type}} get{{field.name.capitalize()}}(){
		return this.{{field.name}};
	}

	{% end %}


}
