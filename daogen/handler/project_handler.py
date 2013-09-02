
import tornado.web
import json, bson
from daogen.model.project_model import project_model_obj
from daogen.model.task_model import task_model_obj

#TODO: Add version to project

class ProjectHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("../templates/project.html")

class ProjectDeleteHandler(tornado.web.RequestHandler):
	def get(self):
		project_id = self.get_argument("project_id", default=None, strip = None)
		project_model_obj.delete(project_id)
		task_model_obj.delete_by_project(project_id)

		self.write(json.dumps({"success": True}))
		self.finish()


class ProjectsHandler(tornado.web.RequestHandler):
	def get(self):
		results = []
		for p in project_model_obj.retrieve():
			if isinstance(p["_id"], bson.objectid.ObjectId):
				p["_id"] = str(p["_id"])
			results.append(p)

		self.write(json.dumps(results))
		self.finish()

	def post(self):
		product_line = self.get_argument("product_line", default=None, strip = None)
		domain = self.get_argument("domain", default=None, strip = None)
		service = self.get_argument("service", default=None, strip = None)
		alias = self.get_argument("alias", default=None, strip = None)
		version = self.get_argument("version", default=None, strip = None)

		project_model_obj.insert(product_line, domain, service,version, alias)

		self.write(json.dumps({"success": True}))
		self.finish()

if __name__ == '__main__':
	for p in project_model_obj.retrieve():
		print type(p["_id"])