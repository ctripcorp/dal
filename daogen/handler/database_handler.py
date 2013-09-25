
from daogen.handler.base import RequestDispatcher
from daogen.model.field_type_model import field_type_model_obj, sp_param_type_model_obj
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

		cursor.execute("use %s" % db_name)

		cursor.callproc('sp_helpindex', (table_name,))

		indexed_columns = []
		for row in cursor:
			indexed_columns.extend(row[2].split(","))

		indexed_columns = [i.strip() for i in indexed_columns]

		primary_key = None

		cursor.execute("""use %s SELECT Col.Column_Name from INFORMATION_SCHEMA.TABLE_CONSTRAINTS Tab, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE Col 
						  	WHERE 
							Col.Constraint_Name = Tab.Constraint_Name
							AND Col.Table_Name = Tab.Table_Name
							AND Constraint_Type = 'PRIMARY KEY '
							AND Col.Table_Name = '%s'""" % (db_name, table_name))		

		row = cursor.fetchone()

		if row:
			primary_key = row[0]

		cursor.execute("use %s SELECT COLUMN_NAME,DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = '%s'" % (db_name, table_name))

		row = cursor.fetchone()
		fields = []
		field_type = {}
		while row:
			name = row[0]
			fields.append({"name": name, 
				"indexed": name in indexed_columns })
			field_type[name] = row[1]
			row = cursor.fetchone()

		if field_type_model_obj.get_by_table(db_name, table_name):
			field_type_model_obj.update(db_name, table_name,primary_key, field_type)
		else:
			field_type_model_obj.insert(db_name, table_name,primary_key, field_type)

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

		params = []
		
		cursor.execute("""use %s select PARAMETER_NAME, DATA_TYPE from information_schema.parameters
					where specific_name='%s'""" % (db_name, sp_name[sp_name.index(".")+1:]))
		for row in cursor:
			params.append({row[0]: row[1]})

		if sp_param_type_model_obj.get_by_sp(db_name, sp_name):
			sp_param_type_model_obj.update(db_name, sp_name, params)
		else:
			sp_param_type_model_obj.insert(db_name, sp_name, params)

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