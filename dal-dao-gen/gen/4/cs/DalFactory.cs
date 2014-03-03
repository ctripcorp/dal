using hjhTest.Interface.IDao;
using hjhTest.Dao;

namespace hjhTest
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
        private static readonly IManyTypes_genDao manyTypes_genDao = new ManyTypes_genDao();

		/// <summary>
        /// Property ManyTypes_genDao
        /// </summary>
		public static IManyTypes_genDao ManyTypes_genDao
        {
            get
            {
                return manyTypes_genDao;
            }
        }
        private static readonly IMoneyTest_genDao moneyTest_genDao = new MoneyTest_genDao();

		/// <summary>
        /// Property MoneyTest_genDao
        /// </summary>
		public static IMoneyTest_genDao MoneyTest_genDao
        {
            get
            {
                return moneyTest_genDao;
            }
        }
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