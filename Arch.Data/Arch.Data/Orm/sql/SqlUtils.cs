using Arch.Data.DbEngine;
using System;
using System.Collections;
using System.Data;

namespace Arch.Data.Orm.sql
{
    static class SqlUtils
    {
        private static readonly Hashtable Typemap;

        static SqlUtils()
        {
            Typemap = new Hashtable(17, 1f)
            {
                { typeof (String), DbType.String },
                { typeof (Int32), DbType.Int32 },
                { typeof (Boolean), DbType.Boolean },
                { typeof (DateTime), DbType.DateTime },
                { typeof (Double), DbType.Double },
                { typeof (Int64), DbType.Int64 },
                { typeof (Int16), DbType.Int16 },
                { typeof (Byte), DbType.Byte },
                { typeof (Char), DbType.StringFixedLength },
                { typeof (Decimal), DbType.Decimal },
                { typeof (Single), DbType.Single },
                { typeof (UInt32), DbType.UInt32 },
                { typeof (UInt64), DbType.UInt64 },
                { typeof (UInt16), DbType.UInt16 },
                { typeof (SByte), DbType.SByte },
                { typeof (Guid), DbType.Guid },
                { typeof (Byte[]), DbType.Binary },
                { typeof (TimeSpan), DbType.Time },
                { typeof (DateTimeOffset), DbType.DateTimeOffset }
            };
        }

        public static void PrepareParameter(StatementParameter parameter, Object value, DbType? dataType)
        {
            if (!dataType.HasValue)
            {
                PrepareParameter(parameter, value);
            }
            else
            {
                parameter.DbType = dataType.Value;

                if (value == null || value == DBNull.Value)
                {
                    parameter.Value = DBNull.Value;
                }
                else
                {
                    parameter.Value = value;
                }
            }
        }

        public static void PrepareParameter(StatementParameter parameter, Object value)
        {
            if (value == null || value == DBNull.Value)
            {
                PrepareParameterType(parameter, null);
                parameter.Value = DBNull.Value;
            }
            else
            {
                var dbType = value.GetType();
                PrepareParameterType(parameter, dbType);
                parameter.Value = value;
            }
        }

        public static void PrepareParameterType(StatementParameter parameter, Type type)
        {
            parameter.DbType = ResolveType(type);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static DbType ResolveType(Type type)
        {
            if (type != null)
            {
                var t = type;

                if (t.IsGenericType && t.IsValueType)
                {
                    var genericTypes = t.GetGenericArguments();
                    if (genericTypes.Length > 0) t = genericTypes[0];
                }

                if (t != null && Typemap.ContainsKey(t)) return (DbType)Typemap[t];
            }

            return DbType.Object;
        }

        public static DbType GetDbType(IColumn column)
        {
            return column.ColumnType ?? ResolveType(column.DataType);
        }

    }
}
