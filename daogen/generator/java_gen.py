
from daogen.model.project_model import project_model_obj
from daogen.model.task_model import task_model_obj

from daogen.template import Loader
import os, shutil

templates_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), 
	"templates")

class Method(object):
	comment = None
	name = None
	paramCount= 0
	sql = ""
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
		project = project_model_obj.retrieve_alias(project_id)

		with open(os.path.join(os.path.join(self.projects_dir, project_id), "pom.xml"), "w") as f:
			f.write(self.pom_template.generate(
					product_line="com.ctrip."+project["product_line"],
					domain=project["domain"],
					app_name=project["service"]
				))

		product_line_dir = self.mkdir_if_not_exists(ctrip_dir, project["product_line"])

		domain_dir = self.mkdir_if_not_exists(product_line_dir, project["domain"])

		service_dir = self.mkdir_if_not_exists(domain_dir, project["service"])

		dao_dir = self.mkdir_if_not_exists(service_dir, "dao")

		self.generate_tree(project, os.path.join(templates_dir, "java/dao"), dao_dir)

		auto_sql = task_model_obj.get_auto_sql(project_id)

		group_by_table = {}
		for s in auto_sql:
			if s["table"] in group_by_table:
				group_by_table[s["table"]].append(s)
			else:
				group_by_table[s["table"]]= [s,]

		print group_by_table

		for k, v in group_by_table.items():
			methods = []
			for sql in v:
				method = Method()
				method.comment = None
				method.name = sql["func_name"]
				method.paramCount = len(sql["where"])
				method.sql = self.format_sql(sql)
				method.action = "fetch" if sql["crud"] == "select" else "execute"
				methods.append(method)
			with open(os.path.join(dao_dir,"tableview/%sDAO.java" % k), "w") as f:
				f.write(self.table_dao_template.generate(
					product_line="com.ctrip."+project["product_line"],
					domain=project["domain"],
					app_name=project["service"],
					TableName=k,
					methods=methods
					))

	def format_sql(self, sql_meta):
		if sql_meta["crud"] == "select":
			return "SELECT %s FROM %s WHERE %s" % (",".join(sql_meta["fields"]),
				sql_meta["table"], "%s = ?" % " = ? ".join(sql_meta["where"].keys()))
		elif sql_meta["crud"] == "insert":
			return "INSERT INTO %s (%s) VALUES (%s)" % (sql_meta["table"], 
				",".join(sql_meta["fields"]), 
				",".join(["?" for i in range(len(sql_meta["fields"]))]))
		elif sql_meta["crud"] == "update":
			return "UPDATE %s SET %s WHERE %s" % (
					sql_meta["table"],
					"%s = ?" % " = ? ".join(sql_meta["fields"]),
					"%s = ?" % " = ? ".join(sql_meta["where"].keys())
				)
		else:
			return "DELETE FROM %s WHERE %s" % (
				sql_meta["table"],
				"%s = ?" % " = ? ".join(sql_meta["where"].keys())
				)

	def generate(self, project_id):
		"""
		Entry point of java generator
		"""
		self.mkdir_if_not_exists(self.projects_dir)
		
		#project = project_model_obj.retrieve_alias(project_id)
		self.tmpl_loader = Loader(os.path.join(templates_dir, "java/tmpl"))
		self.table_dao_template = self.tmpl_loader.load("TableViewTemplate.java")
		self.sp_dao_template = self.tmpl_loader.load("SPTemplate.java")
		self.freesql_dao_template = self.tmpl_loader.load("FreeSQLTemplate.java")
		self.entity_template = self.tmpl_loader.load("EntityTemplate.java")
		self.pom_template = self.tmpl_loader.load("pom.xml")

		self.common_loader = Loader(os.path.join(templates_dir, "java/dao"))

		self.generate_concrete_code(project_id, self.generate_proj_struct(project_id))

generator = JavaGenerator()

# if __name__ == '__main__':
# 	JavaGenerator().generate("521c116928afd21e3c124d69")