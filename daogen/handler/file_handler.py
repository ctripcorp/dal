
import tornado.web

class FileHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("../templates/file.html")