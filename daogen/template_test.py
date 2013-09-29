
import daogen.template as template

t = template.Template("<html>{{ newValue = myvalue+'xx' }} {{ newValue }}</html>")
print t.generate(myvalue="XXX")