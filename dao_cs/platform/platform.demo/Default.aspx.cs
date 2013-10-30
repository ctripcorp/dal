using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using platform.dao;
using platform.dao.client;
using platform.demo.DAO;
using System.Data;
using platform.dao.log;
using System.Diagnostics;

namespace platform.demo
{
    public partial class _Default : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            string port = Request.QueryString["port"];
            string dbName = Request.QueryString["db"];
            string sql = Request.QueryString["sql"];
            if (!string.IsNullOrEmpty(port))
            {
                Consts.ServerPort = int.Parse(port);
                string jsonData = Newtonsoft.Json.JsonConvert.SerializeObject(new { Success=true});
                Response.Clear();
                Response.ContentType = "application/json; charset=utf-8";
                Response.Write(jsonData);
                Response.End();
            }
            if (!string.IsNullOrEmpty(dbName))
            {
                //AbstractDAO.Reload(true, dbName);
                ClientPool.GetInstance().CreateDasClient(dbName, null);
                ClientPool.GetInstance().DefaultName = dbName;
                ClientPool.GetInstance().Hello = ClientPool.GetInstance().Hello + 1;
                string jsonData = Newtonsoft.Json.JsonConvert.SerializeObject(new { Success=true});
                Response.Clear();
                Response.ContentType = "application/json; charset=utf-8";
                Response.Write(jsonData);
                Response.End();
            }
            if (!string.IsNullOrEmpty(sql))
            {
                PersonDAO person = new PersonDAO();

                List<object> results = new List<object>();

                int count = 0;

                Stopwatch watch = new Stopwatch();

                watch.Start();

                using (IDataReader reader = person.ExecuteSql(sql))
                {
                    while (reader.Read())
                    {
                        count++;
                    }
                }

                watch.Stop();

                MonitorData data = MonitorData.GetInstance();

                if (data != null)
                {
                    data.TotalTime = watch.ElapsedMilliseconds;
                    MonitorSender.GetInstance().Send(data);
                }


                string jsonData = Newtonsoft.Json.JsonConvert.SerializeObject(new { Count = count });
                Response.Clear();
                Response.ContentType = "application/json; charset=utf-8";
                Response.Write(jsonData);
                Response.End();

            }
        }
    }
}
