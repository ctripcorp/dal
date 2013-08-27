
import tornado.web
import json, bson
from daogen.model.project_model import project_model_obj

class ProjectHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("../templates/project.html")

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

		project_model_obj.insert(product_line, domain, service, alias)

		self.write(json.dumps({"success": True}))
		self.finish()

if __name__ == '__main__':
	for p in project_model_obj.retrieve():
		print type(p["_id"])