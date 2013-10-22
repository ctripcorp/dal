

import json, bson

from daogen.model.project_model import project_model_obj
from daogen.model.task_model import task_model_obj
from daogen.handler.base import RequestDispatcher

class PerformanceHandler(RequestDispatcher):

	def index(self):
		self.render("../templates/performance.html")