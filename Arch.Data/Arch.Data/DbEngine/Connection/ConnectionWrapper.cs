using System;
using System.Data.Common;

namespace Arch.Data.DbEngine.Connection
{
    /// <summary>
    /// 数据库链接包装器
    /// 控制数据库链接
    /// </summary>
    class ConnectionWrapper : IDisposable
    {
        #region private field

        /// <summary>
        /// 真正的数据库链接
        /// </summary>
        private readonly DbConnection m_Connection;

        /// <summary>
        /// 是否释放链接
        /// </summary>
        private readonly Boolean m_DisposeConnection;

        /// <summary>
        /// 是否已释放
        /// </summary>
        private Boolean m_Disposed;

        #endregion

        /// <summary>
        /// 构造方法
        /// </summary>
        /// <param name="connection">数据库链接</param>
        /// <param name="disposeConnection">是否释放链接</param>
        public ConnectionWrapper(DbConnection connection, Boolean disposeConnection)
        {
            m_Connection = connection;
            m_DisposeConnection = disposeConnection;
            m_Disposed = false;
        }

        /// <summary>
        /// 数据库链接
        /// </summary>
        public DbConnection Connection
        {
            get { return m_Connection; }
        }

        #region IDisposable Members


        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        ~ConnectionWrapper()
        {
            Dispose(false);
        }

        /// <summary>
        /// 释放链接
        /// </summary>
        public void Dispose(Boolean isDisposing)
        {
            if (m_Disposed) return;
            m_Disposed = true;

            if (isDisposing && m_DisposeConnection)
                m_Connection.Dispose();
        }

        #endregion
    }
}
