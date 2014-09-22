using ${namespace}.Interface.IDao;
using ${namespace}.Dao;

namespace ${namespace}
{
	/// <summary>
    /// DALFactory
    /// </summary>
	public partial class DALFactory
	{
#foreach($host in $tableHosts)
        private static readonly I${host}Dao ${WordUtils.uncapitalize($host)}Dao = new ${host}Dao();

#end
#foreach($host in $spHosts)
        private static readonly ${host}Dao ${WordUtils.uncapitalize($host)}Dao = new ${host}Dao();

#end
#foreach($host in $freeSqlHosts)
        private static readonly ${host}Dao ${WordUtils.uncapitalize($host)}Dao = new ${host}Dao();

#end

#foreach($host in $tableHosts)
        /// <summary>
        /// Property ${host}Dao
        /// </summary>
        public static I${host}Dao ${host}Dao
        {
            get
            {
                return ${WordUtils.uncapitalize($host)}Dao;
            }
        }

#end
#foreach($host in $spHosts)
        /// <summary>
        /// Property ${host}Dao
        /// </summary>
        public static ${host}Dao ${host}Dao
        {
            get
            {
                return ${WordUtils.uncapitalize($host)}Dao;
            }
        }

#end
#foreach($host in $freeSqlHosts)
        /// <summary>
        /// Property ${host}Dao
        /// </summary>
        public static ${host}Dao ${host}Dao
        {
            get
            {
                return ${WordUtils.uncapitalize($host)}Dao;
            }
        }

#end
	}
}