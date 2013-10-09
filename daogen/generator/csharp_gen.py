
from daogen.model.project_model import project_model_obj
from daogen.model.task_model import task_model_obj
from daogen.model.field_type_model import field_type_model_obj, sp_param_type_model_obj

from tornado.template import Loader
import os, shutil, re

templates_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), 
	"templates")

in_pattern = re.compile(r"\sIN\s", re.I)

lower_first = lambda s: s[:1].lower() + s[1:] if s else ''

csharp_sql_map = {
	"int": "int",
	"varchar": "string",
	"datetime": "DateTime",
	"nvarchar": "string"
}

value_type = {
    # "string":	"AnsiString",
 #    "int":	"Binary",
 #    "int":	"Byte",
 #    "int":	"Boolean",
 #    "int":	"Currency",
 #    "int":	"Date",
    "DateTime":	"DateTime",
 #    "int":	"Decimal",
 #    "int":	"Double",
 #    "int":	"Guid",
 #    "int":	"Int16",
    "int":	"Int32",
 #    "int":	"Int64",
 #    "int":	"Object",
 #    "int":	"Single",
    "string":	"String",
 #    "int":	"Time",
 #    "int":	"AnsiStringFixedLength",
 #    "int":	"StringFixedLength",
 #    "int":	"Xml",
 #    "int":	"DateTime2",
 #    "int":	"DateTimeOffset",
 #    "int":	"SByte",
 #    "int":	"UInt16",
 #    "int":	"UInt32",
 #    "int":	"UInt64",
 #    "int":	"VarNumeric"
}

class Parameter(object):
	ptype = None
	name = None
	fieldName = None

class DAO(object):
	namespace = None
	class_name = None
	db_name = None
	methods = None

class Method(object):
	comment = None
	name = None
	sql = ""
	action = "fetch"
	extra = None

class SPMethods(object):
	comment = None
	method_name = None
	sp_name = None
	action = "fetch"

