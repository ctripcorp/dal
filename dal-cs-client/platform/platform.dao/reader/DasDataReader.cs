﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using System.Net.Sockets;
using platform.dao.param;
using platform.dao.exception;
using platform.dao.utils;
using System.Diagnostics;
using platform.dao.log;
using platform.dao.enums;
using platform.dao.client;
using System.Threading;
using System.IO;
using ProtoBuf;

namespace platform.dao.sql
{
    public class DasDataReader : IDataReader
    {
        private static readonly DateTime utcStartTime;

        static DasDataReader()
        {
            utcStartTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
        }

        private int cursor = 0;
        private bool readFinished = false;
        private param.Row current;
        private List<param.Row> ResultSet;
        public string CurrentId { get; set; }
        private long totalBytes = 0L;
        private int totalCount = 0;
        private Stopwatch watch;

        public List<param.ResponseHeader> Header { get; set; }

        public PooledSocket Sock { get; set; }

        public void Close()
        {
            this.Dispose();
        }

        public int Depth
        {
            get { return 1; }
        }

        public DataTable GetSchemaTable()
        {
            throw new NotImplementedException();
        }

        public bool IsClosed
        {
            get { return null == current; }
        }

        public bool NextResult()
        {
            return Read();
        }

        public bool Read()
        {

            if (null == this.Sock)
                return false;

            if (watch == null)
            {
                watch = new Stopwatch();
                watch.Start();
            }

            //如果没有读取完成，且剩余的不足100个，则再次读取
            if (!readFinished && ((ResultSet == null) || (ResultSet.Count - cursor < 100)))
            {
                

                int blockSize = Sock.ReadInt();

                byte[] payload = Sock.ReadBytes(blockSize);
                 
                param.InnerResultSet resultSet = null;
                using (MemoryStream ms = new MemoryStream(payload))
                {
                    resultSet = Serializer.Deserialize<param.InnerResultSet>(ms);
                }

                if (resultSet.last)
                    Sock.Dispose();

                List<param.Row> results = new List<param.Row>();

                if (ResultSet != null && ResultSet.Count > 0)
                {
                    results.AddRange(ResultSet.GetRange(cursor, ResultSet.Count - cursor));
                }

                readFinished = resultSet.last;
                results.AddRange(resultSet.rows);

                totalBytes += payload.Length;
                totalCount += resultSet.rows.Count;

                payload = null;

                if (ResultSet != null)
                {
                    ResultSet.Clear();
                }
                ResultSet = results;
                cursor = 0;
            }

            if (readFinished)
            {
                watch.Stop();
                MonitorSender.GetInstance().Send(CurrentId, "totalBytes", totalBytes);
                MonitorSender.GetInstance().Send(CurrentId, "totalCount", totalCount);
                MonitorSender.GetInstance().Send(CurrentId, "decodeResponseTime", watch.ElapsedMilliseconds);
            }

            var result = cursor < ResultSet.Count;
            if (result)
            {
                current = ResultSet[cursor];
                cursor++;
            }
            return result;
        }

        public int RecordsAffected
        {
            get { throw new NotImplementedException(); }
        }

        public void Dispose()
        {
            //if(null != this.current)
            //    this.current.Clear();
            if (null != this.ResultSet)
                this.ResultSet.Clear();
            this.current = null;
            this.ResultSet = null;
        }

        /// <summary>
        /// 获取当前行中的列数
        /// </summary>
        public int FieldCount
        {
            //get { return current.Count; }
            get { return current.columns.Count; }
        }

        public bool GetBoolean(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return current[i].Value.AsBoolean();
        }

        public byte GetByte(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return current[i].Value.AsByte();
        }

        public long GetBytes(int i, long fieldOffset, byte[] buffer, int bufferoffset, int length)
        {
            throw new NotImplementedException();
        }

        public char GetChar(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return (char)current[i].Value.AsUInt16();
        }

        public long GetChars(int i, long fieldoffset, char[] buffer, int bufferoffset, int length)
        {
            throw new NotImplementedException();
        }

        public IDataReader GetData(int i)
        {
            throw new NotImplementedException();
        }

        public string GetDataTypeName(int i)
        {
            throw new NotImplementedException();
        }

        public DateTime GetDateTime(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return utcStartTime.AddMilliseconds(current[i].Value.AsUInt64());
        }

        public decimal GetDecimal(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return decimal.Parse(current[i].Value.AsString());
        }

        public double GetDouble(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return current[i].Value.AsDouble();
        }

        public Type GetFieldType(int i)
        {
            throw new NotImplementedException();
            //return TypeConverter.ResolveDbType(current[i].DbType);
        }

