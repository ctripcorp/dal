

from daogen.model.base import BaseModel
import bson

class TaskModel(BaseModel):
	def insert(self, project_id, task_type, database, **kwargs):
		kwargs["project_id"] = project_id
		kwargs["database"] = database
		kwargs["task_type"] = task_type
		if task_type == "autosql":
			self.db.task_auto.insert(kwargs)
		elif task_type == "sp":
			self.db.task_sp.insert(kwargs)
		elif task_type == "freesql":
			self.db.task_free.insert(kwargs)
	
	def get_by_project(self, project_id):
		auto = self.db.task_auto.find({"project_id": project_id})
		sp = self.db.task_sp.find({"project_id": project_id})
		free = self.db.task_free.find({"project_id": project_id})
		
		result = []

		for a in auto:
			result.append(a)

		for s in sp:
			result.append(s)

		for f in free:
			result.append(f)

		return result

	def get_auto_sql(self, project_id):
		return self.db.task_auto.find({"project_id": project_id})

	def get_sp(self, project_id):
		return self.db.task_sp.find({"project_id": project_id})

	def get_free_sql(self, project_id):
		return self.db.task_free.find({"project_id": project_id})

	def delete_by_project(self, project_id):
		self.db.task_auto.remove({"project_id": project_id})
		self.db.task_free.remove({"project_id": project_id})
		self.db.task_sp.remove({"project_id": project_id})

	def delete_by_id(self, task_id):
		self.db.task_auto.remove({"_id": bson.objectid.ObjectId(task_id)})
		self.db.task_free.remove({"_id": bson.objectid.ObjectId(task_id)})
		self.db.task_sp.remove({"_id": bson.objectid.ObjectId(task_id)})

task_model_obj = TaskModel()