using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using platform.dao.type;

namespace platform.dao.param
{
    /// <summary>
    /// 对于In类型的参数，将@variable替换为多个?，然后根据传入参数的多少，
    /// 填入相应数量的？，传向DAS或者DbClient
    /// 其他参数直接替换为？，传向DAS或者DbClient
    /// </summary>
    public sealed class StatementParameter : IParameter
    {

        private DbType dbType = DbType.Boolean;
        private ParameterDirection direction = ParameterDirection.Input;
        private bool isNullable = false;
        private string name;
        private int index;
        private int size;
        private object value;
        private bool isSensitive = false;

        public DbType DbType
        {
            get
            {
                return dbType;
            }
            set
            {
                dbType = value;   
            }
        }

        public ParameterDirection Direction
        {
            get
            {
                return direction;
            }
            set
            {
                direction = value;
            }
        }

        public bool IsNullable
        {
            get
            {
                return isNullable;
            }
            set
            {
                isNullable = value;
            }
        }

        public string Name
        {
            get
            {
                return name;
            }
            set
            {
                name = value;
            }
        }

        public int Index
        {
            get
            {
                return index;
            }
            set
            {
                index = value;
            }
        }

        public int Size
        {
            get
            {
                return size;
            }
            set
            {
                size = value;
            }
        }

        public object Value
        {
            get
            {
                return value;
            }
            set
            {
                this.value = value;
            }
        }

        public bool IsSensitive
        {
            get
            {
                return isSensitive;
            }
            set
            {
                isSensitive = value;
            }
        }


        public param.AvailableType GetFromObject()
        {
            switch (dbType)
            {
                case System.Data.DbType.Boolean:
                    return new param.AvailableType() { current = 0, bool_arg = (bool)value};
                case System.Data.DbType.Byte:
                case System.Data.DbType.SByte:
                case System.Data.DbType.UInt16:
                case System.Data.DbType.Int16:
                case System.Data.DbType.Int32:
                case System.Data.DbType.UInt32:
                case System.Data.DbType.StringFixedLength:
                    return new param.AvailableType(){current = 1, int32_arg = (int)value};
                case System.Data.DbType.Int64:
                case System.Data.DbType.UInt64:
                    return new param.AvailableType() { current = 2, int64_arg = (long)value};
                case System.Data.DbType.DateTime:
                    return new param.AvailableType() { current = 2, int64_arg = ((DateTime)value).Ticks / TimeSpan.TicksPerMillisecond };
                case System.Data.DbType.Single:
                case System.Data.DbType.Double:
                    return new param.AvailableType() { current = 3, double_arg = (double)value };
                case System.Data.DbType.AnsiString:
                case System.Data.DbType.AnsiStringFixedLength:
                case System.Data.DbType.String:
                case System.Data.DbType.Decimal:
                    return new param.AvailableType() { current = 4, string_arg = value.ToString()};
                case System.Data.DbType.Binary:
                    return new param.AvailableType() { current = 5, bytes_arg = (byte[])value};
                case System.Data.DbType.Guid:
                    return new param.AvailableType() { current = 5, bytes_arg = ((Guid)value).ToByteArray() };
                default:
                    return new param.AvailableType() { current = 4, string_arg = value.ToString() };
            }
        }
    }
}
