using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;
using platform.dao.param;

namespace platform.dao.request
{
    public class RequestMessage
    {
        /// <summary>
        /// SQL指令类型，SP或SQL
        /// </summary>
        public StatementType StatementType { get; set; }

        /// <summary>
        /// 操作类型，读写分离使用
        /// </summary>
        public OperationType OperationType { get; set; }

        /// <summary>
        /// 是否使用缓存
        /// </summary>
        public bool UseCache { get; set; }

        /// <summary>
        /// SP名
        /// </summary>
        public string SpName { get; set; }

        /// <summary>
        /// SQL语句
        /// </summary>
        public string Sql { get; set; }

        /// <summary>
        /// 参数
        /// </summary>
        public StatementParameterCollection Parameters { get; set; }

        /// <summary>
        /// 标识
        /// </summary>
        public int Flags { get; set; }


        public int GetPropertyCount()
        {
            return 6;
        }

    }
}
