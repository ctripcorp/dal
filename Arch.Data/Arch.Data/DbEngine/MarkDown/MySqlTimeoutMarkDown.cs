using Arch.Data.Common.Util;
using System;
using System.Data.Common;

namespace Arch.Data.DbEngine.MarkDown
{
    public class MySqlTimeoutMarkDown : TimeoutMarkDown
    {
        public override Boolean IsAbnormalExceptionTimeout(Exception ex)
        {
            if (ex == null)
                return false;

            var exception = ex as DbException;
            if (exception == null)
                return false;

            Int32 errorCode = ExceptionUtil.GetDbExceptionErrorCode(exception);
            String errorCodes = timeoutMarkDownBean.MySqlErrorCodes;

            if (!String.IsNullOrEmpty(errorCodes))
            {
                var result = SplitUtil.SplitAsInt32(errorCodes);
                if (result.Contains(errorCode))
                    return true;
            }

            return ExceptionUtil.IsTimeoutException(ex);
        }
    }
}
