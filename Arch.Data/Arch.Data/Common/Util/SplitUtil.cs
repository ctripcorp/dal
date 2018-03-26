using System;
using System.Collections.Generic;

namespace Arch.Data.Common.Util
{
    public class SplitUtil
    {
        static readonly Char[] DalValidSplitter = { ',', '，', ';', '；' };

        /// <summary>
        /// 将执行的字符串分割后存入HashSet
        /// </summary>
        /// <param name="value"></param>
        /// <returns></returns>
        public static HashSet<Int32> SplitAsInt32(String value)
        {
            var array = value.Split(DalValidSplitter);
            var result = new HashSet<Int32>();

            if (array.Length > 0)
            {
                foreach (var item in array)
                {
                    Int32 errorCode;
                    if (Int32.TryParse(item.Trim(), out errorCode))
                        result.Add(errorCode);
                }
            }

            return result;
        }

        public static HashSet<String> SplitAsStringIgnoreEmpty(String strToSplit)
        {
            var result = new HashSet<String>();

            foreach (var str in strToSplit.Split(DalValidSplitter, StringSplitOptions.RemoveEmptyEntries))
            {
                result.Add(str);
            }

            return result;
        }

    }
}