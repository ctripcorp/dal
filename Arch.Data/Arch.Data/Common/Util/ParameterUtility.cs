using Arch.Data.Common.constant;
using Arch.Data.DbEngine;
using System;
using System.Collections;
using System.Data;
using System.Text;

namespace Arch.Data.Utility
{
    public class ParameterUtility
    {
        /// <summary>
        /// 将  List{1,2,3},转换成 @p0,@p1,@p2 目前只支持int long string, 且string默认是AnsiString，请参考 ansiString参数
        /// </summary>
        /// <param name="ie">集合</param>
        /// <param name="prex">前缀，默认p</param>
        /// <param name="ps">参数集合</param>
        /// <param name="ansiString">是否是AnsiString(对应于Varchar)，默认是true，如果是NVarchar，传入False</param>
        /// <returns></returns>
        public static String NormalizeInParam(IEnumerable ie, StatementParameterCollection ps, String prex = "p", Boolean ansiString = true)
        {
            if (ie == null) throw new DalException("The list is null, you should pass in something for DAL to process!");

            var enumerator = ie.GetEnumerator();
            StringBuilder sb = new StringBuilder();
            Int32 index = 0;

            while (enumerator.MoveNext())
            {
                String pName = String.Format("@{0}{1}", prex, index++);
                sb.AppendFormat(pName + ",");

                DbType dbType;
                Type type = enumerator.Current.GetType();

                if (type == typeof(Int32))
                {
                    dbType = DbType.Int32;
                }
                else if (type == typeof(Int64))
                {
                    dbType = DbType.Int64;
                }
                else if (type == typeof(String))
                {
                    dbType = ansiString ? DbType.AnsiString : DbType.String;
                }
                else
                {
                    throw new NotSupportedException(String.Format("Type {0} not supported, only support int , long ,string!", type));
                }

                ps.AddInParameter(pName, dbType, enumerator.Current);
            }

            if (sb.Length < 1)
                throw new DalException("The length of the list is zero, you should pass in something for DAL to process!");
            return sb.ToString().TrimEnd(',');
        }

        public static Boolean IgnoreNullValue(IDictionary extendedParameters)
        {
            return extendedParameters == null || !extendedParameters.Contains(DALExtStatementConstant.SETNULL);
        }

    }
}
