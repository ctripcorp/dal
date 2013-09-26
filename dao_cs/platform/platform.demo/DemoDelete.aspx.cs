using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Data;
using platform.demo.DAO;

namespace platform.demo
{
    public partial class DemoDelete : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            PersonDAO person = new PersonDAO();

            //List<object> results = new List<object>();

            var id= this.Request.QueryString["task_id"];


            person.deleteById(int.Parse(id));

            string jsonData = Newtonsoft.Json.JsonConvert.SerializeObject(new { Success = true});
            Response.Clear();
            Response.ContentType = "application/json; charset=utf-8";
            Response.Write(jsonData);
            Response.End();
        }
    }
}