
import tornado.web

class ProjectHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("../templates/project.html")