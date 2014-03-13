using DAL.Interface.IDao;
using DAL.Dao;

namespace DAL
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