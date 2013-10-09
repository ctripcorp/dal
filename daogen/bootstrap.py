

from daogen.model.project_model import project_model_obj

def init_all():
	default = project_model_obj.retrieve_default()
	if not default:
		project_model_obj.insert(
			"platform", "apptools", "demo", "1.0.0.0", "Anoynomous", True)
