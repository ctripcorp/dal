using com.ctrip.platform.toolsIDao;

namespace com.ctrip.platform.tools
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
        private static readonly IPrizeInfoDao prizeInfoDao = new PrizeInfoDao();

		/// <summary>
        /// Property PrizeInfoDao
        /// </summary>
		public static IPrizeInfoDao PrizeInfoDao
        {
            get
            {
                return prizeInfoDao;
            }
        }
	}
}