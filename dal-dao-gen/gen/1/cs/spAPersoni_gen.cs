using System;
using Arch.Data.Orm;

namespace com.ctrip.platform.tools.Entity.DataModel
{
	/// <summary>
    /// dbo.spA_Person_i
    /// </summary>
	[Serializable]
	public partial class spAPersoni_gen
	{
        /// <summary>
        /// ID
        /// </summary>
		public uint ID { get; set; }
        /// <summary>
        /// Name
        /// </summary>
		public string Name { get; set; }
        /// <summary>
        /// Age
        /// </summary>
		public uint Age { get; set; }
        /// <summary>
        /// Birth
        /// </summary>
		public DateTime Birth { get; set; }
	}
}