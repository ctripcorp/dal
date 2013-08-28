
import tornado.web
import pymssql
import json, bson
import StringIO
from daogen.model.project_model import project_model_obj
from daogen.model.task_model import task_model_obj

connection =  pymssql.connect(host=r'testdb.dev.sh.ctriptravel.com',
	port='28747', database='')

cursor = connection.cursor()

class TaskHandler(tornado.web.RequestHandler):

	def get(self):
		project_id = self.get_argument("project_id", default="521c116928afd21e3c124d69", strip=False)
		tasks = []
		project_alias = project_model_obj.retrieve_alias(project_id)["alias"]
		tasks = task_model_obj.get_by_project(project_id)
		self.render("../templates/task.html", 
			databases=self.get_databases(),
			project_id=project_id,
			project_name=project_alias,
			tasks=tasks)

	def post(self):
		project_id = self.get_argument("project_id", default=None, strip=False)
		task_object = self.get_argument("task_object", default=None, strip=False) 
		task_type = self.get_argument("task_type", default=None, strip=False) 

		if task_object is not None:
			task_object = json.loads(task_object)

		task_model_obj.insert(project_id, task_type, **task_object)

		self.write(json.dumps({"success": True}))
		self.finish()

	def get_databases(self):
		cursor.execute('use master select * from sysdatabases')

		row = cursor.fetchone()
		databases = []
		while row:
			databases.append(row[0])
			row = cursor.fetchone()

		return databases


	def get_table_names(self, db_name):
		cursor.execute("use %s select Name from sysobjects where xtype in ('v','u') and status>=0 order by name" % db_name)

		row = cursor.fetchone()
		tables = []
		while row:
			tables.append(row[0])
			row = cursor.fetchone()

		return tables

class TasksHandler(tornado.web.RequestHandler):
	def get(self):
		project_id = self.get_argument("project_id", default="521c116928afd21e3c124d69", strip=False)
		results = []
		for p in task_model_obj.get_by_project(project_id):
			if isinstance(p["_id"], bson.objectid.ObjectId):
				p["_id"] = str(p["_id"])
			results.append(p)

		self.write(json.dumps(results))
		self.finish()

class TableHandler(tornado.web.RequestHandler):
	def get(self):
		meta_type = self.get_argument("meta_type", default=None, strip=False)
		meta_value = self.get_argument("meta_value", default=None, strip=False)
		if meta_type == "tables":
			cursor.execute("use %s select Name from sysobjects where xtype in ('v','u') and status>=0 order by name" % meta_value)

			row = cursor.fetchone()
			tables = []
			while row:
				tables.append(row[0])
				row = cursor.fetchone()

			self.write(json.dumps(tables))
		elif meta_type == "fields":
			db_name = self.get_argument("db_name", default=None, strip = None)
			cursor.execute("use %s SELECT COLUMN_NAME,DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = '%s'" % (db_name, meta_value))

			row = cursor.fetchone()
			fields = []
			while row:
				fields.append({"name":row[0], "type": row[1]})
				row = cursor.fetchone()

			self.write(json.dumps(fields))
		elif meta_type == "sp":
			cursor.execute("use %s select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'" % meta_value)

			row = cursor.fetchone()
			fields = []
			while row:
				fields.append(row[0]+"."+row[1])
				row = cursor.fetchone()

			self.write(json.dumps(fields))
		elif meta_type == "sp_code":
			db_name = self.get_argument("db_name", default=None, strip = None)
			cursor.execute("use %s" % db_name)
			cursor.callproc('sp_HelpText', (meta_value,))

			output = StringIO.StringIO()

			for row in cursor:
				output.write(row[0])

			self.write(json.dumps(output.getvalue()))

		self.finish()