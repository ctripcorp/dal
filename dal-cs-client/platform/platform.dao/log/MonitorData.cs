using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.log
{
    public class MonitorData
    {

        private static object lock_obj = new object();

        private static MonitorData instance;

        public string Taskid { get; set; }

        private long encodeRequestTime;

        private long totalTime;

        private long decodeResposneTime;

        private int totalCount;

        private long totalDataBytes;

        public int TotalCount
        {
            get { return totalCount; }
            set { lock (lock_obj) { totalCount = value; } }
        }

        public long TotalDataBytes
        {
            get { return totalDataBytes; }
            set { lock (lock_obj) { totalDataBytes = value; } }
        }

        public long EncodeRequestTime { 
            get {return encodeRequestTime;}
            set { lock (lock_obj) { encodeRequestTime = value; } }
            }

        public long TotalTime
        {
            get { return totalTime; }
            set { lock (lock_obj) { totalTime = value; } }
        }

        public long DecodeResponseTime
        {
            get { return decodeResposneTime; }
            set { lock (lock_obj) { decodeResposneTime = value; } }
        }

        private MonitorData()
        {
        }

        private MonitorData(string taskid)
        {
            Taskid = taskid;
        }

        public static MonitorData GetInstance(string taskid = null)
        {
            lock (lock_obj)
            {
                if (string.IsNullOrEmpty(taskid))
                {
                    //if (instance != null)
                    //{
                        return instance;
                    //}
                    //throw new Exception("The monitor with this taskid not exists!");
                    //return null;
                }
                else
                {
                    if(instance == null  || !instance.Taskid.Equals(taskid))
                        instance = new MonitorData(taskid);
                    return instance;
                }
            }
        }


    }
}
