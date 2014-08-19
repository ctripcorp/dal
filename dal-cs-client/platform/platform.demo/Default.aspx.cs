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
            
            
            string sql = Request.QueryString["sql"];

            if (!string.IsNullOrEmpty(sql))
            {
                CommonDAO person = new CommonDAO();

                List<object> results = new List<object>();

                int count = 0;

                //Stopwatch watch = new Stopwatch();

                //watch.Start();

                using (IDataReader reader = person.ExecuteSql(sql))
                {
                    if (null != reader)
                    {
                        while (reader.Read())
                        {
                            count++;
                        }
                    }
                }

                //watch.Stop();

                //MonitorData data = MonitorData.GetInstance();

                //if (data != null)
                //{
                //    data.TotalTime = watch.ElapsedMilliseconds;
                //    data.TotalCount = count;
                //    MonitorSender.GetInstance().Send(data);
                //}


                string jsonData = Newtonsoft.Json.JsonConvert.SerializeObject(new { Count = count });
                Response.Clear();
                Response.ContentType = "application/json; charset=utf-8";
                Response.Write(jsonData);
                Response.End();

            }
        }
    }
}
