using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using System.Threading;
using System.Collections.Concurrent;

namespace platform.dao.log
{
    public class MonitorSender
    {

        private static MonitorSender instance = new MonitorSender();
        private static readonly long utcStartTime  = new DateTime(1970, 1, 1, 0, 0, 0, 0).Ticks/ 10000;
        private Thread sendThread;
        private ConcurrentQueue<string> taskQueue = new ConcurrentQueue<string>();

        private MonitorSender()
        {
            sendThread = new Thread(new ThreadStart(Run));
            sendThread.IsBackground = true;
            sendThread.Start();
        }

        public static MonitorSender GetInstance()
        {
            return instance;
        }

        //public void Send(MonitorData data)
        //{
        //    //this.Send(data.Taskid, "totalTime", data.TotalTime);
        //    this.Send(data.Taskid, "encodeRequestTime", data.EncodeRequestTime);
        //    this.Send(data.Taskid, "decodeResponseTime", data.DecodeResponseTime);
        //    this.Send(data.Taskid, "totalCount", data.TotalCount);
        //    this.Send(data.Taskid, "totalBytes", data.TotalDataBytes);
        //}

        public void Run()
        {
            while (true)
            {
                    //if (taskQueue.Count < 1)
                    //{
                    //    Thread.Sleep(100);
                    //    continue;
                    //}


                    string postData = null;

                    if (taskQueue.TryDequeue(out postData))
                    {
                        HttpWebRequest httpWReq =
                (HttpWebRequest)WebRequest.Create("http://localhost:8080/rest/console/monitor/timeCosts");

                        ASCIIEncoding encoding = new ASCIIEncoding();

                        byte[] data = encoding.GetBytes(postData);

                        httpWReq.Method = "POST";
                        httpWReq.ContentType = "application/x-www-form-urlencoded";
                        httpWReq.ContentLength = data.Length;
                        httpWReq.Proxy = null;

                        using (Stream stream = httpWReq.GetRequestStream())
                        {
                            stream.Write(data, 0, data.Length);
                        }

                        using (HttpWebResponse response = (HttpWebResponse)httpWReq.GetResponse())
                        {
                            using (StreamReader sr = new StreamReader(response.GetResponseStream()))
                            {
                                string responseString = sr.ReadToEnd();
                            }
                        }

                        
                    }
                    Thread.Sleep(100);
                }
            
        }

        public void Send(string id, string name, long milliSeconds)
        {
            string postData = string.Format("values={0}:{1}:{2}", id, name, milliSeconds);
            taskQueue.Enqueue(postData);
        }

    }
}
