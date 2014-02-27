using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
	/// <summary>
    /// dbo.CommonPageSelect
    /// </summary>
	[Serializable]
	public partial class CommonPageSelect_gen
	{
        /// <summary>
        /// SqlTable
        /// </summary>
		public string SqlTable { get; set; }
        /// <summary>
        /// SqlColumn
        /// </summary>
		public string SqlColumn { get; set; }
        /// <summary>
        /// SqlWhere
        /// </summary>
		public string SqlWhere { get; set; }
        /// <summary>
        /// Pagenum
        /// </summary>
		public int Pagenum { get; set; }
        /// <summary>
        /// Beginline
        /// </summary>
		public int Beginline { get; set; }
        /// <summary>
        /// SqlPK
        /// </summary>
		public string SqlPK { get; set; }
        /// <summary>
        /// SqlOrder
        /// </summary>
		public string SqlOrder { get; set; }
        /// <summary>
        /// Count
        /// </summary>
		public long Count { get; set; }
        /// <summary>
        /// PageCount
        /// </summary>
		public int PageCount { get; set; }
	}
}