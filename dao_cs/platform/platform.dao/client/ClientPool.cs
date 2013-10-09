using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.client
{
    public sealed class ClientPool
    {

        private static ClientPool pool = new ClientPool();
        private Dictionary<string, IClient> clients;
        private object lock_obj = new object();
        //private string defaultName = string.Empty;

        public string DefaultName { get; set; }

        public int Hello { get; set; }

        private ClientPool()
        {
            clients = new Dictionary<string, IClient>();
        }

        public static ClientPool GetInstance()
        {
            return pool;
        }

        public IClient GetCurrentClient(string dbName=null)
        {
            lock (lock_obj)
            {
                if (string.IsNullOrEmpty(dbName))
                    dbName = DefaultName;
                return clients[dbName];
            }
        }

        public bool CreateDasClient(string dbName, string credential)
        {
            lock (lock_obj)
            {
                //defaultName = dbName;
                //TODO: thread safety
                if (clients.ContainsKey(dbName))
                {
                    return false;
                }
                else
                {
                    DasClient dasClient = new DasClient(dbName, credential);
                    clients.Add(dbName, dasClient);
                    return true;
                }
            }
        }

        public bool CreateDbClient(string connectName, string providerType, string credential)
        {
            lock (lock_obj)
            {
                //defaultName = connectName;
                //TODO: thread safety
                if (clients.ContainsKey(connectName))
                {
                    return false;
                }
                else
                {
                    DbClient dbClient = new DbClient(providerType, credential);
                    clients.Add(connectName, dbClient);
                    return true;
                }
            }
        }

    }
}
