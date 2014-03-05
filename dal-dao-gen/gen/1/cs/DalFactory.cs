using com.ctrip.platform.tools.Interface.IDao;
using com.ctrip.platform.tools.Dao;

namespace com.ctrip.platform.tools
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
        private static readonly IPerson_genDao person_genDao = new Person_genDao();

		/// <summary>
        /// Property Person_genDao
        /// </summary>
		public static IPerson_genDao Person_genDao
        {
            get
            {
                return person_genDao;
            }
        }
        private static readonly Isysdiagrams_genDao sysdiagrams_genDao = new sysdiagrams_genDao();

		/// <summary>
        /// Property sysdiagrams_genDao
        /// </summary>
		public static Isysdiagrams_genDao sysdiagrams_genDao
        {
            get
            {
                return sysdiagrams_genDao;
            }
        }
	}
}