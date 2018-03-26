using Arch.Data.Orm.sql;
using System;
using System.Collections.Concurrent;
using System.Data.Common;
using System.Diagnostics;

namespace Arch.Data.Common.Util
{
    public class ExceptionUtil
    {
        static ConcurrentDictionary<Type, PropertyBridge> Properties = new ConcurrentDictionary<Type, PropertyBridge>();

        /// <summary>
        /// 根据DbException，获取相应的错误码
        /// </summary>
        /// <param name="ex"></param>
        /// <returns></returns>
        public static Int32 GetDbExceptionErrorCode(DbException ex)
        {
            try
            {
                var dbExceptionBridge = Properties.GetOrAdd(ex.GetType(), type =>
                {
                    var field = type.GetProperty("Number");
                    return new PropertyBridge(field);
                });

                var number = dbExceptionBridge.Read(ex);
                if (null != number) return Convert.ToInt32(number);
            }
            catch (Exception exception)
            {
                ThrowInDebugMode(exception);
            }

            return ex.ErrorCode;
        }

        [Conditional("DEBUG")]
        public static void ThrowInDebugMode(Exception ex)
        {
            throw ex;
        }

        /// <summary>
        /// 判断一个异常的堆栈中是否有超时异常
        /// </summary>
        /// <param name="ex"></param>
        /// <returns></returns>
        public static Boolean IsTimeoutException(Exception ex)
        {
            if (null == ex)
                return false;
            if (ex is TimeoutException)
                return true;
            var currentEx = ex.InnerException;

            while (currentEx != null)
            {
                if (currentEx is TimeoutException)
                    return true;
                currentEx = currentEx.InnerException;
            }
            return false;
        }

    }
}
