using System;
using System.Collections.Generic;
using com.ctrip.platform.tools.Entity.DataModel;

namespace com.ctrip.platform.tools.Interface.IDao
{
	public partial interface IPerson_genDao
	{

	       /// <summary>
        ///  插入Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象</param>
        /// <returns>新增的主键</returns>
        int InsertPerson_gen(Person_gen person_gen);

        /// <summary>
        /// 修改Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdatePerson_gen(Person_gen person_gen);

        /// <summary>
        /// 删除Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象</param>
        /// <returns>状态代码</returns>
        int DeletePerson_gen(Person_gen person_gen);


        /// <summary>
        /// 根据主键获取Person_gen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>Person_gen信息</returns>
        Person_gen FindByPk(uint iD);

        /// <summary>
        /// 获取所有Person_gen信息
        /// </summary>
        /// <returns>Person_gen列表</returns>
        IList<Person_gen> GetAll();

        /// <summary>
        ///  批量插入Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkInsertPeople(IList<Person_gen> person_genList);

        /// <summary>
        ///  批量更新Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkUpdatePeople(IList<Person_gen> person_genList);

        /// <summary>
        ///  批量删除Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkDeletePeople(IList<Person_gen> person_genList);

        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索Person_gen，带翻页
        /// </summary>
        /// <param name="obj">Person_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<Person_gen> GetListByPage(Person_gen obj, int pagesize, int pageNo);

        /// <summary>
        ///  GetNameByID
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        public IList<Person_gen> GetNameByID(uint iD);
        /// <summary>
        ///  deleteByName
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public int deleteByName(string name);
	}
}