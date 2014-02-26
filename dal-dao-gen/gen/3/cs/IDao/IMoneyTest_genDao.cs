using System;
using System.Collections.Generic;
using com.ctrip.platform.uat.Entity.DataModel;

namespace com.ctrip.platform.uat.Interface.IDao
{
	public partial interface IMoneyTest_genDao
	{

	       /// <summary>
        ///  插入MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>新增的主键</returns>
        int InsertMoneyTest_gen(MoneyTest_gen moneyTest_gen);

        /// <summary>
        /// 修改MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdateMoneyTest_gen(MoneyTest_gen moneyTest_gen);

        /// <summary>
        /// 删除MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeleteMoneyTest_gen(MoneyTest_gen moneyTest_gen);


        /// <summary>
        /// 根据主键获取MoneyTest_gen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>MoneyTest_gen信息</returns>
        MoneyTest_gen FindByPk(uint id);

        /// <summary>
        /// 获取所有MoneyTest_gen信息
        /// </summary>
        /// <returns>MoneyTest_gen列表</returns>
        IList<MoneyTest_gen> GetAll();




        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();


	}
}