
import tornado.web
import pymssql
import json
import StringIO

connection =  pymssql.connect(host=r'testdb.dev.sh.ctriptravel.com',
	port='28747', database='')

cursor = connection.cursor()

class TaskHandler(tornado.web.RequestHandler):
	def get(self):
		project_id = self.get_argument("project_id", default="", strip=False)
		tasks = []
		self.render("../templates/task.html", 
			databases=self.get_databases(),
			project_id=project_id,
			tasks=tasks)

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
			cursor.execute("use %s Select Name from SysColumns Where id=Object_Id('%s')" % (db_name, meta_value))

			row = cursor.fetchone()
			fields = []
			while row:
				fields.append(row[0])
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