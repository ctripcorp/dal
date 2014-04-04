using com.ctrip.dal.test.test2.Interface.IDao;
using com.ctrip.dal.test.test2.Dao;

namespace com.ctrip.dal.test.test2
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
        private static readonly IPersonGenDao personGenDao = new PersonGenDao();


        /// <summary>
        /// Property PersonGenDao
        /// </summary>
        public static IPersonGenDao PersonGenDao
        {
            get
            {
                return personGenDao;
            }
        }

	}
}