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
	}
}