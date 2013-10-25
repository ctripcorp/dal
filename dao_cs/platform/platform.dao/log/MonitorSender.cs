using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

namespace platform.dao.log
{
    public class MonitorSender
    {

        private static MonitorSender instance = new MonitorSender();

        private MonitorSender()
        {
        }

        public static MonitorSender GetInstance()
        {
            return instance;
        }

        public void Send(string name, long milliSeconds)
        {
            HttpWebRequest httpWReq =
    (HttpWebRequest)WebRequest.Create("http://localhost:8080/console/dal/das/monitor/timeCosts");

            ASCIIEncoding encoding = new ASCIIEncoding();
            string postData = "username=user";
            postData += "&password=pass";
            byte[] data = encoding.GetBytes(postData);

            httpWReq.Method = "POST";
            httpWReq.ContentType = "application/x-www-form-urlencoded";
            httpWReq.ContentLength = data.Length;
            httpWReq.Proxy = null;

            using (Stream stream = httpWReq.GetRequestStream())
            {
                stream.Write(data, 0, data.Length);
            }

            HttpWebResponse response = (HttpWebResponse)httpWReq.GetResponse();

            string responseString = new StreamReader(response.GetResponseStream()).ReadToEnd();
        }

    }
}
