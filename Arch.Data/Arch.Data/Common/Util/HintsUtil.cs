using System;
using System.Collections;
using System.Collections.Generic;

namespace Arch.Data.Common.Util
{
    class HintsUtil
    {
        public static IDictionary CloneHints(IDictionary hints)
        {
            var temp = hints as Dictionary<String, Object>;
            var dict = new Dictionary<String, Object>();

            if (temp == null)
                return dict;

            foreach (var item in temp)
            {
                dict.Add(item.Key, item.Value);
            }

            return dict;
        }
    }
}