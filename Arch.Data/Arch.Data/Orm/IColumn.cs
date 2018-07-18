using Arch.Data.Orm.sql;
using System;
using System.Data;

namespace Arch.Data.Orm
{
    public interface IColumn
    {
        ITable Table { get; }

        IDataBridge Data { get; set; }

        String Name { get; }

        Int32 Ordinal { get; set; }

        Int32 Length { get; set; }

        Type DataType { get; }

        DbType? ColumnType { get; set; }

        Boolean IsPK { get; set; }

        Boolean IsID { get; set; }

        Boolean IsReadable { get; }

        Boolean IsTimestampLock { get; set; }

        Boolean IsVersionLock { get; set; }

        String TimestampExpression { get; set; }

        Boolean IsIgnored { get; set; }

    }
}
