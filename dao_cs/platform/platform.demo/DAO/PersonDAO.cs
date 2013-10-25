using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao.client;
using platform.dao.param;

namespace platform.demo.DAO
{
    public class PersonDAO : AbstractDAO
    {


        // None
        public IDataReader FetchAllRecords()
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();


                string sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person ";

                //return client.Fetch(sql, parameters);

                return this.Fetch(sql, parameters.ToArray());

            }
            catch (Exception ex)
            {
                
            }
            return null;
        }



    }
}
