

import json, bson

from daogen.model.project_model import project_model_obj
from daogen.model.task_model import task_model_obj
from daogen.handler.base import RequestDispatcher

class TaskHandler(RequestDispatcher):

	def index(self):
		project_id = self.get_argument("project_id", default="5223f7ae28afd214e875aca5", strip=False)
		tasks = []
		project_alias = project_model_obj.retrieve_alias(project_id)["alias"]
		tasks = task_model_obj.get_by_project(project_id)
		self.render("../templates/task.html", 
			project_id=project_id,
			project_name=project_alias,
			tasks=tasks)

	def add(self):
		project_id = self.get_argument("project_id", default=None, strip=False)
		task_object = self.get_argument("task_object", default=None, strip=False) 
		task_type = self.get_argument("task_type", default=None, strip=False) 

		if task_object is not None:
			task_object = json.loads(task_object)

		task_model_obj.insert(project_id, task_type, **task_object)

		self.write(json.dumps({"success": True}))
		self.finish()

	def delete(self):
		task_id = self.get_argument("task_id", default=None, strip=False)
		task_model_obj.delete_by_id(task_id)

		self.write(json.dumps({"success": True}))
		self.finish()

	def tasks(self):
		project_id = self.get_argument("project_id", default="5223f7ae28afd214e875aca5", strip=False)
		results = []
		for p in task_model_obj.get_by_project(project_id):
			if isinstance(p["_id"], bson.objectid.ObjectId):
				p["_id"] = str(p["_id"])
			results.append(p)

		self.write(json.dumps(results))
		self.finish()