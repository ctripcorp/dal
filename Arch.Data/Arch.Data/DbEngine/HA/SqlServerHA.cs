using Arch.Data.Common.Util;
using System;
using System.Collections.Generic;

namespace Arch.Data.DbEngine.HA
{
    class SqlServerHA : AbstractHA
    {
        private static readonly HashSet<Int32> SqlServerErrorCodes = new HashSet<Int32> { -2, 233, 701, 708, 802, 945, 1204, 1421 };

        public override HashSet<Int32> RetryFailOverErrorCodes
        {
            get
            {
                String errorCodes = haBean.SqlServerErrorCodes;
                var result = String.IsNullOrEmpty(errorCodes) ? SqlServerErrorCodes : SplitUtil.SplitAsInt32(errorCodes);
                return result;
            }
        }

    }
}
