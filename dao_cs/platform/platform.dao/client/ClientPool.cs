using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Configuration;

namespace platform.dao.client
{
    public sealed class ClientPool
    {

        private static ClientPool pool = new ClientPool();
        private IClient currentClient;
        private  bool directConnect;

        private Dictionary<string, IClient> clients;
        private IList<string> logicDbs;
        private object lock_obj = new object();

        private ClientPool()
        {
            //是否直连数据库
            directConnect = bool.Parse(ConfigurationManager.AppSettings["UseDirectConnection"]);
            if (directConnect)
            {
                string dbName = ConfigurationManager.AppSettings["DirectDbName"];
                string providername = ConfigurationManager.ConnectionStrings[dbName].ProviderName;
                string connString = ConfigurationManager.ConnectionStrings[dbName].ConnectionString;

                currentClient = new DbClient(providername, connString);
            }
            else
            {
                string logicDbNames = ConfigurationManager.AppSettings["LogicDbNames"];
                string defaultDbName = ConfigurationManager.AppSettings["DefaultDb"];
                string dasCredential = ConfigurationManager.AppSettings["DasCredential"];

                logicDbs = new List<string>();

                clients = new Dictionary<string, IClient>();
                foreach (var db in logicDbNames.Split(';'))
                {
                    logicDbs.Add(db);
                    IClient client = new DasClient(db, dasCredential);
                    if (currentClient == null)
                    {
                        currentClient = client;
                    }
                    clients.Add(db, client);
                }

                if (clients.ContainsKey(defaultDbName))
                {
                    currentClient = clients[defaultDbName];
                }

            }
        }

        /// <summary>
        /// 获取单例对象
        /// </summary>
        /// <returns></returns>
        public static ClientPool GetInstance()
        {
            return pool;
        }

        public IClient CurrentClient { get { return currentClient;} }

        public IList<string> LogicDbNames
        {
            get { return logicDbs;}
        }

        public IClient ChangeClient(string dbName)
        {
            lock (lock_obj)
            {
                currentClient = clients[dbName];
                return currentClient;
            }
        }


    }
}
