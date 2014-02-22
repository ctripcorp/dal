using System;
using Arch.Data.Orm;

namespace ${host.getNameSpaceEntity()}
{
	/// <summary>
    /// ${host.getSpName()}
    /// </summary>
	[Serializable]
	public partial class ${host.getClassName()}
	{
#foreach($p in $host.getSpParams())
        /// <summary>
        /// ${WordUtils.capitalizeFully($p.getName().replace("@",""))}
        /// </summary>
		public ${p.getType()}#if($p.isNullable())?#end #if($WordUtils.capitalizeFully($p.getName()) == $host.getClassName())${host.getClassName()}_${host.getClassName()}:${WordUtils.capitalizeFully($p.getName().replace("@",""))} { get; set; }
#end
	}
}