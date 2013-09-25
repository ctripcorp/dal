using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using platform.dao.type;

namespace platform.dao.param
{
    /// <summary>
    /// 对于In类型的参数，将@variable替换为多个?，然后根据传入参数的多少，
    /// 填入相应数量的？，传向DAS或者DbClient
    /// 其他参数直接替换为？，传向DAS或者DbClient
    /// </summary>
    public sealed class StatementParameter
    {
        /// <summary>
        /// 数据类型
        /// </summary>
        public DbType DbType { get; set; }

        ParameterDirection _direction = ParameterDirection.Input;
        /// <summary>
        /// 参数方向
        /// </summary>
        public ParameterDirection Direction
        {
            get { return _direction; }
            set { this._direction = value; }
        }

        /// <summary>
        /// 是否可空
        /// </summary>
        public bool IsNullable { get; set; }

        /// <summary>
        /// 参数名称
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// 与参数名称对应的参数序号
        /// </summary>
        public int Index { get; set; }

        /// <summary>
        /// 字段的大小，如Varchar（50）
        /// </summary>
        public int Size { get; set; }

        /// <summary>
        /// 参数值
        /// </summary>
        public object Value { get; set; }

        /// <summary>
        /// 参数是否包含敏感字符
        /// </summary>
        public bool IsSensitive { get; set; }
    }
}
