
import tornado.web
import subprocess
from subprocess import PIPE
import glob, os, shutil, json
from daogen.generator.java_gen import generator

projects_dir = os.path.join(
				os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
				"projects")

class FileHandler(tornado.web.RequestHandler):
	def get(self):
		files = glob.iglob(os.path.join(projects_dir, "*.jar"))

		names = [os.path.basename(f) for f in files]

		print names

		self.render("../templates/file.html", files=names)

class GenerateHandler(tornado.web.RequestHandler):
	
	def post(self):
		project_id = self.get_argument("project_id", default=None, strip=False)
		generator.generate(project_id)

		working_dir = os.path.join(projects_dir,project_id)

		p = subprocess.Popen("mvn package",
			cwd=working_dir,
			shell=True, 
			stdout=PIPE, 
			stderr=PIPE)
		p.communicate()

		files = glob.iglob(os.path.join(
			os.path.join(working_dir,"target"),
			 "*.jar"))

		for f in files:
			if os.path.isfile(f):
				shutil.copy2(f, projects_dir)

		self.write(json.dumps({"success": True}))
		self.finish()