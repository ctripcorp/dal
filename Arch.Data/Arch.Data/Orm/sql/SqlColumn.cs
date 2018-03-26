using System;
using System.Data;

namespace Arch.Data.Orm.sql
{
    public class SqlColumn : IColumn
    {
        public SqlColumn(ITable table, String name, IDataBridge data)
        {
            Ordinal = -1;
            Table = table;
            Name = name.ToLower();
            Data = data;
        }

        public IDataBridge Data { get; set; }

        public Int32 Ordinal { get; set; }

        public ITable Table { get; private set; }

        public Type DataType
        {
            get { return Data.DataType; }
        }

        public Int32 Length { get; set; }

        public String Name { get; private set; }

        public DbType? ColumnType { get; set; }

        public Object DefaultValue { get; set; }

        private String alias;

        public String Alias
        {
            get { return String.IsNullOrEmpty(alias) ? Name : alias; }
            set { alias = (value == null) ? null : value.ToLower(); }
        }

        public String TimestampExpression { get; set; }

        public Boolean IsPK { get; set; }

        public Boolean IsOutPut { get; set; }

        public Boolean IsVersionLock { get; set; }

        public Boolean IsTimestampLock { get; set; }

        public Boolean IsReturnValue { get; set; }

        public Boolean IsID { get; set; }

        public Boolean IsReadable
        {
            get { return Data.Readable; }
        }

        public Boolean IsWriteable
        {
            get { return Data.Writeable; }
        }

        public void SetParameterValue(IDbDataParameter p, Object obj)
        {
            Object val = Data.Read(obj);
            p.Value = val ?? DBNull.Value;
        }

        public void SetValue(Object obj, Object val)
        {
            try
            {
                if (val == null || val == DBNull.Value)
                {
                    Data.Write(obj, DefaultValue);
                }
                else
                {
                    Data.Write(obj, val);
                }
            }
            catch (Exception ex)
            {
                throw new DalException(String.Format("Assign {0} to {1} failed,data type of property is {2}.", val, Name, DataType), ex);
            }
        }

        public Boolean IsIgnored { get; set; }
    }
}
