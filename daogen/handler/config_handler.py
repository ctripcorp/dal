

import json, bson

from daogen.model.project_model import project_model_obj
from daogen.model.task_model import task_model_obj
from daogen.handler.base import RequestDispatcher

class ConfigHandler(RequestDispatcher):

	def index(self):
		self.render("../templates/dbconf.html")

	def machine(self):
		self.render("../templates/machineconf.html")

	def port(self):
		self.render("../templates/portconf.html")

	def worker(self):
		self.render("../templates/workerconf.html")

	def controller(self):
		self.render("../templates/controllerconf.html")