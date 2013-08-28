
import tornado.ioloop
import tornado.web
import os
import json
from daogen.handler.task_handler import TaskHandler,TasksHandler, TableHandler
from daogen.handler.project_handler import ProjectHandler, ProjectsHandler
from daogen.handler.file_handler import FileHandler, GenerateHandler

class LoginHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("templates/login.html")

class SqlQueueHandler(tornado.web.RequestHandler):
	def get(self):
		self.write(json.dumps([]))
		self.finish()

settings = {
    "static_path": os.path.join(os.path.dirname(__file__), "static"),
    "projects_path":os.path.join(os.path.dirname(__file__), "projects"),
    "cookie_secret": "__TODO:_GENERATE_YOUR_OWN_RANDOM_VALUE_HERE__",
    "login_url": "/login",
    "xsrf_cookies": False,
}
application = tornado.web.Application([
	(r"/", ProjectHandler),
	(r"/project/projects", ProjectsHandler),
	(r"/task/tasks", TasksHandler),
	(r"/task", TaskHandler),
	(r"/metadata",TableHandler),
	(r"/file", FileHandler),
	(r"/generate", GenerateHandler),
	(r"/login", LoginHandler),
	(r"/sql_queue", SqlQueueHandler),
	(r'/projects/(.*)', tornado.web.StaticFileHandler, {'path': settings['projects_path']}),
	(r'/static/(.*)', tornado.web.StaticFileHandler, {'path': settings['static_path']}),
	(r'/static/img/(.*)', tornado.web.StaticFileHandler, {'path': os.path.join(settings['static_path'], "img")}),
	#(r'/static/font/(.*)', tornado.web.StaticFileHandler, {'path': os.path.join(settings['static_path'], "font")}),
	(r'/img/(.*)', tornado.web.StaticFileHandler, {'path': os.path.join(settings['static_path'], "img")}),
	(r'/static/css/images/(.*)', tornado.web.StaticFileHandler, {'path': os.path.join(settings['static_path'], "img")}),
	(r"/(favicon\.ico)", tornado.web.StaticFileHandler,{'path': settings['static_path']}),
], debug=True,**settings)

if __name__ == "__main__":
	application.listen(8888)
	tornado.ioloop.IOLoop.instance().start()