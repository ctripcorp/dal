using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Data;
using System.Data.SqlClient;
//using platform.dao.log;
using System.ComponentModel;
using System.Text.RegularExpressions;
using platform.dao.enums;

namespace platform.bll
{
    class Program
    {
        static void Main(string[] args)
        {

            Console.WriteLine(GetFromByteArray(Types.BOOLEAN, new byte[]{1}));

            Console.WriteLine(GetFromByteArray(Types.TINYINT, new byte[] { 1 }));

            Console.WriteLine(GetFromByteArray(Types.SMALLINT, new byte[] { 0x01, 0x02 }));
            
            Console.Read();

        }

        private static object GetFromByteArray(int currentType, byte[] currentValue)
        {
            DateTime utcStartTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
            object result = null;
            switch (currentType)
            {
                case Types.BOOLEAN:
                    result = BitConverter.ToBoolean(currentValue, 0);
                    break;
                case Types.SMALLINT:
                    result = BitConverter.ToInt16(currentValue, 0);
                    break;
                case Types.TINYINT:
                    result = currentValue[0];
                    break;
                case Types.INTEGER:
                    result = BitConverter.ToInt32(currentValue, 0);
                    break;
                case Types.BIGINT:
                    result = BitConverter.ToInt64(currentValue, 0);
                    break;
                case Types.FLOAT:
                case Types.DOUBLE:
                    result = BitConverter.ToDouble(currentValue, 0);
                    break;
                case Types.BINARY:
                    result = currentValue;
                    break;
                case Types.TIMESTAMP:
                    result = utcStartTime.AddMilliseconds(BitConverter.ToUInt64(currentValue, 0));
                    break;
                default:
                    result = BitConverter.ToString(currentValue);
                    break;
            }

            return result;

        }

     
    }
}
