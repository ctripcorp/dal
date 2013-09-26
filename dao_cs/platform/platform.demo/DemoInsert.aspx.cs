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
    public partial class DemoInsert : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            PersonDAO person = new PersonDAO();

            //List<object> results = new List<object>();

            //var address = this.Request.QueryString["address"];
            //var name = this.Request.QueryString["name"];
            //var telephone = this.Request.QueryString["telephone"];
            //var age = this.Request.QueryString["age"];
            //var gender = this.Request.QueryString["gender"];
            //var birth = this.Request.QueryString["birth"];

            var address = this.Request.Form["address"];
            var name = this.Request.Form["name"];
            var telephone = this.Request.Form["telephone"];
            var age = this.Request.Form["age"];
            var gender = this.Request.Form["gender"];
            var birth = this.Request.Form["birth"];

            person.insertAll(0, address, name, telephone, int.Parse(age), int.Parse(gender), DateTime.Parse(birth));

            string jsonData = Newtonsoft.Json.JsonConvert.SerializeObject(new { Success = true});
            Response.Clear();
            Response.ContentType = "application/json; charset=utf-8";
            Response.Write(jsonData);
            Response.End();
        }
    }
}