using System;

namespace Arch.Data.DbEngine.MarkDown
{
    public interface IMarkDown
    {
        void Monitor(String databaseSet, String allInOneKey, Exception ex);

        Boolean IsAbnormalExceptionTimeout(Exception ex);
    }
}