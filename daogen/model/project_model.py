

from daogen.model.base import BaseModel

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

project_model_obj = ProjectModel()