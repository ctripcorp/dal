using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using platform.demo.DAO;
using System.Data;

namespace platform.demo
{
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.Web.Script.Services.ScriptService]
    public class DemoAjax : System.Web.Services.WebService
    {

        [WebMethod]
        public List<object> GetAll()
        {
            PersonDAO person = new PersonDAO();

            List<object> results = new List<object>();

            using (IDataReader reader = person.getAll())
            {
                while (reader.Read())
                {
                    results.Add(new { 
                        ID=reader["ID"],
                        Address = reader["Address"],
                        Name = reader["Name"],
                        Telephone = reader["Telephone"],
                        Age = reader["Age"],
                        Gender = reader["Gender"],
                        Birth = reader["Birth"]
                    });
                }
            }

            return results;
        }
    }
}
