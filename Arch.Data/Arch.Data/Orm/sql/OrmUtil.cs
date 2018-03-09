using Arch.Data.Common.Util;
using System;
using System.Collections.Generic;
using System.Data;

namespace Arch.Data.Orm.sql
{
    class OrmUtil
    {
        /// <summary>
        /// Get Field Index By Field Name
        /// </summary>
        /// <param name="dict">Key:Field Name, Value:Field Index</param>
        /// <param name="columnName"></param>
        /// <param name="reader"></param>
        /// <returns></returns>
        private static Int32 getFieldIndex(IDictionary<String, Int32> dict, String columnName, IDataReader reader)
        {
            Int32 index = -1;

            if (!dict.TryGetValue(columnName, out index))
            {
                index = reader.GetOrdinal(columnName);
                dict.Add(columnName, index);
            }

            return index;
        }

        /// <summary>
        /// Overloading
        /// </summary>
        /// <param name="dict"></param>
        /// <param name="columnName"></param>
        /// <param name="dataTable"></param>
        /// <returns></returns>
        private static Int32 getFieldIndex(IDictionary<String, Int32> dict, String columnName, DataTable dataTable)
        {
            Int32 index = -1;

            if (!dict.TryGetValue(columnName, out index))
            {
                index = dataTable.Columns.IndexOf(columnName);
                dict.Add(columnName, index);
            }

            return index;
        }

        /// <summary>
        /// Get Field Type By Reader Index
        /// </summary>
        /// <param name="dict">Key:Index, Value:FieldType</param>
        /// <param name="columnIndex"></param>
        /// <param name="reader"></param>
        /// <returns></returns>
        private static Type getFieldType(IDictionary<Int32, Type> dict, Int32 columnIndex, IDataReader reader)
        {
            Type t = null;

            if (!dict.TryGetValue(columnIndex, out t))
            {
                t = reader.GetFieldType(columnIndex);
                dict.Add(columnIndex, t);
            }

            return t;
        }

        /// <summary>
        /// Overloading
        /// </summary>
        /// <param name="dict"></param>
        /// <param name="columnIndex"></param>
        /// <param name="dataTable"></param>
        /// <returns></returns>
        private static Type getFieldType(IDictionary<Int32, Type> dict, Int32 columnIndex, DataTable dataTable)
        {
            Type t = null;

            if (!dict.TryGetValue(columnIndex, out t))
            {
                t = dataTable.Columns[columnIndex].DataType;
                dict.Add(columnIndex, t);
            }

            return t;
        }

