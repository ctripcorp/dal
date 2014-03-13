using System;
using System.Collections.Generic;
using DAL.Entity.DataModel;

namespace DAL.Interface.IDao
{
        public partial interface IPersonGenDao
        {

               /// <summary>
        ///  插入PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象</param>
        /// <returns>状态代码</returns>
        int InsertPersonGen(PersonGen personGen);

        /// <summary>
        /// 修改PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象</param>
        /// <returns>状态代码</returns>
        int UpdatePersonGen(PersonGen personGen);

        /// <summary>
        /// 删除PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象</param>
        /// <returns>状态代码</returns>
        int DeletePersonGen(PersonGen personGen);


        /// <summary>
        /// 根据主键获取PersonGen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>PersonGen信息</returns>
        PersonGen FindByPk(int iD);

        /// <summary>
        /// 获取所有PersonGen信息
        /// </summary>
        /// <returns>PersonGen列表</returns>
        IList<PersonGen> GetAll();

        /// <summary>
        ///  批量插入PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkInsertPersonGen(IList<PersonGen> personGenList);

        /// <summary>
        ///  批量更新PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkUpdatePersonGen(IList<PersonGen> personGenList);

        /// <summary>
        ///  批量删除PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象列表</param>
        /// <returns>状态代码</returns>
        int BulkDeletePersonGen(IList<PersonGen> personGenList);

        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        long Count();

        /// <summary>
        ///  检索PersonGen，带翻页
        /// </summary>
        /// <param name="obj">PersonGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        IList<PersonGen> GetListByPage(PersonGen obj, int pagesize, int pageNo);

        /// <summary>
        ///  GetNameByID
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        IList<PersonGen> GetNameByID(int iD);
        /// <summary>
        ///  deleteByName
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        int deleteByName(string name);
        }
}