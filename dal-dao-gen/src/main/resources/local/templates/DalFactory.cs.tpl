using ${namespace}.Interface.IDao;
using ${namespace}.Dao;

namespace ${namespace}
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
#foreach($clazz in $clazzList)
        private static readonly I${clazz}Dao ${WordUtils.uncapitalize($clazz)}Dao = new ${clazz}Dao();

		/// <summary>
        /// Property ${clazz}Dao
        /// </summary>
		public static I${clazz}Dao ${clazz}Dao
        {
            get
            {
                return ${WordUtils.uncapitalize($clazz)}Dao;
            }
        }
#end
	}
}