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
    public class TestTest
    {
        public static void Test()
        {
            IList<Test> selectAbcResult = selectAbc(1);

            IList<Test> testQueryResult = testQuery();

            IList<Test> getByIDResult = getByID();

            IList<Test> getByNameResult = getByName("11");

        }
    }
}