using System.Collections;
using System.Data;
using System.Net.Sockets;
using System.Text.RegularExpressions;
using platform.dao.param;
using platform.dao.request;
using platform.dao.response;
using System.Collections.Generic;
using platform.dao.log;
using System.Diagnostics;
using System;

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
                sock.Disconnect(true);
                sock = null;
            }
            sock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            while (!sock.Connected)
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
            }
        }

        /// <summary>
        /// 向Das服务写入请求
        /// </summary>
        /// <param name="request"></param>
        private void WriteRequest(DefaultRequest request)
        {
            Stopwatch watch = new Stopwatch();

            watch.Start();
            byte[] payload = request.Pack2ByteArray();
            watch.Stop();

            logger.Info(string.Format("Serialization time: {0} Ticks(divide 10,000 to get milliseconds)", watch.ElapsedTicks));

            int protocolVersion = request.GetProtocolVersion();

            int totalLength = 2 + payload.Length;

            bool success = false;
            while (!success)
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
            }
        }

        /// <summary>
        /// 从Das服务读出响应结果
        /// </summary>
        /// <returns></returns>
        private DefaultResponse ReadResponse()
        {
            DefaultResponse response = null;
            bool success = false;

            while (!success)
            {
                try
                {
                    int totalLength = (networkStream.ReadByte() << 24) |
                        (networkStream.ReadByte() << 16) |
                        (networkStream.ReadByte() << 8) |
                        (networkStream.ReadByte() << 0);

                    int protocolVersion = (networkStream.ReadByte() << 8) |
                        (networkStream.ReadByte() << 0);

                    byte[] buffer = new byte[totalLength - 2];

                    int realCount = networkStream.Read(buffer, 0, buffer.Length);
                    if (realCount != buffer.Length)
                    {
                    }

                    Stopwatch watch = new Stopwatch();

                    watch.Start();
                    response = DefaultResponse.UnpackFromByteArray(buffer);
                    watch.Stop();

                    logger.Info(string.Format("Deserialization time: {0} Ticks(divide 10,000 to get milliseconds)", watch.ElapsedTicks));

                    success = true;
                }
                catch (Exception ex)
                {
                    logger.Error(ex.StackTrace);
                    Connect();
                }
            }

            return response;

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
                Taskid = System.Guid.NewGuid(),
                DbName = dbName,
                Credential = credential,
                Message = message
            };

            WriteRequest(request);

            DefaultResponse response = ReadResponse();

            IDataReader reader = new DasDataReader()
            {
                ResultSet = response.ResultSet
            };

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
                Taskid = System.Guid.NewGuid(),
                DbName = dbName,
                Credential = credential,
                Message = message
            };

            WriteRequest(request);

            DefaultResponse response = ReadResponse();

            return response.AffectRowCount;
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
                Taskid = System.Guid.NewGuid(),
                DbName = dbName,
                Credential = credential,
                Message = message
            };

            WriteRequest(request);

            DefaultResponse response = ReadResponse();

            IDataReader reader = new DasDataReader()
            {
                ResultSet = response.ResultSet
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
            if (null != parameters && parameters.Length > 0)
            {
                int i = 1;
                for (int j = 0; j < parameters.Length; j++)
                {
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
                Taskid = System.Guid.NewGuid(),
                DbName = dbName,
                Credential = credential,
                Message = message
            };

            WriteRequest(request);

            DefaultResponse response = ReadResponse();

            return response.AffectRowCount;
        }

    }
}
