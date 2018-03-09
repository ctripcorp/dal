using Arch.Data.DbEngine.DB;
using System;
using System.Collections.Generic;

namespace Arch.Data.DbEngine.HA
{
    interface IHA
    {
        HashSet<Int32> RetryFailOverErrorCodes { get; }

        T ExecuteWithHa<T>(Func<Database, T> func, OperationalDatabases databases);
    }
}
