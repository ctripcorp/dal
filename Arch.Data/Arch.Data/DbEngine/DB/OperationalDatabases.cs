using System.Collections.Generic;

namespace Arch.Data.DbEngine.DB
{
    /// <summary>
    /// 一次数据库操作，牵涉到的所有Database对象
    /// </summary>
    public class OperationalDatabases
    {
        /// <summary>
        /// 优先在此Database上进行数据库操作
        /// </summary>
        public Database FirstCandidate { get; set; }

        /// <summary>
        /// 备选Databse，按照优先级从高到低
        /// </summary>
        public IList<Database> OtherCandidates { get; set; }

    }
}
