
import tornado.web
import pymssql

connection =  pymssql.connect(host=r'testdb.dev.sh.ctriptravel.com',
	port='28747', database='')

cursor = connection.cursor()

class TaskHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("../templates/task.html", databases=self.get_databases())

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
