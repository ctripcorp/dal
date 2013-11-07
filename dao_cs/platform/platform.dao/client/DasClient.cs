using System;
using System.Data;
using System.Diagnostics;
using System.Net.Sockets;
using System.Text.RegularExpressions;
using platform.dao.log;
using platform.dao.param;
using platform.dao.sql;
using System.Text;
using System.Collections.Generic;
using ProtoBuf;
using System.IO;

namespace platform.dao.client
{
    internal class DasClient : IClient
    {
        private static ILogAdapter logger = LogFactory.GetLogger(typeof(DasClient).Name);

        public string PhysicDbName { get; set; }
        public string CredentialID { get; set; }
        public int ServicePort { get; set; }

        private SocketPool socketPool;

        private Regex paramRegex = new Regex(@"(?<paramName>[@|:]\w+)");

        public void Init()
        {
            socketPool = new SocketPool("127.0.0.1", ServicePort);
        }

        /// <summary>
        /// 向Das服务写入请求
        /// </summary>
        /// <param name="request"></param>
        private PooledSocket WriteRequest(param.Request request)
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

            return socketPool.Execute<PooledSocket>(delegate(PooledSocket socket)
            {
                short protocolVersion = 1;

                int totalLength = 2 + payload.Length;

                socket.WriteInt(totalLength);

                socket.WriteShort(protocolVersion);

                socket.WriteBytes(payload);

                return socket;
            });

        }

        /// <summary>
        /// 从Das服务读出响应结果
        /// </summary>
        /// <returns></returns>
        private param.Response ReadResponse(PooledSocket sock)
        {
            return socketPool.Execute<param.Response>(delegate(PooledSocket socket)
            {
                param.Response response = null;

                int totalLength = socket.ReadInt();
                short protocolVersion = socket.ReadShort();

                byte[] payload = socket.ReadBytes(totalLength - 2);

                using (MemoryStream ms = new MemoryStream(payload))
                {
                    response = Serializer.Deserialize<param.Response>(ms);
                }

                return response;
            }, sock);

        }

        /// <summary>
        /// 将查询请求转发到DAS服务，并获取返回结果
        /// </summary>
        /// <param name="sql"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public IDataReader Fetch(string sql, params IParameter[] parameters)
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
                db = PhysicDbName,
                cred = CredentialID ?? string.Empty
            };

            watch.Start();

            PooledSocket sock = WriteRequest(request);

            watch.Stop();

            MonitorData data = MonitorData.GetInstance(taskid.ToString());

            if (data != null)
            {
                data.EncodeRequestTime = watch.ElapsedMilliseconds;
            }

            param.Response response = ReadResponse(sock);

            if (null == response)
                return null;

            IDataReader reader = new DasDataReader()
            {
                Sock = sock,
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
        public int Execute(string sql, params IParameter[] parameters)
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
        public IDataReader FetchBySp(string sp, params IParameter[] parameters)
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
        public int ExecuteSp(string sp, params IParameter[] parameters)
        {
            throw new NotImplementedException();
        }

    }
}
