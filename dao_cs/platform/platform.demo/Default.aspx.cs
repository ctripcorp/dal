using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using platform.dao;
using platform.dao.client;

namespace platform.demo
{
    public partial class _Default : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            string port = Request.QueryString["port"];
            string dbName = Request.QueryString["db"];
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
                AbstractDAO.Reload(true, dbName);
                string jsonData = Newtonsoft.Json.JsonConvert.SerializeObject(new { Success=true});
                Response.Clear();
                Response.ContentType = "application/json; charset=utf-8";
                Response.Write(jsonData);
                Response.End();
            }
        }
    }
}
