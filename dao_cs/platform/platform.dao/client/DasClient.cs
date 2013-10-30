using System;
using System.Data;
using System.Diagnostics;
using System.Net.Sockets;
using System.Text.RegularExpressions;
using platform.dao.log;
using platform.dao.param;
using platform.dao.request;
using platform.dao.response;
using System.Text;

namespace platform.dao.client
{
    internal class DasClient : AbstractClient
    {

        private string dbName;
        private string credential;

        private static ILoggerAdapter logger = LogFactory.GetLogger(typeof(DasClient).Name);

        internal DasClient(string dbName, string credential)
        {
            this.dbName = dbName;
            this.credential = credential;
        }

        private static Socket sock;
        private static NetworkStream networkStream;
        private Regex paramRegex = new Regex(@"(?<paramName>[@|:]\w+)");

        static DasClient()
        {
            Connect();
        }

        private static void Connect()
        {
            if (sock != null)
            {
                try
                {
                    sock.Disconnect(true);
                }
                catch
                {
                }
                sock = null;
            }
            sock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            int currentRetry = 0;
            while (!sock.Connected && currentRetry < Consts.RetryTimesWhenError)
            {
                try
                {
                    //sock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                    sock.Connect(Consts.ServerIp, Consts.ServerPort);
                    networkStream = new NetworkStream(sock);
                }
                catch(Exception ex)
                {
                    logger.Error(ex.StackTrace);
                }
                currentRetry++;
            }
        }

        /// <summary>
        /// 向Das服务写入请求
        /// </summary>
        /// <param name="request"></param>
        private void WriteRequest(DefaultRequest request)
        {
            watch.Reset();
            watch.Start();
            //MonitorSender.GetInstance().Send(request.Taskid.ToString(), "beforeRequest", DateTime.Now.Ticks / 10000);

            //long beforeReqeust = DateTime.Now.Ticks / 10000;

            byte[] payload = request.Pack2ByteArray();
            //watch.Stop();

            //logger.Info(string.Format("Client encode request time: {0} MilliSeconds", watch.ElapsedTicks/10000.0));

            

            logger.Info(request.Message.Sql);

            int protocolVersion = request.GetProtocolVersion();

            int totalLength = 2 + payload.Length;

            bool success = false;
            int currentRetry = 0;
            while (!success && currentRetry < Consts.RetryTimesWhenError)
            {
                try
                {
                    //相当于向服务器端写入一个Int类型的数据,4字节
                    networkStream.WriteByte((byte)(totalLength >> 24));
                    networkStream.WriteByte((byte)(totalLength >> 16));
                    networkStream.WriteByte((byte)(totalLength >> 8));
                    networkStream.WriteByte((byte)(totalLength >> 0));

                    //相当于向服务器端写入一个Short类型的数据， 2字节
                    networkStream.WriteByte((byte)(protocolVersion >> 8));
                    networkStream.WriteByte((byte)(protocolVersion >> 0));

                    networkStream.Write(payload, 0, payload.Length);
                    success = true;
                   
                }
                catch (Exception ex)
                {
                    logger.Error(ex.StackTrace);
                    Connect();
                }
                currentRetry++;
            }
            //MonitorSender.GetInstance().Send(request.Taskid.ToString(), "endRequest", DateTime.Now.Ticks / 10000);
            watch.Stop();

            MonitorData data  = MonitorData.GetInstance(request.Taskid.ToString());

            data.EncodeRequestTime = watch.ElapsedMilliseconds;

        }

