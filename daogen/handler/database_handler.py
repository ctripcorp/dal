
from daogen.handler.base import RequestDispatcher
import StringIO
import pymssql, json

connection =  pymssql.connect(host=r'testdb.dev.sh.ctriptravel.com',
	port='28747', database='')

cursor = connection.cursor()

class DatabaseHandler(RequestDispatcher):

	def databases(self):
		cursor.execute('use master select * from sysdatabases')

		row = cursor.fetchone()
		databases = []
		while row:
			databases.append(row[0])
			row = cursor.fetchone()

		self.write(json.dumps(databases))
		self.finish()

	def tables(self):
		db_name = self.get_argument("db_name", default=None, strip=False)
		cursor.execute("use %s select Name from sysobjects where xtype in ('v','u') and status>=0 order by name" % db_name)

		row = cursor.fetchone()
		tables = []
		while row:
			tables.append(row[0])
			row = cursor.fetchone()

		self.write(json.dumps(tables))
		self.finish()

	def fields(self):
		db_name = self.get_argument("db_name", default=None, strip=False)
		table_name = self.get_argument("table_name", default=None, strip=False)
		cursor.execute("use %s SELECT COLUMN_NAME,DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = '%s'" % (db_name, table_name))

		row = cursor.fetchone()
		fields = []
		while row:
			fields.append({"name":row[0], "type": row[1]})
			row = cursor.fetchone()

		self.write(json.dumps(fields))
		self.finish()

	def sps(self):
		db_name = self.get_argument("db_name", default=None, strip=False)
		cursor.execute("use %s select SPECIFIC_SCHEMA,SPECIFIC_NAME from information_schema.routines where routine_type = 'PROCEDURE'" % db_name)

		row = cursor.fetchone()
		fields = []
		while row:
			fields.append(row[0]+"."+row[1])
			row = cursor.fetchone()

		self.write(json.dumps(fields))
		self.finish()

	def sp_code(self):
		db_name = self.get_argument("db_name", default=None, strip=False)
		sp_name = self.get_argument("sp_name", default=None, strip=False)
		cursor.execute("use %s" % db_name)
		cursor.callproc('sp_HelpText', (sp_name,))

		output = StringIO.StringIO()

		for row in cursor:
			output.write(row[0])

		self.write(json.dumps(output.getvalue()))
		self.finish()

	def save_sp(self):
		db_name = self.get_argument("db_name", default=None, strip=False)
		sp_code = self.get_argument("sp_code", default=None, strip=False)

		sp_code = json.loads(sp_code)

		cursor.execute("use %s" % db_name)
		cursor.execute(sp_code)

		# cursor.commit()
		connection.commit()

		self.write(json.dumps({"success": True}))
		self.finish()