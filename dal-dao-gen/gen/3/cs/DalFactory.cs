using com.ctrip.platform.uat.Interface.IDao;
using com.ctrip.platform.uat.Dao;

namespace com.ctrip.platform.uat
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
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
	}
}