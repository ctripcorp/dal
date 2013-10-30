using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using platform.dao.param;

namespace platform.dao.client
{
    public interface IDAO : IClient
    {

        /// <summary>
        /// 根据自增主键查找
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        T FetchByPk<T>(int iD);

        /// <summary>
        /// 根据自增主键删除
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        int DeleteByPk<T>(int iD);

        /// <summary>
        /// 插入一条数据
        /// </summary>
        /// <param name="entity"></param>
        /// <returns></returns>
        int Insert<T>(T entity);

        /// <summary>
        /// 批量插入数据
        /// </summary>
        /// <param name="entities"></param>
        /// <returns></returns>
        int BatchInsert<T>(IList<T> entities);

        /// <summary>
        /// 更新一条数据
        /// </summary>
        /// <param name="entity"></param>
        /// <returns></returns>
        int Update<T>(T entity);

        /// <summary>
        /// 获取所有的数据
        /// </summary>
        /// <returns></returns>
        IList<T> FetchAll<T>();

        /// <summary>
        /// 删除所有数据
        /// </summary>
        /// <returns></returns>
        int DeleteAll<T>();

    }
}
