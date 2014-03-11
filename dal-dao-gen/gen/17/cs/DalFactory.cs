using com.ctrip.shard.Interface.IDao;
using com.ctrip.shard.Dao;

namespace com.ctrip.shard
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
        private static readonly IManyTypesGenDao manyTypesGenDao = new ManyTypesGenDao();

        private static readonly IDjangoAdminLogGenDao djangoAdminLogGenDao = new DjangoAdminLogGenDao();


        /// <summary>
        /// Property ManyTypesGenDao
        /// </summary>
        public static IManyTypesGenDao ManyTypesGenDao
        {
            get
            {
                return manyTypesGenDao;
            }
        }

        /// <summary>
        /// Property DjangoAdminLogGenDao
        /// </summary>
        public static IDjangoAdminLogGenDao DjangoAdminLogGenDao
        {
            get
            {
                return djangoAdminLogGenDao;
            }
        }

	}
}