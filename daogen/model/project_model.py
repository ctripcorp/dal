

from daogen.model.base import BaseModel
import bson

class ProjectModel(BaseModel):
	def insert(self, product_line, domain, service,version, alias=None, default=False):
		self.db.project.insert({
				"product_line": product_line,
				"domain": domain,
				"service": service,
				"version": version,
				"alias": alias,
				"default": default
			})

	def retrieve(self):
		return self.db.project.find()

	def retrieve_default(self):
		return self.db.project.find_one({"default": True})

	def retrieve_alias(self, project_id):
		return self.db.project.find_one(
			{"_id": bson.objectid.ObjectId(project_id)})

	def delete(self, project_id):
		self.db.project.remove({"_id": bson.objectid.ObjectId(project_id)})

project_model_obj = ProjectModel()