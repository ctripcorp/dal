using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using DAL.Entity.DataModel;
using DAL.Interface.IDao;
using DAL.Dao;

namespace DAL.Test
{
    public class PersonGenTest
    {
        public static void Test()
        {
            //以下方法的主要目的是教会您如何使用DAL
            //在实际使用的时候，您需要根据不同的情形
            //反注释相应的代码，并传入合法的参数
            //-------其他可用的方法，VS的intellisense会告诉您的---------
            IPersonGenDao personGenDao = DALFactory.PersonGenDao;

            int insertResult = personGenDao.InsertPersonGen(new PersonGen());

            int updateResult = personGenDao.UpdatePersonGen(new PersonGen());

            int deleteResult = personGenDao.DeletePersonGen(new PersonGen());

            var resultsByPk = personGenDao.FindByPk(0);
            var entities = personGenDao.GetAll();

            long count = personGenDao.Count();

            var listByPage = personGenDao.GetListByPage(null, 0, 0);

            int bulkInsertResult = personGenDao.BulkInsertPersonGen(new List<PersonGen>());

            int bulkUpdateResult = personGenDao.BulkUpdatePersonGen(new List<PersonGen>());

            int bulkDeleteResult = personGenDao.BulkDeletePersonGen(new List<PersonGen>());

            var GetNameByIDResult =  personGenDao.GetNameByID(0);

            var deleteByNameResult =  personGenDao.deleteByName(null);

        }
    }
}
