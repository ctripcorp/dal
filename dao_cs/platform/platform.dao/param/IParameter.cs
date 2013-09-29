using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using MsgPack;

namespace platform.dao.param
{
    public interface IParameter
    {
        /// <summary>
        /// 对应的数据库类型
        /// </summary>
        DbType DbType { get; set; }

        /// <summary>
        /// 参数Input或是Output
        /// </summary>
        ParameterDirection Direction { get; set; }

        /// <summary>
        /// 是否可空
        /// </summary>
        bool IsNullable { get; set; }

        /// <summary>
        /// 参数名
        /// </summary>
        string Name { get; set; }

        /// <summary>
        /// 参数编号
        /// </summary>
        int Index { get; set; }

        /// <summary>
        /// 字段大小
        /// </summary>
        int Size { get; set; }

        /// <summary>
        /// 参数值
        /// </summary>
        MessagePackObject Value { get; set; }
        
        /// <summary>
        /// 是否包含敏感字符
        /// </summary>
        bool IsSensitive { get; set; }

    }
}
