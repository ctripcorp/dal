using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;

namespace platform.dao
{
    public class Consts
    {

        public static  string ServerIp = "localhost";

        public static  int ServerPort = 9000;

        public static readonly IList<DbType> validDbType;

        public static int RetryTimesWhenError = 3;

        static Consts()
        {
            validDbType = new List<DbType>();
            validDbType.Add(DbType.Boolean);
            validDbType.Add(DbType.Byte);
            validDbType.Add(DbType.SByte);
            validDbType.Add(DbType.Int16);
            validDbType.Add(DbType.Int32);
            validDbType.Add(DbType.Int64);
            validDbType.Add(DbType.UInt32);
            validDbType.Add(DbType.UInt64);
            validDbType.Add(DbType.UInt16);
            validDbType.Add(DbType.Single);
            validDbType.Add(DbType.Double);
            validDbType.Add(DbType.Decimal);
            validDbType.Add(DbType.StringFixedLength);
            validDbType.Add(DbType.String);
            validDbType.Add(DbType.Guid);
            validDbType.Add(DbType.DateTime);
            validDbType.Add(DbType.Binary);
        }

    }
}
