
import socket, struct, msgpack, uuid
# ,binascii
# from io import BytesIO as StringIO

class MyMessage(object):
	tableName = "what"
	fields = ["world"]

def encode_my_message(obj):
	if isinstance(obj, MyMessage):
		return (obj.tableName, obj.fields)
	return obj

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(("localhost", 9000))

serialized = msgpack.packb(MyMessage(), default=encode_my_message)
# serialized = msgpack.packb(MyMessage())
# serialized = msgpack.packb(["what", ["world"]])
# serialized = msgpack.packb("what")

# data = StringIO()
# data.write(serialized)
# serialized = msgpack.packb(["world"])
# data.write(serialized)
# serialized = data.getvalue()
s_len = len(serialized)

# for a in serialized:
# 	print binascii.hexlify(a)

total_len = 4+36+2+2+3+4+1+s_len

send_data = struct.pack("!i36shh3shi%ds" % s_len, 
	total_len, str(uuid.uuid4()), 32, 3, "xxx",1, s_len, serialized)

s.sendall(send_data)

s.close()