class CSharpGenerator(object):

	def __init__(self):

		self.projects_dir = os.path.join(
			os.path.dirname(
				os.path.abspath(
					os.path.dirname(__file__))),
			"projects")

		self.mkdir_if_not_exists(self.projects_dir)
		
		#project = project_model_obj.retrieve_alias(project_id)
		self.tmpl_loader = Loader(os.path.join(templates_dir, "csharp"))
		self.dao_template = self.tmpl_loader.load("DAOTemplate.cs")
		self.sp_dao_template = self.tmpl_loader.load("SPTemplate.cs")
		self.freesql_dao_template = self.tmpl_loader.load("SQLTemplate.cs")
		#self.poco_template = self.tmpl_loader.load("POCOTemplate.cs")

	def mkdir_if_not_exists(self, parent, child=None):
		whole_path = parent
		if child:
			whole_path = os.path.join(parent, child)

		if not os.path.exists(whole_path):
			os.mkdir(whole_path)

		return whole_path

	def generate(self, project_id):

		project_dir = self.mkdir_if_not_exists(self.projects_dir, project_id)

		project = project_model_obj.retrieve_alias(project_id)

		#auto generated sql tasks here

		auto_task = task_model_obj.get_auto_sql(project_id)

		group_by_table = {}
		for s in auto_task:
			if s["table"] in group_by_table:
				group_by_table[s["table"]].append(s)
			else:
				group_by_table[s["table"]]= [s,]

		dao = DAO()

		dao.namespace = "%s.%s.%s" % (project["product_line"], project["domain"], project["service"])
		if len(group_by_table) > 0:
			first_value = group_by_table[group_by_table.keys()[0]]
			if len(first_value) > 0:
				dao.db_name = first_value[0]["database"]

		for table, tasks in group_by_table.items():
			methods = []
			sp_methods = []
			types = field_type_model_obj.get_by_table(dao.db_name, table)
			field_type = types["fields"]
			pk = types["primary_key"]

			dao.class_name = "%sDAO" % table
			
			for task in tasks:
				#cud is done through stored procedure or raw sql
				sql_or_sp = task["crud"] == "select" or task["cud"] == "sql"
				method = None

				parameters = []
				if sql_or_sp:
					method = Method()
					method.comment = None
					method.name = task["func_name"]
					method.action = task["crud"]
					method.sql = task["sql"]

					if method.action in ["select", "delete"]:
						for k, v in task["where"].items():
							parameter = Parameter()
							parameter.name = lower_first(k)
							parameter.fieldName = k
							parameter.ptype = csharp_sql_map[field_type[k]]
							parameters.append(parameter)
					elif method.action == "insert":
						for k in task["fields"]:
							parameter = Parameter()
							parameter.name = lower_first(k)
							parameter.fieldName = k
							parameter.ptype = csharp_sql_map[field_type[k]]
							parameters.append(parameter)
					else:
						for k in task["fields"]:
							parameter = Parameter()
							parameter.name = "set%s" % k.capitalize()
							parameter.fieldName = k
							parameter.ptype = csharp_sql_map[field_type[k]]
							parameters.append(parameter)
						for k, v in task["where"].items():
							parameter = Parameter()
							parameter.name = "where%s" % k.capitalize()
							parameter.fieldName = k
							parameter.ptype = csharp_sql_map[field_type[k]]
							parameters.append(parameter)

					methods.append(method)
				else:
					method = SPMethods()
					method.comment = None
					method.sp_name = task["sp_name"]
					method.method_name = task["func_name"]
					method.action = task["crud"]
					if method.action == "delete" and pk:
						parameter = Parameter()
						parameter.name = lower_first(pk)
						parameter.fieldName = pk
						parameter.ptype = csharp_sql_map[field_type[pk]]

						parameters.append(parameter)
					else:
						for k in task["fields"]:
							parameter = Parameter()
							parameter.name = lower_first(k)
							parameter.fieldName = k
							parameter.ptype = csharp_sql_map[field_type[k]]
							parameters.append(parameter)

					sp_methods.append(method)

				method.parameters = parameters

				dao.methods = methods
				dao.sp_methods = sp_methods

			with open(os.path.join(project_dir,"%sDAO.cs" % table), "w") as f:
				f.write(self.dao_template.generate(
					dao=dao,
					value_type = value_type
					))

		dao = DAO()

		dao.namespace = "%s.%s.%s" % (project["product_line"], project["domain"], project["service"])
		# #execute or fetch from stored procedure
		sp_task = task_model_obj.get_sp(project_id)

		group_by_database = {}
		for t in sp_task:
			if t["database"] in group_by_database:
				group_by_database[t["database"]].append(t)
			else:
				group_by_database[t["database"]]= [t,]

		for db, tasks in group_by_database.items():
			sp_methods = []
			for task in tasks:
				method = SPMethods()
				method.comment = None
				method.method_name = task["sp_name"][task["sp_name"].find(".")+1:]
				method.sp_name = task["sp_name"]
				method.action = task["sp_action"]

				parameters = []
				params = sp_param_type_model_obj.get_by_sp(db, method.sp_name)["params"]
				for p in params:
					parameter = Parameter()
					parameter.name = p.keys()[0][1:]
					parameter.fieldName = parameter.name
					parameter.ptype = csharp_sql_map[p[p.keys()[0]]]
					parameters.append(parameter)
				method.parameters = parameters

				sp_methods.append(method)

			dao.db_name = db
			dao.sp_methods = sp_methods
			dao.class_name = "%sSPDAO" % db

			with open(os.path.join(project_dir,"%sSPDAO.cs" % db), "w") as f:
				f.write(self.sp_dao_template.generate(
					dao=dao,
					value_type = value_type
					))

		dao = DAO()

		dao.namespace = "%s.%s.%s" % (project["product_line"], project["domain"], project["service"])
		# #free form sql here
		free_task = task_model_obj.get_free_sql(project_id)

		group_by_daoname = {}
		for t in free_task:
			if t["dao_name"] in group_by_daoname:
				group_by_daoname[t["dao_name"]].append(t)
			else:
				group_by_daoname[t["dao_name"]]= [t,]

		pattern = re.compile(r"[@|:](\w+)")

		for dao_name, tasks in group_by_daoname.items():
			methods = []
			for task in tasks:
				dao.db_name = task["database"]
				method = Method()
				method.comment = None
				method.name = task["func_name"]
				method.sql = task["sql"]

				parameters = []

				for g in pattern.findall(method.sql):
					parameter = Parameter()
					parameter.name = g
					parameter.fieldName = g
					parameter.ptype = "string"
					parameters.append(parameter)

				method.parameters = parameters

				methods.append(method)

			dao.class_name = dao_name
			dao.methods = methods

			with open(os.path.join(project_dir,"%s.cs" % dao_name), "w") as f:
				f.write(self.freesql_dao_template.generate(
					dao=dao,
					value_type = value_type
					))

generator = CSharpGenerator()