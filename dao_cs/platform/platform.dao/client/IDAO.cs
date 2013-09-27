using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.client
{
    public interface IDAO<T>
    {
        /// <summary>
        /// 根据自增主键查找
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        T FindByPk(int iD);

        /// <summary>
        /// 根据自增主键删除
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        int DeleteByPk(int iD);

        /// <summary>
        /// 删除一个跟Entity匹配的
        /// </summary>
        /// <param name="entity"></param>
        /// <returns></returns>
        int Delete(T entity);

        /// <summary>
        /// 插入一条数据
        /// </summary>
        /// <param name="entity"></param>
        /// <returns></returns>
        int Insert(T entity);

        /// <summary>
        /// 批量插入数据
        /// </summary>
        /// <param name="entities"></param>
        /// <returns></returns>
        int BatchInsert(IList<T> entities);

        /// <summary>
        /// 更新一条数据
        /// </summary>
        /// <param name="entity"></param>
        /// <returns></returns>
        int Update(T entity);

        /// <summary>
        /// 获取所有的数据
        /// </summary>
        /// <returns></returns>
        IList<T> GetAll();

        /// <summary>
        /// 删除所有数据
        /// </summary>
        /// <returns></returns>
        int DeleteAll();

    }
}