        public float GetFloat(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return current[i].Value.AsSingle();
        }

        public Guid GetGuid(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return new Guid(current[i].Value.AsBinary());
        }

        public short GetInt16(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return current[i].Value.AsInt16();
        }

        public int GetInt32(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return current[i].Value.AsInt32();
        }

        public long GetInt64(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return current[i].Value.AsInt64();
        }

        public string GetName(int i)
        {
            throw new NotImplementedException();
            //foreach (var p in current)
            //{
            //    if (p.Index.Equals(i))
            //    {
            //        return p.Name;
            //    }
            //}
            //return null;
        }

        public int GetOrdinal(string name)
        {
            throw new NotImplementedException();
            //foreach (var p in current)
            //{
            //    if (p.Name.Equals(name))
            //    {
            //        return p.Index;
            //    }
            //}
            //return -1;
        }

        public string GetString(int i)
        {
            throw new NotImplementedException();
            //if (i > FieldCount)
            //    throw new DAOException("Index out of bound!");

            //return current[i].Value.AsString();
        }

        public object GetValue(int i)
        {
            throw new NotImplementedException();
            //return current[i].Value.ToObject();
        }

        public int GetValues(object[] values)
        {
            throw new NotImplementedException();
        }

        public bool IsDBNull(int i)
        {
            throw new NotImplementedException();
            //return current[i].Value.IsNil;
        }

        public object this[string name]
        {
            get
            {
                object result = null;

                if (Header == null || current == null)
                    return null;

                int lableIndex = -1;

                //获取当前列名的索引位置
                for (int i = 0; i < Header.Count; i++)
                {
                    var p = Header[i];
                    if (p.name.Equals(name))
                    {
                        lableIndex = i;
                        break;
                    }
                }

                int currentType = Header[lableIndex].type;

                param.AvailableType currentValue = current.columns[lableIndex];

                if (currentValue.current < 0)
                    return null;

                switch (currentType)
                {
                    case Types.INTEGER:
                        result = currentValue.int32_arg;
                        break;
                    case Types.VARCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.LONGNVARCHAR:
                        result = currentValue.string_arg;
                        break;
                    case Types.DECIMAL:
                        result = decimal.Parse(currentValue.string_arg);
                        break;
                    case Types.TIMESTAMP:
                        result = utcStartTime.AddMilliseconds(currentValue.int64_arg);
                        break;
                    default:
                        break;
                }

                //IParameter param = null;
                //foreach (var p in current)
                //{
                //    if (p.Name.Equals(name))
                //    {
                //        param = p;
                //        break;
                //    }
                //}

                //if (param != null)
                //{
                //    if (param.DbType == DbType.Decimal)
                //        result = decimal.Parse(param.Value.AsString());
                //    else if (param.DbType == DbType.StringFixedLength)
                //        result = (char)param.Value.AsUInt16();
                //    else if (param.DbType == DbType.Guid)
                //        result = new Guid(param.Value.AsBinary());
                //    else if (param.DbType == DbType.DateTime)
                //        result = utcStartTime.AddMilliseconds(param.Value.AsUInt64());
                //    else
                //        result = param.Value.ToObject();
                //}

                return result;
            }
        }

        public object this[int i]
        {
            get
            {
                object result = null;

                if (Header == null || current == null)
                    return null;

                int lableIndex = i;

                int currentType = Header[lableIndex].type;

                param.AvailableType currentValue = current.columns[lableIndex];

                if (currentValue.current < 0)
                    return null;

                switch (currentType)
                {
                    case Types.INTEGER:
                        result = currentValue.int32_arg;
                        break;
                    case Types.VARCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.LONGNVARCHAR:
                        result = currentValue.string_arg;
                        break;
                    case Types.DECIMAL:
                        result = decimal.Parse(currentValue.string_arg);
                        break;
                    case Types.TIMESTAMP:
                        result = utcStartTime.AddMilliseconds(currentValue.int64_arg);
                        break;
                    default:
                        break;
                }


                //IParameter param = current[i];

                //if (param != null)
                //{
                //    if (param.DbType == DbType.Decimal)
                //        result = decimal.Parse(param.Value.AsString());
                //    else if (param.DbType == DbType.StringFixedLength)
                //        result = (char)param.Value.AsUInt16();
                //    else if (param.DbType == DbType.Guid)
                //        result = new Guid(param.Value.AsBinary());
                //    else if (param.DbType == DbType.DateTime)
                //        result = utcStartTime.AddMilliseconds(param.Value.AsUInt64());
                //    else
                //        result = param.Value.ToObject();
                //}

                return result;
            }
        }



    }
}