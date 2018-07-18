using Arch.Data.DbEngine.DB;

namespace Arch.Data.DbEngine.RW
{
    public interface IReadWriteSplitting
    {
        OperationalDatabases GetOperationalDatabases(Statement statement);
    }
}