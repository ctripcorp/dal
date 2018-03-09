using System;

namespace Arch.Data.Orm
{
    /// <summary>
    /// 约束接口
    /// </summary>
    public interface IConstraint
    {
        /// <summary>
        /// 别名
        /// </summary
        String Column { get; } //alias

        /// <summary>
        /// 操作，例如：=
        /// </summary>
        String Operator { get; } //=, <>, ...

        /// <summary>
        /// 
        /// </summary>
        Object Value { get; set; }

        /// <summary>
        /// 
        /// </summary>
        Boolean HasQuery { get; }

        /// <summary>
        /// 
        /// </summary>
        IQuery Query { get; }
    }

}
