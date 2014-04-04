
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
    public class GetretTest
    {
        public static void Test()
        {
            int execGetretResult = DALFactory.GetretDao.ExecGetret(new Getret());
        }
        
    }
}
