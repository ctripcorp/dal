using DAL.Interface.IDao;
using DAL.Dao;

namespace DAL
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
        private static readonly IMoneyTestGenDao moneyTestGenDao = new MoneyTestGenDao();

        private static readonly IPersonGenDao personGenDao = new PersonGenDao();

        private static readonly ISDPVersionGenDao sDPVersionGenDao = new SDPVersionGenDao();

        private static readonly ISDPSearchDataGenDao sDPSearchDataGenDao = new SDPSearchDataGenDao();

        private static readonly IPersonGenDao personGenDao = new PersonGenDao();

        private static readonly IPersonGenDao personGenDao = new PersonGenDao();

        private static readonly PerTestDao perTestDao = new PerTestDao();

        private static readonly TestDao testDao = new TestDao();

        private static readonly JustQueryDao justQueryDao = new JustQueryDao();


        /// <summary>
        /// Property MoneyTestGenDao
        /// </summary>
        public static IMoneyTestGenDao MoneyTestGenDao
        {
            get
            {
                return moneyTestGenDao;
            }
        }

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

        /// <summary>
        /// Property SDPVersionGenDao
        /// </summary>
        public static ISDPVersionGenDao SDPVersionGenDao
        {
            get
            {
                return sDPVersionGenDao;
            }
        }

        /// <summary>
        /// Property SDPSearchDataGenDao
        /// </summary>
        public static ISDPSearchDataGenDao SDPSearchDataGenDao
        {
            get
            {
                return sDPSearchDataGenDao;
            }
        }

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

        /// <summary>
        /// Property PerTestDao
        /// </summary>
        public static PerTestDao PerTestDao
        {
            get
            {
                return perTestDao;
            }
        }

        /// <summary>
        /// Property TestDao
        /// </summary>
        public static TestDao TestDao
        {
            get
            {
                return testDao;
            }
        }

        /// <summary>
        /// Property JustQueryDao
        /// </summary>
        public static JustQueryDao JustQueryDao
        {
            get
            {
                return justQueryDao;
            }
        }

	}
}