        /// <summary>
        /// Get Field Value By Column Index & Column Type
        /// </summary>
        /// <param name="columnIndex"></param>
        /// <param name="columnType"></param>
        /// <param name="reader"></param>
        /// <returns></returns>
        private static Object getFieldValue(Int32 columnIndex, Type fieldType, Type entityType, IDataReader reader)
        {
            Object value = null;

            if (columnIndex < 0)
            {
                return value;
            }

            if (fieldType == null)
            {
                return value;
            }

            if (reader == null)
            {
                return value;
            }

            if (reader.IsDBNull(columnIndex))
            {
                return value;
            }

            if (fieldType.Equals(typeof(String)))
            {
                String fieldValue = reader.GetString(columnIndex);

                if (entityType.Equals(typeof(Char)) || entityType.Equals(typeof(Nullable<Char>)))
                {
                    value = String.IsNullOrEmpty(fieldValue) ? default(Char) : fieldValue[0];
                }
                else
                {
                    value = fieldValue;
                }
            }
            else if (fieldType.Equals(typeof(Boolean)))
            {
                value = reader.GetBoolean(columnIndex);
            }
            else if (fieldType.Equals(typeof(Int32)))
            {
                if (entityType.Equals(typeof(Int64)) || entityType.Equals(typeof(Nullable<Int64>)))
                {
                    value = Convert.ToInt64(reader[columnIndex]);   //For OrderId compatibility.
                }
                else if (entityType.Equals(typeof(Int16)) || entityType.Equals(typeof(Nullable<Int16>)))
                {
                    value = Convert.ToInt16(reader[columnIndex]);     //For Year field type condition.
                }
                else
                {
                    value = reader.GetInt32(columnIndex);
                }
            }
            else if (fieldType.Equals(typeof(Int64)))
            {
                if (entityType.Equals(typeof(Int32)) || entityType.Equals(typeof(Nullable<Int32>)))     //For MySql(Select 0) & For OrderId compatibility.
                {
                    value = Convert.ToInt32(reader[columnIndex]);
                }
                else
                {
                    value = reader.GetInt64(columnIndex);
                }
            }
            else if (fieldType.Equals(typeof(DateTime)))
            {
                value = reader.GetDateTime(columnIndex);
            }
            else if (fieldType.Equals(typeof(Decimal)))
            {
                value = reader.GetDecimal(columnIndex);
            }
            else if (fieldType.Equals(typeof(Byte)))
            {
                value = reader.GetByte(columnIndex);
            }
            else if (fieldType.Equals(typeof(Byte[])))
            {
                value = reader[columnIndex];
            }
            else if (fieldType.Equals(typeof(Int16)))
            {
                value = reader.GetInt16(columnIndex);
            }
            else if (fieldType.Equals(typeof(Char)))
            {
                value = reader.GetChar(columnIndex);
            }
            else if (fieldType.Equals(typeof(Single)))
            {
                value = reader.GetFloat(columnIndex);
            }
            else if (fieldType.Equals(typeof(Double)))
            {
                value = reader.GetDouble(columnIndex);
            }
            else if (fieldType.Equals(typeof(SByte)))
            {
                if (entityType.Equals(typeof(Byte)) || entityType.Equals(typeof(Nullable<Byte>)))
                {
                    value = Convert.ToByte(reader[columnIndex]);     //May cause precision lost.
                }
                //绕过MySql.Data.dll的Bug，
                /*MySql.Data.dll在读取字段的类型时，会沿用第一条的数据类型，
                 * 因此，如果SQL查询结果中，第一行数据中该字段是NULL，
                 * 那么之后所有非空的tinyint(1)字段映射会失败，
                 * 如果第一行数据中该字段是正常数据，那么之后所有映射都是成功的，*/
                else if (entityType.Equals(typeof(Boolean)) || entityType.Equals(typeof(Nullable<Boolean>)))
                {
                    value = Convert.ToBoolean(reader[columnIndex]);
                }
                else
                {
                    value = Convert.ToSByte(reader[columnIndex]);
                }
            }
            else if (fieldType.Equals(typeof(UInt16)))
            {
                value = Convert.ToUInt16(reader[columnIndex]);
            }
            else if (fieldType.Equals(typeof(UInt32)))
            {
                if (entityType.Equals(typeof(UInt64)) || entityType.Equals(typeof(Nullable<UInt64>)))
                {
                    value = Convert.ToUInt64(reader[columnIndex]);   //For OrderId compatibility.
                }
                else if (entityType.Equals(typeof(UInt16)) || entityType.Equals(typeof(Nullable<UInt16>)))
                {
                    value = Convert.ToUInt16(reader[columnIndex]);     //For Year field type condition.
                }
                else
                {
                    value = Convert.ToUInt32(reader[columnIndex]);
                }
            }
            else if (fieldType.Equals(typeof(UInt64)))
            {
                if (entityType.Equals(typeof(UInt32)) || entityType.Equals(typeof(Nullable<UInt32>)))     //For MySql(Select 0) & For OrderId compatibility.
                {
                    value = Convert.ToUInt32(reader[columnIndex]);
                }
                else if (entityType.Equals(typeof(Boolean)) || entityType.Equals(typeof(Nullable<Boolean>)))    //如果是bit(2~64),而Entity写成了Boolean，此处填这个坑
                {
                    value = Convert.ToBoolean(reader[columnIndex]);
                }
                else
                {
                    value = Convert.ToUInt64(reader[columnIndex]);
                }
            }
            else if (fieldType.Equals(typeof(TimeSpan)))
            {
                value = TimeSpan.Parse(reader[columnIndex].ToString());
            }
            else if (fieldType.Equals(typeof(Guid)))
            {
                value = reader.GetGuid(columnIndex);
            }
            else if (fieldType.Name.Equals("MySqlDateTime"))
            {
                value = reader.GetDateTime(columnIndex);
            }
            else
            {
                value = reader[columnIndex];
            }

            return value;
        }