        /// <summary>
        /// 从Das服务读出响应结果
        /// </summary>
        /// <returns></returns>
        private DefaultResponse ReadResponse(Guid taskid)
        {
            watch.Reset();
            watch.Start();
            DefaultResponse response = null;
            bool success = false;
            int currentRetry = 0;
            while (!success && currentRetry < Consts.RetryTimesWhenError)
            {
                try
                {
                    //MonitorSender.GetInstance().Send(taskid.ToString(), "beforeResponse", DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);


                    int protocolVersion = (networkStream.ReadByte() << 8) |
                        (networkStream.ReadByte() << 0);

                    byte[] taskidBuffer = new byte[36];

                    int taskidLen = networkStream.Read(taskidBuffer, 0, taskidBuffer.Length);

                    if (taskidLen != taskidBuffer.Length)
                        throw new Exception();

                    int resultType = (networkStream.ReadByte() << 24) |
                        (networkStream.ReadByte() << 16) |
                        (networkStream.ReadByte() << 8) |
                        (networkStream.ReadByte() << 0);

                    response = new DefaultResponse();

                    response.Taskid = new Guid(Encoding.Default.GetString(taskidBuffer));

                    response.ResultType = (enums.OperationType)resultType;

                    //MonitorSender.GetInstance().Send(taskid.ToString(), "endResponse", DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);

                    //byte[] buffer = new byte[totalLength - 2];

                    //int realCount = networkStream.Read(buffer, 0, buffer.Length);
                    //if (realCount != buffer.Length)
                    //{
                    //}
                    //watch.Reset();
                    //watch.Start();
                    //response = DefaultResponse.UnpackFromByteArray(buffer);
                    //watch.Stop();


                    //logger.Info(string.Format("Client decode response time: {0} MilliSeconds", watch.ElapsedTicks / 10000.0));

                    success = true;
                    
                }
                catch (Exception ex)
                {
                    logger.Error(ex.StackTrace);
                    Connect();
                }
                currentRetry++;
            }

            watch.Stop();

            MonitorData data = MonitorData.GetInstance(taskid.ToString());

            data.DecodeResponseTime = watch.ElapsedMilliseconds;

            return response;

        }

        /// <summary>
        /// 从网络中读取影响的行数 
        /// </summary>
        /// <returns></returns>
        private int ReadAffectRowCount()
        {
            int rowCount = 0;
            bool success = false;
            int currentRetry = 0;
            while (!success && currentRetry < Consts.RetryTimesWhenError)
            {
                try
                {

                    rowCount = (networkStream.ReadByte() << 24) |
                       (networkStream.ReadByte() << 16) |
                       (networkStream.ReadByte() << 8) |
                       (networkStream.ReadByte() << 0);

                    success = true;
                    
                }
                catch (Exception ex)
                {
                    logger.Error(ex.StackTrace);
                    Connect();
                }
                currentRetry++;
            }

            return rowCount;
        }

