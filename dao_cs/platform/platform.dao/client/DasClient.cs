using System;
using System.Data;
using System.Diagnostics;
using System.Net.Sockets;
using System.Text.RegularExpressions;
using platform.dao.log;
using platform.dao.param;
using System.Text;
using System.Collections.Generic;
using ProtoBuf;
using System.IO;

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
                catch (Exception ex)
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
        private void WriteRequest(param.Request request)
        {

            //Log the sql
            logger.Info(request.msg.name);

            byte[] payload = null;

            using (MemoryStream ms = new MemoryStream())
            {
                Serializer.Serialize<param.Request>(ms, request);

                payload = new byte[ms.Position];
                var fullB = ms.GetBuffer();
                Array.Copy(fullB, payload, payload.Length);
            }

            int protocolVersion = 1;

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

        }

        /// <summary>
        /// 从Das服务读出响应结果
        /// </summary>
        /// <returns></returns>
        private param.Response ReadResponse(Guid taskid)
        {
            param.Response response = null;
            bool success = false;
            int currentRetry = 0;
            while (!success && currentRetry < Consts.RetryTimesWhenError)
            {
                try
                {

                    int totalLength = (networkStream.ReadByte() << 24) |
                        (networkStream.ReadByte() << 16) |
                        (networkStream.ReadByte() << 8) |
                        (networkStream.ReadByte() << 0);

                    int protocolVersion = (networkStream.ReadByte() << 8) |
                        (networkStream.ReadByte() << 0);

                    byte[] header = new byte[totalLength - 2];

                    int taskidLen = networkStream.Read(header, 0, header.Length);

                    if (taskidLen != header.Length)
                        throw new Exception();

                    using (MemoryStream ms = new MemoryStream(header))
                    {
                        response = Serializer.Deserialize<param.Response>(ms);
                    }

                    success = true;

                }
                catch (Exception ex)
                {
                    logger.Error(ex.StackTrace);
                    Connect();
                }
                currentRetry++;
            }

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
            Stopwatch watch = new Stopwatch();
            //watch.Reset();
            

            Guid taskid = System.Guid.NewGuid();

            //MonitorSender.GetInstance().Send(taskid.ToString(), "taskBegin", DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);

            //MonitorData data = MonitorData.GetInstance(taskid.ToString());

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

            param.RequestMessage msg = new param.RequestMessage
            {
                stateType = param.StatementType.SQL,
                crud = param.CRUD.GET,
                flags = 1,
                master = true,
                name = sql
            };
            if (null != parameters && parameters.Length > 0)
            {
                foreach (IParameter p in parameters)
                {
                    msg.parameters.Add(new param.SqlParameters
                    {
                        dbType = (int)p.DbType,
                        direction = (int)p.Direction,
                        index = p.Index,
                        isNull = p.IsNullable,
                        name = p.Name,
                        value = p.GetFromObject(),
                        sensitive = p.IsSensitive
                    });
                }
            }

            param.Request request = new param.Request
            {
                msg = msg,
                id = taskid.ToString(),
                db = dbName,
                cred =
                    credential ?? string.Empty
            };

            watch.Start();

            WriteRequest(request);

            watch.Stop();

            MonitorData data = MonitorData.GetInstance(taskid.ToString());

            if (data != null)
            {
                data.EncodeRequestTime = watch.ElapsedMilliseconds;
            }

            param.Response response = ReadResponse(taskid);

            IDataReader reader = new DasDataReader()
            {
                NetworkStream = networkStream,
                Header = response.header
            };

           

            //MonitorSender.GetInstance().Send(taskid.ToString(), "encodeRequestTime", watch.ElapsedMilliseconds);

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
            throw new NotImplementedException();
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

            throw new NotImplementedException();
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
            throw new NotImplementedException();
        }

    }
}
