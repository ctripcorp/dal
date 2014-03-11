using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using DAL.Entity.DataModel;

namespace DAL.Test
{
    public class PerTestTest
    {
        public static void Test()
        {
            IList<PerUser> GetPersonResult = GetPerson();

        }
    }
}