        /// <summary>
        /// 将查询请求转发到DAS服务，并获取返回结果
        /// </summary>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public override IDataReader Fetch(string sql, params IParameter[] parameters)
        {
            //begin watch
            //Stopwatch watch = new Stopwatch();
            //watch.Reset();
            //watch.Start();

            Guid taskid = System.Guid.NewGuid();

            //MonitorSender.GetInstance().Send(taskid.ToString(), "taskBegin", DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);

            if (null != parameters && parameters.Length > 0)
            {
                MatchCollection mc = paramRegex.Matches(sql);
                int i = 1;
                foreach (Match ma in mc)
                {
                    for (int j = 0; j < parameters.Length; j++)
                    {
                        if (ma.Groups["paramName"].Value.Equals(parameters[j].Name))
                        {
                            if(parameters[j].Index == 0)
                                parameters[j].Index = i;
                            break;
                        }
                    }

                    i++;
                }
            }

            sql = Regex.Replace(sql, @"[@|:]\w+", "?");

            RequestMessage message = new RequestMessage()
            {
                StatementType = enums.StatementType.Sql,
                OperationType = enums.OperationType.Read,
                UseCache = false,
                Sql = sql,
                Parameters =  (null != parameters && parameters.Length > 0) ? parameters : new IParameter[0],
                Flags = 1
            };

            DefaultRequest request = new DefaultRequest()
            {
                Taskid = taskid,
                DbName = dbName,
                Credential = credential ?? string.Empty,
                Message = message
            };

            WriteRequest(request);

            DefaultResponse response = ReadResponse(taskid);

            IDataReader reader = new DasDataReader()
            {
                NetworkStream = networkStream//,
                //Taskid = taskid
            };

            //end watch
            //watch.Stop();

            //logger.Info(string.Format("Total time of fetch: {0} MilliSeconds", watch.ElapsedTicks / 10000.0));
            //MonitorSender.GetInstance().Send(taskid.ToString(), "taskEnd", DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);

            //MonitorData data  = MonitorData.GetInstance(taskid.ToString());

            //data.TotalTime = watch.ElapsedMilliseconds;

            return reader;

        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public override int Execute(string sql, params IParameter[] parameters)
        {
            Guid taskid = System.Guid.NewGuid();

            //MonitorSender.GetInstance().Send(taskid.ToString(), "taskBegin", DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);

            if (null != parameters && parameters.Length > 0)
            {
                MatchCollection mc = paramRegex.Matches(sql);
                int i = 1;
                foreach (Match ma in mc)
                {
                    for (int j = 0; j < parameters.Length; j++)
                    {
                        if (ma.Groups["paramName"].Value.Equals(parameters[j].Name))
                        {
                            if (parameters[j].Index == 0)
                                parameters[j].Index = i;
                            break;
                        }
                    }
                    i++;
                }
            }

            sql = Regex.Replace(sql, @"[@|:]\w+", "?");

            RequestMessage message = new RequestMessage()
            {
                StatementType = enums.StatementType.Sql,
                OperationType = enums.OperationType.Write,
                UseCache = false,
                Sql = sql,
                Parameters = (null != parameters && parameters.Length > 0) ? parameters : new IParameter[0],
                Flags = 1
            };

            DefaultRequest request = new DefaultRequest()
            {
                Taskid = taskid,
                DbName = dbName,
                Credential = credential ?? string.Empty,
                Message = message
            };

            WriteRequest(request);

            DefaultResponse response = ReadResponse(taskid);

            int rowCount = ReadAffectRowCount();

            //MonitorSender.GetInstance().Send(taskid.ToString(), "taskEnd", DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);

            return rowCount;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sp"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public override IDataReader FetchBySp(string sp, params IParameter[] parameters)
        {

            Guid taskid = System.Guid.NewGuid();

            RequestMessage message = new RequestMessage()
            {
                StatementType = enums.StatementType.StoredProcedure,
                OperationType = enums.OperationType.Read,
                UseCache = false,
                SpName = sp,
                Parameters = (null != parameters && parameters.Length > 0) ? parameters : new IParameter[0],
                Flags = 1
            };

            DefaultRequest request = new DefaultRequest()
            {
                Taskid = taskid,
                DbName = dbName,
                Credential = credential,
                Message = message
            };

            WriteRequest(request);

            DefaultResponse response = ReadResponse(taskid);

            IDataReader reader = new DasDataReader()
            {
                NetworkStream = networkStream
            };

            return reader;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sp"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public override int ExecuteSp(string sp, params IParameter[] parameters)
        {
            Guid taskid = System.Guid.NewGuid();

            if (null != parameters && parameters.Length > 0)
            {
                int i = 1;
                for (int j = 0; j < parameters.Length; j++)
                {
                    if (parameters[j].Index == 0)
                        parameters[j].Index = i;
                    i++;
                }
            }

            RequestMessage message = new RequestMessage()
            {
                StatementType = enums.StatementType.StoredProcedure,
                OperationType = enums.OperationType.Write,
                UseCache = false,
                SpName = sp,
                Parameters = (null != parameters && parameters.Length > 0) ? parameters : new IParameter[0],
                Flags = 1
            };

            DefaultRequest request = new DefaultRequest()
            {
                Taskid = taskid,
                DbName = dbName,
                Credential = credential,
                Message = message
            };

            WriteRequest(request);

            DefaultResponse response = ReadResponse(taskid);

            int rowCount = ReadAffectRowCount();

            return rowCount;
        }

    }
}
