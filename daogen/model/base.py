
from daogen.config import conf
from pymongo.mongo_client import MongoClient

class BaseModel(object):
    
    def __init__(self):
        self.conn = MongoClient(host=conf["mongo_host"], port=int(conf["mongo_port"]))
        self.db = self.conn[conf["mongo_database"]]

    def __enter__(self):
        return self

    def __exit__(self, type, value, traceback):
    	self.conn.disconnect()

    def start_request(self):
    	self.conn.start_request()

    def end_request(self):
    	self.conn.end_request()