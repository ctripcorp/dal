
import tornado.ioloop
import tornado.web
import os
import json
from daogen.handler.base import RequestDispatcher
from daogen.handler.task_handler import TaskHandler
from daogen.handler.project_handler import ProjectHandler
from daogen.handler.file_handler import FileHandler
from daogen.handler.database_handler import DatabaseHandler
from daogen.bootstrap import init_all

class IndexHandler(RequestDispatcher):
	def index(self):
		self.render("templates/project.html")

class LoginHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("templates/login.html")

settings = {
    "static_path": os.path.join(os.path.dirname(__file__), "static"),
    "projects_path":os.path.join(os.path.dirname(__file__), "projects"),
    "cookie_secret": "__TODO:_GENERATE_YOUR_OWN_RANDOM_VALUE_HERE__",
    "login_url": "/login",
    "xsrf_cookies": False,
}
application = tornado.web.Application([
	(r"/", IndexHandler),
	(r"/login", LoginHandler),
	(r"/project/.*", ProjectHandler),
	(r"/task/.*", TaskHandler),
	(r"/file/.*", FileHandler),
	(r"/database/.*",DatabaseHandler),
	(r'/projects/(.*)', tornado.web.StaticFileHandler, {'path': settings['projects_path']}),
	(r'/static/img/(.*)', tornado.web.StaticFileHandler, {'path': os.path.join(settings['static_path'], "img")}),
	(r"/(favicon\.ico)", tornado.web.StaticFileHandler,{'path': settings['static_path']}),
], debug=True,**settings)

if __name__ == "__main__":
	init_all()
	application.listen(8888)
	tornado.ioloop.IOLoop.instance().start()