
from daogen.model.project_model import project_model_obj
from daogen.model.task_model import task_model_obj

from daogen.template import Loader
import os, shutil, re

templates_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), 
	"templates")

in_pattern = re.compile(r"\sIN\s", re.I)

class Method(object):
	comment = None
	method_name = None
	sql = ""
	action = "fetch"
	extra = None

class SPMethods(object):
	comment = None
	method_name = None
	sp_name = None
	action = "fetch"

class JavaGenerator(object):

	def __init__(self):
		self.common_loader = None
		self.common_templates = {}
		self.table_dao_template = None
		self.sp_dao_template = None
		self.freesql_dao_template = None
		self.entity_template = None
		self.pom_template = None

		self.projects_dir = os.path.join(
			os.path.dirname(
				os.path.abspath(
					os.path.dirname(__file__))),
			"projects")

		self.mkdir_if_not_exists(self.projects_dir)
		
		#project = project_model_obj.retrieve_alias(project_id)
		self.tmpl_loader = Loader(os.path.join(templates_dir, "java/tmpl"))
		self.table_dao_template = self.tmpl_loader.load("TableDAOTemplate.java")
		self.sp_dao_template = self.tmpl_loader.load("SPTemplate.java")
		self.freesql_dao_template = self.tmpl_loader.load("FreeSQLTemplate.java")
		self.entity_template = self.tmpl_loader.load("POJOTemplate.java")
		self.pom_template = self.tmpl_loader.load("pom.xml")

		self.common_loader = Loader(os.path.join(templates_dir, "java/dao"))

	def mkdir_if_not_exists(self, parent, child=None):
		whole_path = parent
		if child:
			whole_path = os.path.join(parent, child)

		if not os.path.exists(whole_path):
			os.mkdir(whole_path)

		return whole_path

	def generate_proj_struct(self, project_id):
		current_project = self.mkdir_if_not_exists(self.projects_dir, project_id)

		src_dir = self.mkdir_if_not_exists(current_project, "src")

		main_dir = self.mkdir_if_not_exists(src_dir, "main")

		test_dir = self.mkdir_if_not_exists(src_dir, "test")

		java_dir = self.mkdir_if_not_exists(main_dir, "java")

		#resources_dir = self.mkdir_if_not_exists(main_dir, "resources")
		if os.path.exists(os.path.join(main_dir, "resources")):
			shutil.rmtree(os.path.join(main_dir, "resources"))
		shutil.copytree(os.path.join(templates_dir, "java/resources"), 
			os.path.join(main_dir, "resources"))

		com_dir = self.mkdir_if_not_exists(java_dir, "com")

		ctrip_dir = self.mkdir_if_not_exists(com_dir, "ctrip")

		return ctrip_dir

	def generate_tree(self, project, src, dst):
		names = os.listdir(src)

		self.mkdir_if_not_exists(dst)

		for name in names:
			srcname = os.path.join(src, name)
			dstname = os.path.join(dst, name)

			if os.path.isdir(srcname):
				self.generate_tree(project, srcname, dstname)
			else:
				src_abs_name = os.path.abspath(srcname)
				if not src_abs_name in self.common_templates:
					self.common_templates[src_abs_name] = self.common_loader.load(src_abs_name)
				with open(dstname, 'w') as f:
					f.write(self.common_templates[src_abs_name].generate(
						product_line="com.ctrip."+project["product_line"],
						domain=project["domain"],
						app_name=project["service"]
						))

	def generate_concrete_code(self, project_id, ctrip_dir):

		def replace_holder(match_obj):
			return "%s"

		def normalize_holder(match_obj):
			return "?"

		project = project_model_obj.retrieve_alias(project_id)

		with open(os.path.join(os.path.join(self.projects_dir, project_id), "pom.xml"), "w") as f:
			f.write(self.pom_template.generate(
					product_line="com.ctrip."+project["product_line"],
					domain=project["domain"],
					app_name=project["service"],
					version=project["version"]
				))

		product_line_dir = self.mkdir_if_not_exists(ctrip_dir, project["product_line"])

		domain_dir = self.mkdir_if_not_exists(product_line_dir, project["domain"])

		service_dir = self.mkdir_if_not_exists(domain_dir, project["service"])

		dao_dir = self.mkdir_if_not_exists(service_dir, "dao")

		self.generate_tree(project, os.path.join(templates_dir, "java/dao"), dao_dir)

		#auto generated sql tasks here

		auto_task = task_model_obj.get_auto_sql(project_id)

		group_by_table = {}
		for s in auto_task:
			if s["table"] in group_by_table:
				group_by_table[s["table"]].append(s)
			else:
				group_by_table[s["table"]]= [s,]

		for table, tasks in group_by_table.items():
			methods = []
			sp_methods = []
			for task in tasks:
				sql_or_sp = task["crud"] == "select" or task["cud"] == "sql"
				method = Method() if sql_or_sp else SPMethods()
				method.comment = None
				method.method_name = task["func_name"]
				if sql_or_sp:
					if in_pattern.search(task["sql"]):
						method.extra = True
						method.sql = re.sub(r"\?", replace_holder, task["sql"])
					else:
						method.sql = task["sql"]
				else:
					method.sp_name = task["sp_name"]
				method.action = "fetch" if task["crud"] == "select" else "execute"

				if sql_or_sp:
					methods.append(method)
				else:
					sp_methods.append(method)

			with open(os.path.join(dao_dir,"%sDAO.java" % table), "w") as f:
				f.write(self.table_dao_template.generate(
					product_line="com.ctrip."+project["product_line"],
					domain=project["domain"],
					app_name=project["service"],
					dao_name="%sDAO" % table,
					methods=methods,
					sp_methods = sp_methods
					))

		#execute or fetch from stored procedure
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
				sp_methods.append(method)

			with open(os.path.join(dao_dir,"%sSPDAO.java" % db), "w") as f:
				f.write(self.sp_dao_template.generate(
					product_line="com.ctrip."+project["product_line"],
					domain=project["domain"],
					app_name=project["service"],
					database_name="%sSPDAO" % db,
					sp_methods = sp_methods
					))

		#free form sql here
		free_task = task_model_obj.get_free_sql(project_id)
		group_by_daoname = {}
		for t in free_task:
			if t["dao_name"] in group_by_daoname:
				group_by_daoname[t["dao_name"]].append(t)
			else:
				group_by_daoname[t["dao_name"]]= [t,]

		for dao_name, tasks in group_by_daoname.items():
			methods = []
			for task in tasks:
				method = Method()
				method.comment = None
				method.method_name = task["func_name"]
				sql = re.sub(r"[@|:]\w+", normalize_holder, task["sql"])
				if in_pattern.search(task["sql"]):
					method.extra = True
					method.sql = re.sub(r"\?", replace_holder, sql)
				else:
					method.sql = sql

				methods.append(method)

			with open(os.path.join(dao_dir,"%s.java" % dao_name), "w") as f:
				f.write(self.freesql_dao_template.generate(
					product_line="com.ctrip."+project["product_line"],
					domain=project["domain"],
					app_name=project["service"],
					dao_name=dao_name,
					methods = methods
					))

	def generate(self, project_id):
		"""
		Entry point of java generator
		"""
		self.generate_concrete_code(project_id, self.generate_proj_struct(project_id))

generator = JavaGenerator()

# if __name__ == '__main__':
# 	JavaGenerator().generate("521c116928afd21e3c124d69")