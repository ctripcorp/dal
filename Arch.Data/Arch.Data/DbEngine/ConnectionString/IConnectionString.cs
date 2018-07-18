using System;
using System.Configuration;

namespace Arch.Data.DbEngine.ConnectionString
{
    public interface IConnectionString
    {
        ConnectionStringSettings GetConnectionString(String connectionStringName);
    }
}