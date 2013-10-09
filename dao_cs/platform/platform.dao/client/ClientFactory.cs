using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;

namespace platform.dao.client
{
    public sealed class ClientFactory
    {

        private static Dictionary<string, DasClient> dasClients;
        private static Dictionary<string, DbClient> dbClients;

        static ClientFactory()
        {
            dbClients = new Dictionary<string, DbClient>();
            dasClients = new Dictionary<string, DasClient>();

            //Array values = Enum.GetValues(typeof(DbDialect));

            //foreach (DbDialect val in values)
            //{
            //    dbClients.Add(val, new DbClient(val));
            //}
        }

        public static IClient CreateDasClient(string dbName, string credential)
        {
            //TODO: thread safety
            if (dasClients.ContainsKey(dbName))
            {
                return dbClients[dbName];
            }
            else
            {
                DasClient dasClient = new DasClient(dbName, credential);
                dasClients.Add(dbName, dasClient);
                return dasClient;
            }
        }

        public static IClient CreateDbClient(string providerType, string credential)
        {
            //TODO: thread safety
            if (dbClients.ContainsKey(providerType))
            {
                return dbClients[providerType];
            }
            else
            {
                DbClient dbClient = new DbClient(providerType, credential);
                dbClients.Add(providerType, dbClient);
                return dbClient;
            }
        }

    }
}
