
import subprocess
from subprocess import PIPE
import glob, os, shutil, json
#from daogen.generator.java_gen import generator
from daogen.generator.csharp_gen import generator
from daogen.handler.base import RequestDispatcher
from daogen.model.project_model import project_model_obj

projects_dir = os.path.join(
				os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
				"projects")

class FileHandler(RequestDispatcher):

	def index(self):
		# files = glob.iglob(os.path.join(projects_dir, "*.jar"))

		# #pom = glob.iglob(os.path.join(projects_dir, "*.xml"))

		# names = [os.path.basename(f) for f in files]

		# #names.extend([os.path.basename(p) for p in pom])

		self.render("../templates/file.html")

	def projects(self):
		request_id = self.get_argument("id", default=None, strip=False)
		request_type = self.get_argument("type", default=None, strip=False)
		request_name = self.get_argument("name", default=None, strip=False)
		results = []
		if request_type == "all":
			projects = []
			for p in project_model_obj.retrieve():
				projects.append({
					"id": str(p["_id"]),
					"name": p["alias"],
					"isParent": True,
					"type": "project"
					})
			results = projects
		elif request_type == "project":
			files = glob.iglob(os.path.join(
			os.path.join(projects_dir, request_id),"*.*"))
			for f in files:
				results.append({
					"id": request_id,
					"name": os.path.basename(f),
					"isParent": False,
					"type": "file"
					})
		self.write(json.dumps(results))
		self.finish()

	def content(self):
		request_id = self.get_argument("id", default=None, strip=False)
		request_name = self.get_argument("name", default=None, strip=False)

		file_path = os.path.join(os.path.join(projects_dir, request_id), request_name)
		result = ""
		if os.path.exists(file_path):
			with open(file_path, "r") as f:
				result = f.read()
		
		self.write(json.dumps(result))
		self.finish()

	def generate(self):
		project_id = self.get_argument("project_id", default=None, strip=False)
		generator.generate(project_id)

		# working_dir = os.path.join(projects_dir,project_id)

		# p = subprocess.Popen("mvn deploy",
		# 	cwd=working_dir,
		# 	shell=True, 
		# 	stdout=PIPE, 
		# 	stderr=PIPE)
		# output, error = p.communicate()
		# result = {
		# 	"output": output,
		# 	"error": error
		# }

		result = {"success": True}

		# print result

		# files = glob.iglob(os.path.join(
		# 	os.path.join(working_dir,"target"),
		# 	 "*.jar"))

		# for f in files:
		# 	if os.path.isfile(f):
		# 		shutil.copy2(f, projects_dir)

		self.write(json.dumps(result, encoding="GB2312"))
		self.finish()