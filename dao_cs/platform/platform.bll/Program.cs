using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Data;
using System.Data.SqlClient;

namespace platform.bll
{
    class Program
    {
        static void Main(string[] args)
        {

            PersonDAO person = new PersonDAO();

            while (Console.ReadKey().Key != ConsoleKey.Escape)
            {

                using (IDataReader reader = person.GetAddrNameByPk(21))
                {
                    while (reader.Read())
                    {
                        Console.WriteLine(reader["Address"]);
                        Console.WriteLine(reader["Name"]);

                    }
                }
            }
            //Console.Read();

        }
    }
}
