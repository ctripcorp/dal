

import pymssql, _mssql

#conn1 = _mssql.connect('192.168.83.76')

conn = pymssql.connect(host=r'testdb.dev.sh.ctriptravel.com',
	port='28747', database='')

cur = conn.cursor()

cur.execute('use master select * from sysdatabases')

row = cur.fetchone()
while row:
    print "Name=%s" % row[0]
    row = cur.fetchone()

# cur.execute('delete from dbo.person')
# conn.commit()
conn.close()