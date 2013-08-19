
import tornado.ioloop
import tornado.web
import os
import json

class MainHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("templates/index.html")

class LoginHandler(tornado.web.RequestHandler):
	def get(self):
		self.render("templates/login.html")

class BodyHandler(tornado.web.RequestHandler):
	def get(self, body_name):
		self.render("templates/%s.html" % body_name)

class SqlQueueHandler(tornado.web.RequestHandler):
	def get(self):
		self.write(json.dumps([]))
		self.finish()

settings = {
    "static_path": os.path.join(os.path.dirname(__file__), "static"),
    "cookie_secret": "__TODO:_GENERATE_YOUR_OWN_RANDOM_VALUE_HERE__",
    "login_url": "/login",
    "xsrf_cookies": True,
}
application = tornado.web.Application([
	(r"/", MainHandler),
	(r"/body/(.*)", BodyHandler),
	(r"/login", LoginHandler),
	(r"/sql_queue", SqlQueueHandler),
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