        /// <summary>
        /// Overloading
        /// </summary>
        /// <param name="columnIndex"></param>
        /// <param name="fieldType"></param>
        /// <param name="dataRow"></param>
        /// <returns></returns>
        private static Object getFieldValue(Int32 columnIndex, Type fieldType, Type entityType, DataRow dataRow)
        {
            Object value = null;

            if (columnIndex < 0)
            {
                return value;
            }

            if (fieldType == null)
            {
                return value;
            }

            if (dataRow == null)
            {
                return value;
            }

            if (dataRow.IsNull(columnIndex))
            {
                return value;
            }

            if (fieldType.Equals(typeof(String)))
            {
                String fieldValue = dataRow.Field<String>(columnIndex);

                if (entityType.Equals(typeof(Char)) || entityType.Equals(typeof(Nullable<Char>)))
                {
                    value = String.IsNullOrEmpty(fieldValue) ? default(Char) : fieldValue[0];
                }
                else
                {
                    value = fieldValue;
                }
            }
            else if (fieldType.Equals(typeof(Boolean)))
            {
                value = dataRow.Field<Boolean>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Int32)))
            {
                if (entityType.Equals(typeof(Int64)) || entityType.Equals(typeof(Nullable<Int64>)))
                {
                    value = Convert.ToInt64(dataRow[columnIndex]);      //For OrderId compatibility.
                }
                else if (entityType.Equals(typeof(Int16)) || entityType.Equals(typeof(Nullable<Int16>)))
                {
                    value = Convert.ToInt16(dataRow[columnIndex]);      //For Year field type condition.
                }
                else
                {
                    value = dataRow.Field<Int32>(columnIndex);
                }
            }
            else if (fieldType.Equals(typeof(Int64)))
            {
                if (entityType.Equals(typeof(Int32)) || entityType.Equals(typeof(Nullable<Int32>)))     //For MySql(Select 0) & For OrderId compatibility.
                {
                    value = Convert.ToInt32(dataRow[columnIndex]);
                }
                else
                {
                    value = dataRow.Field<Int64>(columnIndex);
                }
            }
            else if (fieldType.Equals(typeof(DateTime)))
            {
                value = dataRow.Field<DateTime>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Decimal)))
            {
                value = dataRow.Field<Decimal>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Byte)))
            {
                value = dataRow.Field<Byte>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Byte[])))
            {
                value = dataRow.Field<Byte[]>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Int16)))
            {
                value = dataRow.Field<Int16>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Char)))
            {
                value = dataRow.Field<Char>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Single)))
            {
                value = dataRow.Field<Single>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Double)))
            {
                value = dataRow.Field<Double>(columnIndex);
            }
            else if (fieldType.Equals(typeof(SByte)))
            {
                if (entityType.Equals(typeof(Byte)) || entityType.Equals(typeof(Nullable<Byte>)))
                {
                    value = Convert.ToByte(dataRow[columnIndex]);     //May cause precision lost.
                }
                else if (entityType.Equals(typeof(Boolean)) || entityType.Equals(typeof(Nullable<Boolean>)))
                {
                    value = Convert.ToBoolean(dataRow[columnIndex]);
                }
                else
                {
                    value = dataRow.Field<SByte>(columnIndex);
                }
            }
            else if (fieldType.Equals(typeof(UInt16)))
            {
                value = dataRow.Field<UInt16>(columnIndex);
            }
            else if (fieldType.Equals(typeof(UInt32)))
            {
                if (entityType.Equals(typeof(UInt64)) || entityType.Equals(typeof(Nullable<UInt64>)))
                {
                    value = Convert.ToUInt64(dataRow[columnIndex]);   //For OrderId compatibility.
                }
                else if (entityType.Equals(typeof(UInt16)) || entityType.Equals(typeof(Nullable<UInt16>)))
                {
                    value = Convert.ToUInt16(dataRow[columnIndex]);     //For Year field type condition.
                }
                else
                {
                    value = dataRow.Field<UInt32>(columnIndex);
                }
            }
            else if (fieldType.Equals(typeof(UInt64)))
            {
                if (entityType.Equals(typeof(UInt32)) || entityType.Equals(typeof(Nullable<UInt32>)))     //For MySql(Select 0) & For OrderId compatibility.
                {
                    value = Convert.ToUInt32(dataRow[columnIndex]);
                }
                else if (entityType.Equals(typeof(Boolean)) || entityType.Equals(typeof(Nullable<Boolean>)))
                {
                    value = Convert.ToBoolean(dataRow[columnIndex]);
                }
                else
                {
                    value = dataRow.Field<UInt64>(columnIndex);
                }
            }
            else if (fieldType.Equals(typeof(TimeSpan)))
            {
                value = dataRow.Field<TimeSpan>(columnIndex);
            }
            else if (fieldType.Equals(typeof(Guid)))
            {
                value = dataRow.Field<Guid>(columnIndex);
            }
            else if (fieldType.Name.Equals("MySqlDateTime"))
            {
                value = Convert.ToDateTime(dataRow[columnIndex]);
            }
            else
            {
                value = dataRow.Field<Object>(columnIndex);
            }

            return value;
        }

        public static void FillBySingleFied<T>(IDataReader reader, IList<T> list)
        {
            if (reader == null || list == null) return;

            try
            {
                IDictionary<String, Int32> columnNameDict = new Dictionary<String, Int32>();
                IDictionary<Int32, Type> typeDict = new Dictionary<Int32, Type>();

                while (reader.Read())
                {
                    Int32 columnIndex = 0;
                    Type columnType = getFieldType(typeDict, columnIndex, reader);
                    Object value = getFieldValue(columnIndex, columnType, typeof(T), reader);
                    T t = TypeUtils.ChangeType<T>(value);
                    list.Add(t);
                }
            }
            catch
            {
                throw;
            }
        }

        public static void FillByName<T>(IDataReader reader, List<SqlColumn> columns, IList<T> list)
        {
            if (reader == null || list == null || columns == null) return;

            try
            {
                ISet<String> fieldList = new HashSet<String>();

                for (Int32 i = 0; i < reader.FieldCount; i++)
                {
                    fieldList.Add(reader.GetName(i).ToLower());
                }

                var sqlColumns = columns.FindAll(p => fieldList.Contains(p.Name.ToLower()));

                if (sqlColumns != null && sqlColumns.Count > 0)
                {
                    IDictionary<String, Int32> columnNameDict = new Dictionary<String, Int32>();
                    IDictionary<Int32, Type> typeDict = new Dictionary<Int32, Type>();

                    while (reader.Read())
                    {
                        T item = Activator.CreateInstance<T>();

                        foreach (SqlColumn column in sqlColumns)
                        {
                            Int32 columnIndex = getFieldIndex(columnNameDict, column.Name, reader);
                            Type columnType = getFieldType(typeDict, columnIndex, reader);
                            Object value = getFieldValue(columnIndex, columnType, column.DataType, reader);
                            column.SetValue(item, value);
                        }

                        list.Add(item);
                    }
                }
            }
            catch
            {
                throw;
            }
        }

        public static void FillDataTableByName<T>(DataTable dataTable, List<SqlColumn> columns, Type type, IList<T> list)
        {
            if (dataTable == null || list == null || columns == null) return;

            Int32 columnCount = dataTable.Columns.Count;
            if (columnCount == 0) return;

            Int32 rowCount = dataTable.Rows.Count;
            if (rowCount == 0) return;

            try
            {
                ISet<String> fieldList = new HashSet<String>();

                foreach (DataColumn item in dataTable.Columns)
                {
                    fieldList.Add(item.ColumnName.ToLower());
                }

                List<SqlColumn> sqlColumns = columns.FindAll(p => fieldList.Contains(p.Name.ToLower()));

                if (sqlColumns != null && sqlColumns.Count > 0)
                {
                    IDictionary<String, Int32> columnNameDict = new Dictionary<String, Int32>();
                    IDictionary<Int32, Type> typeDict = new Dictionary<Int32, Type>();

                    foreach (DataRow dataRow in dataTable.Rows)
                    {
                        T item = Activator.CreateInstance<T>();

                        foreach (SqlColumn column in sqlColumns)
                        {
                            Int32 columnIndex = getFieldIndex(columnNameDict, column.Name, dataTable);
                            Type columnType = getFieldType(typeDict, columnIndex, dataTable);
                            Object value = getFieldValue(columnIndex, columnType, column.DataType, dataRow);
                            column.SetValue(item, value);
                        }

                        list.Add(item);
                    }
                }
            }
            catch
            {
                throw;
            }
        }

        public static void FillFirstByName<T>(IDataReader reader, List<SqlColumn> columns, ref T obj)
        {
            if (reader == null || columns == null) return;

            try
            {
                ISet<String> fieldList = new HashSet<String>();

                for (Int32 i = 0; i < reader.FieldCount; i++)
                {
                    fieldList.Add(reader.GetName(i).ToLower());
                }

                List<SqlColumn> sqlColumns = columns.FindAll(p => fieldList.Contains(p.Name.ToLower()));

                if (sqlColumns != null && sqlColumns.Count > 0)
                {
                    IDictionary<String, Int32> columnNameDict = new Dictionary<String, Int32>();
                    IDictionary<Int32, Type> typeDict = new Dictionary<Int32, Type>();

                    if (reader.Read())
                    {
                        obj = Activator.CreateInstance<T>();

                        foreach (SqlColumn column in sqlColumns)
                        {
                            Int32 columnIndex = getFieldIndex(columnNameDict, column.Name, reader);
                            Type columnType = getFieldType(typeDict, columnIndex, reader);
                            Object value = getFieldValue(columnIndex, columnType, column.DataType, reader);
                            column.SetValue(obj, value);
                        }
                    }
                }
            }
            catch
            {
                throw;
            }
        }

        [Obsolete]
        public static void FillFirstDataTableByName<T>(DataTable dt, List<SqlColumn> columns, ref T obj)
        {
            try
            {
                if (dt == null || dt.Rows.Count <= 0) return;
                int fic = dt.Columns.Count;

                var fieldList = new List<string>();
                for (int j = 0; j < fic; j++)
                {
                    fieldList.Add(dt.Columns[j].ColumnName.ToLower());

                }
                var resultCols = columns.FindAll(p => fieldList.Contains(p.Name));
                obj = Activator.CreateInstance<T>();
                foreach (SqlColumn column in resultCols)
                {
                    object val = dt.Rows[0][column.Name];
                    column.SetValue(obj, val);
                }
            }
            catch
            {
                throw;
            }
        }

    }
}
