
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.dal.test.test4.Entity.DataModel;

namespace com.ctrip.dal.test.test4.Test
{
    public class TestSearchTableTest
    {
        public static void Test()
        {
            int execTestSearchTableResult = DALFactory.TestSearchTableDao.ExecTestSearchTable(new TestSearchTable());
        }
        
    }
}
