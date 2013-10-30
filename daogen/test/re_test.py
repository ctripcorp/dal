
import re

def normalize_holder(match_obj):
	return "?"

print re.sub(r"[@|:]\w+", normalize_holder, "SELECT @hello :what")