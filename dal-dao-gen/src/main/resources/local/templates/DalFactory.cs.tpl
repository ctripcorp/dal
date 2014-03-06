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
        private static readonly I${host.getClassName()}Dao ${WordUtils.uncapitalize($host.getClassName())}Dao = new ${host.getClassName()}Dao();

#end
#foreach($host in $spHosts)
        private static readonly ${host.getClassName()}Dao ${WordUtils.uncapitalize($host.getClassName())}Dao = new ${host.getClassName()}Dao();

#end
#foreach($host in $freeSqlHosts)
        private static readonly ${host.getClassName()}Dao ${WordUtils.uncapitalize($host.getClassName())}Dao = new ${host.getClassName()}Dao();

#end

#foreach($host in $tableHosts)
        /// <summary>
        /// Property ${host.getClassName()}Dao
        /// </summary>
        public static I${host.getClassName()}Dao ${host.getClassName()}Dao
        {
            get
            {
                return ${WordUtils.uncapitalize($host.getClassName())}Dao;
            }
        }

#end
#foreach($host in $spHosts)
        /// <summary>
        /// Property ${host.getClassName()}Dao
        /// </summary>
        public static ${host.getClassName()}Dao ${host.getClassName()}Dao
        {
            get
            {
                return ${WordUtils.uncapitalize($host.getClassName())}Dao;
            }
        }

#end
#foreach($host in $freeSqlHosts)
        /// <summary>
        /// Property ${host.getClassName()}Dao
        /// </summary>
        public static ${host.getClassName()}Dao ${host.getClassName()}Dao
        {
            get
            {
                return ${WordUtils.uncapitalize($host.getClassName())}Dao;
            }
        }

#end
	}
}