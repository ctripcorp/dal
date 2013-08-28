

from daogen.model.base import BaseModel
import bson

class ProjectModel(BaseModel):
	def insert(self, product_line, domain, service, alias=None):
		self.db.project.insert({
				"product_line": product_line,
				"domain": domain,
				"service": service,
				"alias": alias
			})

	def retrieve(self):
		return self.db.project.find()

	def retrieve_alias(self, project_id):
		return self.db.project.find_one(
			{"_id": bson.objectid.ObjectId(project_id)})

project_model_obj = ProjectModel()