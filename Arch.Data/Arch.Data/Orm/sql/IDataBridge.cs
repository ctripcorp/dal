using System;

namespace Arch.Data.Orm.sql
{
    public interface IDataBridge
    {
        Boolean Readable { get; }

        Boolean Writeable { get; }

        Object Read(Object obj);

        void Write(Object obj, Object value);

        Type DataType { get; }
    }
}
