
from daogen.model.base import BaseModel
import bson

class FieldTypeModel(BaseModel):
	def insert(self, db_name, table_name,primary_key, fields):
		kwargs = {}
		kwargs["db_name"] = db_name
		kwargs["table_name"] = table_name
		kwargs["primary_key"] = primary_key
		kwargs["fields"] = fields
		self.db.fieldtype.insert(kwargs)

	def update(self, db_name, table_name,primary_key, fields):
		kwargs = {}
		kwargs["db_name"] = db_name
		kwargs["table_name"] = table_name
		kwargs["primary_key"] = primary_key
		kwargs["fields"] = fields
		self.db.fieldtype.update(
			{"db_name": db_name, "table_name": table_name},
			kwargs,
			upsert=True
			)
	
	def get_by_table(self, db_name, table_name):
		
		result = self.db.fieldtype.find_one({"db_name": db_name, "table_name": table_name})

		return result

class SPParamTypeModel(BaseModel):
	def insert(self, db_name, sp_name, params):
		kwargs = {}
		kwargs["db_name"] = db_name
		kwargs["sp_name"] = sp_name
		kwargs["params"] = params
		self.db.spparams.insert(kwargs)

	def update(self, db_name, sp_name,params):
		kwargs = {}
		kwargs["db_name"] = db_name
		kwargs["sp_name"] = sp_name
		kwargs["params"] = params
		self.db.spparams.update(
			{"db_name": db_name, "sp_name": sp_name},
			kwargs,
			upsert=True
			)
	
	def get_by_sp(self, db_name, sp_name):
		
		result = self.db.spparams.find_one({"db_name": db_name, "sp_name": sp_name})

		return result


field_type_model_obj = FieldTypeModel()
sp_param_type_model_obj = SPParamTypeModel()