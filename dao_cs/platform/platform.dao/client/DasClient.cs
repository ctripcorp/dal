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
    /// <summary>
    /// 细节TO BE DONE：
    /// 1. 支持IN类型的参数
    /// 2. 支持批量操作
    /// 3. 完善生成工具
    /// 4. HA
    /// 5. 事务支持
    /// 6. 读写分离与Sharding
    /// </summary>
    internal class DasClient : IClient
    {
        private static ILogAdapter logger = LogFactory.GetLogger(typeof(DasClient).Name);

        public string PhysicDbName { get; set; }
        public string CredentialID { get; set; }
        public int ServicePort { get; set; }

        private SocketPool socketPool;

        private Regex paramRegex = new Regex(@"(?<paramName>[@|:]\w+)");
        private Regex inRegex = new Regex(@"\sIN\s(?<paramName>[@|:]\w+)", RegexOptions.IgnoreCase);

        public void Init()
        {
            socketPool = new SocketPool("172.16.155.184", ServicePort);
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

                MonitorData data = MonitorData.GetInstance();

                if (data != null)
                {
                    data.TotalDataBytes += 4 + 2 + payload.Length;
                }

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
        public IDataReader Fetch(string sql, IParameter[] parameters,bool masterOnly = true)
        {
            //begin watch
            Stopwatch watch = new Stopwatch();
            //watch.Reset();
            


            Guid taskid = System.Guid.NewGuid();

            //MonitorSender.GetInstance().Send(taskid.ToString(), "taskBegin", DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);

            //MonitorData data = MonitorData.GetInstance(taskid.ToString());

            if (null != parameters && parameters.Length > 0)
            {
                //MatchCollection inMatches = inRegex.Matches(sql);

                //foreach (Match ma in inMatches)
                //{
                //    for (int j = 0; j < parameters.Length; j++)
                //    {
                //        //找到IN对应的参数名
                //        if (ma.Groups["paramName"].Value.Equals(parameters[j].Name))
                //        {
                            
                //            break;
                //        }
                //    }
                //}


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
        public int Execute(string sql, IParameter[] parameters, bool masterOnly = true)
        {
            Stopwatch watch = new Stopwatch();

            Guid taskid = System.Guid.NewGuid();

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
                crud = param.CRUD.CUD,
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
                return -1;

            return response.affectRows;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sp"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public IDataReader FetchBySp(string sp, IParameter[] parameters, bool masterOnly = true)
        {
            Stopwatch watch = new Stopwatch();

            Guid taskid = System.Guid.NewGuid();

            if (null != parameters && parameters.Length > 0)
            {
                int i = 1;

                for (int j = 0; j < parameters.Length; j++)
                {
                    if (parameters[j].Index == 0)
                    {
                        parameters[j].Index = i;
                        i++;
                    }
                    else
                    {
                        i = parameters[j].Index + 1;
                    }
                }
            }

            param.RequestMessage msg = new param.RequestMessage
            {
                stateType = param.StatementType.SP,
                crud = param.CRUD.GET,
                flags = 1,
                master = true,
                name = sp
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
        /// <param name="sp"></param>
        /// <param name="parameters"></param>
        /// <param name="extraOptions"></param>
        /// <returns></returns>
        public int ExecuteSp(string sp, IParameter[] parameters, bool masterOnly = true)
        {
            Stopwatch watch = new Stopwatch();

            Guid taskid = System.Guid.NewGuid();

            if (null != parameters && parameters.Length > 0)
            {
                int i = 1;

                for (int j = 0; j < parameters.Length; j++)
                {
                    if (parameters[j].Index == 0)
                    {
                        parameters[j].Index = i;
                        i++;
                    }
                    else
                    {
                        i = parameters[j].Index + 1;
                    }
                }
            }

            param.RequestMessage msg = new param.RequestMessage
            {
                stateType = param.StatementType.SP,
                crud = param.CRUD.CUD,
                flags = 1,
                master = true,
                name = sp
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
                return -1;

            return response.affectRows;
        }

    }
}
