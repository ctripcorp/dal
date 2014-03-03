using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
	/// <summary>
    /// dbo.spT_Person_i
    /// </summary>
	[Serializable]
	public partial class SpTPersoni_gen
	{
        /// <summary>
        /// TVP_Person
        /// </summary>
		public ${p.getType()} TVP_Person { get; set; }
	}
}