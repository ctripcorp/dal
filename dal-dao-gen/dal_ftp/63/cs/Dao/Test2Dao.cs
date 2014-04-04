using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.dal.test.test4.Entity.DataModel;
using com.ctrip.dal.test.test4.Interface.IDao;

namespace com.ctrip.dal.test.test4.Dao
{
   /// <summary>
    /// 更多DAL接口功能，请参阅DAL Confluence，地址：
    /// http://conf.ctripcorp.com/display/SysDev/Dal+Fx+API
    /// </summary>
    public partial class Test2Dao : ITest2Dao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AccCorpDB_INSERT_1");
        

        /// <summary>
        /// 获取所有Test2信息
        /// </summary>
        /// <returns>Test2列表</returns>
        public IList<Test2> GetAll()
        {
            try
            {
                return baseDao.GetAll<Test2>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用Test2Dao时，访问GetAll时出错", ex);
            }
        }
        
        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        public long Count()
        {
            try
            {
                String sql = "SELECT count(1) from test2  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Test2Dao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索Test2，带翻页
        /// </summary>
        /// <param name="obj">Test2实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<Test2> GetListByPage(Test2 obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by AccBalanceID desc ) as rownum, ");
                sbSql.Append(@"AccBalanceID, BatchNo, AccountID, CreateTime, StartDate, EndDate, recReturn, ReportCompletionDay, ContractFirmDay, ContractDay, ReConfirmDate, ContractDate, LastAuditDate, SendRptDate, AuditDate, ReceiveDate, RptDate, ConfirmDate, BalDate, InvoiceDate, ConfirmFlag, LastAuditFlag, SettleFlag from test2 (nolock) ");

                //包含查询条件
                //StringBuilder whereCondition = new StringBuilder();
                //if (!string.IsNullOrEmpty(obj.Name))
                //{
                //    //人名
                //    whereCondition.Append("Where Name like @Name ");
                //    dic.AddInParameter("@Name", DbType.String, "%" + obj.Name + "%");
                //}
                //sbSql.Append(whereCondition);

                sbSql.Append(")"); //WITH CTE 结束

                // 用 CTE 完成分页
                sbSql.Append(@"select AccBalanceID, BatchNo, AccountID, CreateTime, StartDate, EndDate, recReturn, ReportCompletionDay, ContractFirmDay, ContractDay, ReConfirmDate, ContractDate, LastAuditDate, SendRptDate, AuditDate, ReceiveDate, RptDate, ConfirmDate, BalDate, InvoiceDate, ConfirmFlag, LastAuditFlag, SettleFlag from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<Test2> list = baseDao.SelectList<Test2>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Test2Dao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
