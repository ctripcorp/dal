using com.ctrip.dal.test.test4.Interface.IDao;
using com.ctrip.dal.test.test4.Dao;

namespace com.ctrip.dal.test.test4
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
        private static readonly ITestDao testDao = new TestDao();

        private static readonly IAccountcheckDao accountcheckDao = new AccountcheckDao();

        private static readonly IAbacusCancelPNRLogGenDao abacusCancelPNRLogGenDao = new AbacusCancelPNRLogGenDao();

        private static readonly ITest1Dao test1Dao = new Test1Dao();

        private static readonly IAbacusGetPnrLogGenDao abacusGetPnrLogGenDao = new AbacusGetPnrLogGenDao();

        private static readonly IAbacusParaGenDao abacusParaGenDao = new AbacusParaGenDao();

        private static readonly IAccountBalanceLetterDao accountBalanceLetterDao = new AccountBalanceLetterDao();

        private static readonly IAbacusCreateSegmentLogGenDao abacusCreateSegmentLogGenDao = new AbacusCreateSegmentLogGenDao();

        private static readonly IAbacusAirBookLogGenDao abacusAirBookLogGenDao = new AbacusAirBookLogGenDao();

        private static readonly ITest2Dao test2Dao = new Test2Dao();

        private static readonly IAbacusModifyInfoLogGenDao abacusModifyInfoLogGenDao = new AbacusModifyInfoLogGenDao();

        private static readonly IAbacusGetTaxLogGenDao abacusGetTaxLogGenDao = new AbacusGetTaxLogGenDao();

        private static readonly IFltOrdersTmpDao fltOrdersTmpDao = new FltOrdersTmpDao();

        private static readonly TestSearchTableDao testSearchTableDao = new TestSearchTableDao();

        private static readonly GetretDao getretDao = new GetretDao();


        /// <summary>
        /// Property TestDao
        /// </summary>
        public static ITestDao TestDao
        {
            get
            {
                return testDao;
            }
        }

        /// <summary>
        /// Property AccountcheckDao
        /// </summary>
        public static IAccountcheckDao AccountcheckDao
        {
            get
            {
                return accountcheckDao;
            }
        }

        /// <summary>
        /// Property AbacusCancelPNRLogGenDao
        /// </summary>
        public static IAbacusCancelPNRLogGenDao AbacusCancelPNRLogGenDao
        {
            get
            {
                return abacusCancelPNRLogGenDao;
            }
        }

        /// <summary>
        /// Property Test1Dao
        /// </summary>
        public static ITest1Dao Test1Dao
        {
            get
            {
                return test1Dao;
            }
        }

        /// <summary>
        /// Property AbacusGetPnrLogGenDao
        /// </summary>
        public static IAbacusGetPnrLogGenDao AbacusGetPnrLogGenDao
        {
            get
            {
                return abacusGetPnrLogGenDao;
            }
        }

        /// <summary>
        /// Property AbacusParaGenDao
        /// </summary>
        public static IAbacusParaGenDao AbacusParaGenDao
        {
            get
            {
                return abacusParaGenDao;
            }
        }

        /// <summary>
        /// Property AccountBalanceLetterDao
        /// </summary>
        public static IAccountBalanceLetterDao AccountBalanceLetterDao
        {
            get
            {
                return accountBalanceLetterDao;
            }
        }

        /// <summary>
        /// Property AbacusCreateSegmentLogGenDao
        /// </summary>
        public static IAbacusCreateSegmentLogGenDao AbacusCreateSegmentLogGenDao
        {
            get
            {
                return abacusCreateSegmentLogGenDao;
            }
        }

        /// <summary>
        /// Property AbacusAirBookLogGenDao
        /// </summary>
        public static IAbacusAirBookLogGenDao AbacusAirBookLogGenDao
        {
            get
            {
                return abacusAirBookLogGenDao;
            }
        }

        /// <summary>
        /// Property Test2Dao
        /// </summary>
        public static ITest2Dao Test2Dao
        {
            get
            {
                return test2Dao;
            }
        }

        /// <summary>
        /// Property AbacusModifyInfoLogGenDao
        /// </summary>
        public static IAbacusModifyInfoLogGenDao AbacusModifyInfoLogGenDao
        {
            get
            {
                return abacusModifyInfoLogGenDao;
            }
        }

        /// <summary>
        /// Property AbacusGetTaxLogGenDao
        /// </summary>
        public static IAbacusGetTaxLogGenDao AbacusGetTaxLogGenDao
        {
            get
            {
                return abacusGetTaxLogGenDao;
            }
        }

        /// <summary>
        /// Property FltOrdersTmpDao
        /// </summary>
        public static IFltOrdersTmpDao FltOrdersTmpDao
        {
            get
            {
                return fltOrdersTmpDao;
            }
        }

        /// <summary>
        /// Property TestSearchTableDao
        /// </summary>
        public static TestSearchTableDao TestSearchTableDao
        {
            get
            {
                return testSearchTableDao;
            }
        }

        /// <summary>
        /// Property GetretDao
        /// </summary>
        public static GetretDao GetretDao
        {
            get
            {
                return getretDao;
            }
        }

	}
}