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
        /// ${WordUtils.capitalize($p.getName().replace("@",""))}
        /// </summary>
		public ${p.getType()}#if($p.isNullable())?#end #if($WordUtils.capitalize($p.getName()) == $host.getClassName())${host.getClassName()}_${host.getClassName()}#{else}${WordUtils.capitalize($p.getName().replace("@",""))}#end { get; set; }
#end
	}
}