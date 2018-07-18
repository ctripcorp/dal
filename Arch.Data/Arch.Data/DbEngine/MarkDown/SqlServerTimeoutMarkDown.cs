using Arch.Data.Common.Util;
using System;
using System.Collections.Generic;
using System.Data.Common;

namespace Arch.Data.DbEngine.MarkDown
{
    public class SqlServerTimeoutMarkDown : TimeoutMarkDown
    {
        private static readonly HashSet<Int32> SqlServerErrorCodes = new HashSet<Int32> { -2 };

        public override Boolean IsAbnormalExceptionTimeout(Exception ex)
        {
            if (ex == null)
                return false;
            var exception = ex as DbException;
            if (exception == null)
                return false;

            Int32 errorCode = ExceptionUtil.GetDbExceptionErrorCode(exception);
            String errorCodes = timeoutMarkDownBean.SqlServerErrorCodes;
            var result = String.IsNullOrEmpty(errorCodes) ? SqlServerErrorCodes : SplitUtil.SplitAsInt32(errorCodes);
            if (result.Contains(errorCode))
                return true;

            return false;
        }
    }
}
