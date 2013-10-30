using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.enums
{
    public class Types
    {

        public  const  int BIT = -7;

        public  const  int TINYINT = -6;

        public  const  int SMALLINT = 5;

        public  const  int INTEGER = 4;

        public  const  int BIGINT = -5;

        public  const  int FLOAT = 6;

        public  const  int REAL = 7;

        public  const  int DOUBLE = 8;

        public  const  int NUMERIC = 2;

        public  const  int DECIMAL = 3;

        public  const  int CHAR = 1;

        public  const  int VARCHAR = 12;

        public  const  int LONGVARCHAR = -1;

        public  const  int DATE = 91;

        public  const  int TIME = 92;

        public  const  int TIMESTAMP = 93;

        public  const  int BINARY = -2;

        public  const  int VARBINARY = -3;

        public  const  int LONGVARBINARY = -4;

        public  const  int NULL = 0;

        public  const  int OTHER = 1111;

        public  const  int JAVA_OBJECT = 2000;

        public  const  int DISTINCT = 2001;

        public  const  int STRUCT = 2002;

        public  const  int ARRAY = 2003;

        public  const  int BLOB = 2004;

        public  const  int CLOB = 2005;

        public  const  int REF = 2006;

        public  const  int DATALINK = 70;

        public  const  int BOOLEAN = 16;

        public  const  int ROWID = -8;

        public  const  int NCHAR = -15;

        public  const  int NVARCHAR = -9;

        public  const  int LONGNVARCHAR = -16;

        public  const  int NCLOB = 2011;

        public  const  int SQLXML = 2009;

        // Prevent instantiation
        private Types() { }
    }
}
