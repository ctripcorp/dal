using System;
using System.Configuration;
using System.Security.Cryptography;
using System.Text;

namespace Arch.Data.Common.Util
{
    public partial class CommonUtil
    {
        /// <summary>
        ///  获得string对象的Hash值，每次耗时~5 微秒
        /// </summary>
        /// <param name="text"></param>
        /// <returns></returns>
        //  static  HashAlgorithm hash = new MD5CryptoServiceProvider();
        public static String GetHashCodeOfSQL(String text)
        {
            text = text.Trim();

            //1微妙
            using (HashAlgorithm hash = new MD5CryptoServiceProvider())
            {
                //0.8微秒
                Byte[] temp = Encoding.Default.GetBytes(text);

                //4微秒
                Byte[] md5Data = hash.ComputeHash(temp);

                //0.3微秒
                return Convert.ToBase64String(md5Data);
            }
        }

        static readonly String AppIdComment = "/*" + ConfigurationManager.AppSettings["AppID"] + "*/";

        /// <summary>
        /// 给SQL打上APPID的Tag
        /// </summary>
        /// <param name="sql"></param>
        /// <returns></returns>
        public static String GetTaggedAppIDSql(String sql)
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendLine(AppIdComment);
            sb.Append(sql);
            return sb.ToString();
        }

    }
}
