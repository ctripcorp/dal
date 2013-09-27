using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Data;
using System.Data.SqlClient;
using platform.bll.DAO;
using platform.bll.Entity;

namespace platform.bll
{
    class Program
    {
        static void Main(string[] args)
        {

            PersonDAO person = new PersonDAO();

            Person p = person.FindByPk(37);

            Console.WriteLine(p.ID);
            Console.WriteLine(p.Address);
            Console.WriteLine(p.Name);
            Console.WriteLine(p.Telephone);
            Console.WriteLine(p.Age);
            Console.WriteLine(p.Gender);
            Console.WriteLine(p.Birth.Value.ToString());

            //while (Console.ReadKey().Key != ConsoleKey.Escape)
            //{

            //    using (IDataReader reader = person.GetAddrNameByPk(21))
            //    {
            //        while (reader.Read())
            //        {
            //            Console.WriteLine(reader["Address"]);
            //            Console.WriteLine(reader["Name"]);

            //        }
            //    }
            //}
            Console.Read();

        }
    }
}
