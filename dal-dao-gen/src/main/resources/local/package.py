#! /usr/bin/python
# -*- coding:utf-8 -*-
from __future__ import absolute_import

import sys
import os
import subprocess
import shutil

from optparse import OptionParser

def __help(args):
    print """\
Usage: package <command> [options]

Commands:
  build      build dao generator

Options:
  --help          show this help message and exit

Type 'package <command> --help' for help using a specific command.
"""

def build(args):
	usage = "Usage: package build [options]"
	parser = OptionParser(usage=usage)

	parser.add_option("-d", "--directory", type="str", help="The working directory")

	option, args = parser.parse_args(args)

	working_dir = option.directory

	src_conf_properties = os.path.join(working_dir, "src/main/resources/local/conf.properties")
	src_jdbc_properties = os.path.join(working_dir, "src/main/resources/local/jdbc.properties")
	src_log4j = os.path.join(working_dir, "src/main/resources/local/log4j.xml")
	src_web_xml = os.path.join(working_dir, "src/main/webapp/WEB-INF/web.xml")
	try:
		shutil.copy2(src_conf_properties, os.path.join(working_dir, "conf.properties"))
		shutil.copy2(src_jdbc_properties, os.path.join(working_dir, "jdbc.properties"))
		shutil.copy2(src_log4j, os.path.join(working_dir, "log4j.xml"))
		shutil.copy2(src_web_xml, os.path.join(working_dir, "web.xml"))

		shutil.copy2(os.path.join(os.path.dirname(src_conf_properties), "conf.properties.pub"), src_conf_properties)
		shutil.copy2(os.path.join(os.path.dirname(src_jdbc_properties), "jdbc.properties.pub"), src_jdbc_properties)
		shutil.copy2(os.path.join(os.path.dirname(src_log4j), "log4j.xml"), src_log4j)
		shutil.copy2(os.path.join(os.path.dirname(src_web_xml), "web.xml.pub"), src_web_xml)
		os.chdir(working_dir)
		p = subprocess.Popen("mvn install",shell=True)
		p.communicate()

		shutil.copy2(os.path.join(working_dir, "conf.properties"),src_conf_properties)
		shutil.copy2(os.path.join(working_dir, "jdbc.properties"),src_jdbc_properties)
		shutil.copy2(os.path.join(working_dir, "log4j.xml"),src_log4j)
		shutil.copy2(os.path.join(working_dir, "web.xml"),src_web_xml)
	except Exception,e:
		print e
	

def main():

	valid_commands = ["--help", "build"]
	if len(sys.argv) < 2 or sys.argv[1] not in valid_commands:
	    print "Invaid input!"
	    __help([])
	else:
	    eval(sys.argv[1].replace("-", "_") + "(sys.argv[2:])", )

if __name__ == "__main__":
	main()
