package com.ctrip.platform.dal.dao.logging;

public class CommonUtil {
    /// <summary>
    ///  获得string对象的Hash值，每次耗时~5 微秒
    /// </summary>
    /// <param name="text"></param>
    /// <returns></returns>
  //  static  HashAlgorithm hash = new MD5CryptoServiceProvider();
    public static String GetHashCode4SQLString(String text)
    {
    	/*
        //去头尾空字符
        text = text.Trim();

        //1微妙
        using (HashAlgorithm hash = new MD5CryptoServiceProvider())
        {
            //0.8微秒
            byte[] temp = System.Text.Encoding.Default.GetBytes(text);

            //4微秒
            byte[] md5data = hash.ComputeHash(temp);

            //0.3微秒
            return Convert.ToBase64String(md5data);
        }*/
    	return text;
    }
    /// <summary>
    /// 获得string对象的Hash值
    /// </summary>
    /// <param name="val"></param>
    /// <returns></returns>
    public static String GetHash(String val)
    {
    	/*
        byte[] buf = Encoding.UTF8.GetBytes(val);
        byte[] res = SHA256.Create().ComputeHash(buf);
        return BitConverter.ToString(res).Replace("-", "");*/
    	return val;
    }

    private static final String APPIDComment = "/* " + Logger.getAppId() + ", ";
    /// <summary>
    /// SQL打Tag
    /// </summary>
    /// <param name="sql"></param>
    /// <returns></returns>
    public static String GetTaggedSQLText(String sql)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(APPIDComment);
        try
        {
            sb.append(getMachinName());
        }
        catch (Exception e)
        {
            sb.append("UNKNOW");
        }
        sb.append("*/\n");
        sb.append(sql);
        return sb.toString();

    }

    private static String machine;
    public static String getMachinName() {
    	if(machine != null)
    		return machine;
        try
        {
            return machine = System.getenv("Host");
        }
        catch (Exception e)
        {
        	return machine = "UNKNOW";
        }
    }
    /**/
    /// <summary> 
    /// 对称加密解密的密钥 
    /// </summary> 
    private static String key = "dalctripcn";

    /**/
    /// <summary> 
    /// DES加密 
    /// </summary> 
    /// <param name="encryptString"></param> 
    /// <returns></returns> 
    public static String DesEncrypt(String encryptString)
    {
    	return encryptString;
//        if (encryptString == null) return null;
//        
//        byte[] keyBytes = Encoding.UTF8.GetBytes(key.Substring(0, 8));
//        byte[] keyIV = keyBytes;
//        byte[] inputByteArray = Encoding.UTF8.GetBytes(encryptString);
//        using (DESCryptoServiceProvider provider = new DESCryptoServiceProvider())
//        {
//            MemoryStream mStream = new MemoryStream();
//            CryptoStream cStream = new CryptoStream(mStream, provider.CreateEncryptor(keyBytes, keyIV), CryptoStreamMode.Write);
//            cStream.Write(inputByteArray, 0, inputByteArray.Length);
//            cStream.FlushFinalBlock();
//            return Convert.ToBase64String(mStream.ToArray());
//        }
    }

    /**/
    /// <summary> 
    /// DES解密 
    /// </summary> 
    /// <param name="decryptString"></param> 
    /// <returns></returns> 
    public static String DesDecrypt(String decryptString)
    {
    	return decryptString;
//        if (decryptString == null) return null;
//
//        byte[] keyBytes = Encoding.UTF8.GetBytes(key.Substring(0, 8));
//        byte[] keyIV = keyBytes;
//        byte[] inputByteArray = Convert.FromBase64String(decryptString);
//        using (DESCryptoServiceProvider provider = new DESCryptoServiceProvider())
//        {
//            MemoryStream mStream = new MemoryStream();
//            CryptoStream cStream = new CryptoStream(mStream, provider.CreateDecryptor(keyBytes, keyIV), CryptoStreamMode.Write);
//            cStream.Write(inputByteArray, 0, inputByteArray.Length);
//            cStream.FlushFinalBlock();
//            return Encoding.UTF8.GetString(mStream.ToArray());
//        }
    }

//
//    static int RSAMaxBlockSize = 117;
//    static String pk = "<RSAKeyValue><Modulus>mxqvz9XTkIo/N9Cb6F1YaxH7CJgp8Lv44QHx9BTOjN/GbdLBvi1sH5Bn7L8Yv6R7iyxyzTttnQUvAIHDbMCK+GGEK5xl5tZYrIjc3uayDF0zStJ3DxMpPKEt00SJ88JDUUrwz5Sc3L8Kx2ISSlXKxlXb/pHtXpEflZiZme5098k=</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>";
//    public static String RSAEncrypt(String encryptString)
//    {
//        using (RSACryptoServiceProvider crypt = new RSACryptoServiceProvider())
//        {
//            crypt.FromXmlString(pk);
//
//            byte[] inputByteArray = Encoding.UTF8.GetBytes(encryptString);
//
//            if (inputByteArray.Length < RSAMaxBlockSize)
//            {
//                byte[] temp = crypt.Encrypt(inputByteArray, false);
//                return Convert.ToBase64String(temp);
//            }
//            else
//            {
//                using (MemoryStream PlaiStream = new MemoryStream(inputByteArray))
//                using (MemoryStream CrypStream = new MemoryStream())
//                {
//                    Byte[] Buffer = new Byte[RSAMaxBlockSize];
//                    int BlockSize = PlaiStream.Read(Buffer, 0, RSAMaxBlockSize);
//
//                    while (BlockSize > 0)
//                    {
//                        Byte[] ToEncrypt = new Byte[BlockSize];
//                        Array.Copy(Buffer, 0, ToEncrypt, 0, BlockSize);
//
//                        Byte[] Cryptograph = crypt.Encrypt(ToEncrypt, false);
//                        CrypStream.Write(Cryptograph, 0, Cryptograph.Length);
//
//                        BlockSize = PlaiStream.Read(Buffer, 0, RSAMaxBlockSize);
//                    }
//
//                    return Convert.ToBase64String(CrypStream.ToArray(), Base64FormattingOptions.None);
//                }
//
//            }
//
//        }
//    }
}
