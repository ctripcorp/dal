# -*- coding:utf-8 -*-

#Configuration of the project

import ConfigParser, os


class Conf(object):
    def __init__(self):
        self.Config = ConfigParser.ConfigParser()

        self.Config.read(os.path.join(os.path.abspath(os.path.dirname(__file__)), "daogen.conf"))

        self._conf = {}

        for section in self.Config.sections():
            self._conf.update(self.ConfigSectionMap(section))

    def  __getitem__(self, name):
        return self._conf[name]

    def __setitem__(self, name, value):
        if isinstance(value, (bool)):
            value = 1 if value else 0
        self.Config.set('MainSection', name, value)
        self._conf.update({name: value})
        with open(os.path.join(os.path.abspath(os.path.dirname(__file__)), "daogen.conf"), 'w') \
         as f:
         self.Config.write(f)

    def ConfigSectionMap(self, section):
        dict1 = {}
        options = self.Config.options(section)
        for option in options:
            try:
                dict1[option] = self.Config.get(section, option)
                if dict1[option] == -1:
                    DebugPrint("skip: %s" % option)
            except:
                print("exception on %s!" % option)
                dict1[option] = None
        return dict1

conf = Conf()