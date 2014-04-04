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
    public partial class TestDao : ITestDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AccCorpDB_INSERT_1");
        

        /// <summary>
        /// 获取所有Test信息
        /// </summary>
        /// <returns>Test列表</returns>
        public IList<Test> GetAll()
        {
            try
            {
                return baseDao.GetAll<Test>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用TestDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from test  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用TestDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索Test，带翻页
        /// </summary>
        /// <param name="obj">Test实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<Test> GetListByPage(Test obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by id desc ) as rownum, ");
                sbSql.Append(@"id, status, number, AccBalanceID, AccountID, StartDate, AccountName, CompanyName, accountType, accountTypeName, CheckAccType, CheckAccTypeName, fltconMoney, HtlXconMoney, HtlHconMoney, settleConsume, settleService, settleReturn, conappointed, ContractFirmDay, ContractDay, InvoiceDate, ReportCompletionDay, RptDate, AuditDate, SendRptDate, ConfirmDate, LastAuditDate, RptDateOper, ConfirmDateOper, IsDailyAudit, ReConfirmflag from test (nolock) ");

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
                sbSql.Append(@"select id, status, number, AccBalanceID, AccountID, StartDate, AccountName, CompanyName, accountType, accountTypeName, CheckAccType, CheckAccTypeName, fltconMoney, HtlXconMoney, HtlHconMoney, settleConsume, settleService, settleReturn, conappointed, ContractFirmDay, ContractDay, InvoiceDate, ReportCompletionDay, RptDate, AuditDate, SendRptDate, ConfirmDate, LastAuditDate, RptDateOper, ConfirmDateOper, IsDailyAudit, ReConfirmflag from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<Test> list = baseDao.SelectList<Test>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用TestDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
