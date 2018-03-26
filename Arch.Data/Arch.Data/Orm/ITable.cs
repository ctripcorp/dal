using System;

namespace Arch.Data.Orm
{
    public interface ITable
    {
        Type Class { get; }

        String Name { get; }

        String Schema { get; }

        IColumn[] Columns { get; }
    }
}
