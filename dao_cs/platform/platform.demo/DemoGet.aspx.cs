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
    public partial class DemoGet : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            PersonDAO person = new PersonDAO();

            List<object> results = new List<object>();

            using (IDataReader reader = person.getAll())
            {
                while (reader.Read())
                {
                    results.Add(new
                    {
                        ID = reader["ID"],
                        Address = reader["Address"],
                        Name = reader["Name"],
                        Telephone = reader["Telephone"],
                        Age = reader["Age"],
                        Gender = reader["Gender"],
                        Birth = reader["Birth"]
                    });
                }
            }
            string jsonData = Newtonsoft.Json.JsonConvert.SerializeObject(results);
            Response.Clear();
            Response.ContentType = "application/json; charset=utf-8";
            Response.Write(jsonData);
            Response.End();
        }
    }
}