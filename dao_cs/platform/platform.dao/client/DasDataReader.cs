using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using System.Net.Sockets;
using platform.dao.param;
using platform.dao.exception;
using platform.dao.utils;
using platform.dao.response;
using System.Diagnostics;
using platform.dao.log;
using platform.dao.enums;
using MsgPack;
using MsgPack.Serialization;

namespace platform.dao.client
{
    public class DasDataReader : IDataReader
    {
        private static readonly DateTime utcStartTime;

        //private static ParameterSerializer serializer;

        private static ResultSetHeaderSerializer headerSerializer;

        static DasDataReader()
        {
            utcStartTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
            //serializer = new ParameterSerializer();
            headerSerializer = new ResultSetHeaderSerializer();
        }

        private int cursor = 0;

        private bool readFinished = false;

        private int rowCursor = 0;

        //private List<IParameter> current;
        //private byte[][] current;
        private List<MessagePackObject> current;

        //private List<List<IParameter>> ResultSet;
        //private List<byte[][]> ResultSet;
        private List<List<MessagePackObject>> ResultSet;

        private List<ResultSetHeader> header;

        public NetworkStream NetworkStream { get; set; }

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

            if (null == this.NetworkStream)
                return false;

            //如果是第一次读取
            if (header == null)
            {
                int headerSize = (NetworkStream.ReadByte() << 24) |
                       (NetworkStream.ReadByte() << 16) |
                       (NetworkStream.ReadByte() << 8) |
                       (NetworkStream.ReadByte() << 0);

                byte[] buffer = new byte[headerSize];

                NetworkStream.Read(buffer, 0, buffer.Length);

                header = headerSerializer.UnpackSingleObject(buffer);

            }
            //如果没有读取完成，且剩余的不足100个，则再次读取
            if (!readFinished &&  ((ResultSet == null) || (ResultSet.Count - cursor < 100)))
            {
                Stopwatch watch = new Stopwatch();
                watch.Start();

                int blockSize = (NetworkStream.ReadByte() << 24) |
                        (NetworkStream.ReadByte() << 16) |
                        (NetworkStream.ReadByte() << 8) |
                        (NetworkStream.ReadByte() << 0);

                readFinished = 1 == NetworkStream.ReadByte();

                byte[] buffer = new byte[blockSize - 1];

                NetworkStream.Read(buffer, 0, buffer.Length);

                //List<List<IParameter>> results = new List<List<IParameter>>();
                //List<byte[][]> results = new List<byte[][]>();
                List<List<MessagePackObject>> results = new List<List<MessagePackObject>>();

                if (ResultSet != null && ResultSet.Count > 0)
                {
                    results.AddRange(ResultSet.GetRange(cursor, ResultSet.Count - cursor));
                }

                //List<List<IParameter>> readerResults = serializer.UnpackSingleObject(buffer);
                //List<List<MessagePackObject>> readerResults = serializer.UnpackSingleObject(buffer);

                var se = MessagePackSerializer.Create<List<List<MessagePackObject>>>();

                 results.AddRange(se.UnpackSingleObject(buffer));

                buffer = null;
                se = null;

                watch.Stop();

                MonitorData data = MonitorData.GetInstance();

                if (data != null)
                {
                    //data.TotalTime += watch.ElapsedMilliseconds;
                    data.DecodeResponseTime += watch.ElapsedMilliseconds;
                }

                if (ResultSet != null)
                {
                    ResultSet.Clear();
                }
                ResultSet = results;
                cursor = 0;
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
            if(null != this.ResultSet)
                this.ResultSet.Clear();
            this.current = null;
            this.ResultSet = null;
        }

        /// <summary>
        /// 获取当前行中的列数
        /// </summary>
        public int FieldCount
        {
            get { return current.Count; }
            //get { return current.Length; }
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

                if (header == null || current == null)
                    return null;

                int lableIndex = -1;

                //获取当前列名的索引位置
                for (int i=0;i<header.Count;i++)
                {
                    var p = header[i];
                    if (p.ColumnName.Equals(name))
                    {
                        lableIndex = i;
                        break;
                    }
                }

                int currentType = header[lableIndex].ColumnType;

                MessagePackObject currentValue = current[lableIndex];

                switch (currentType)
                {
                    case Types.DECIMAL:
                        result = decimal.Parse(currentValue.AsString());
                        break;
                    case Types.TIMESTAMP:
                        result = utcStartTime.AddMilliseconds(currentValue.AsUInt64());
                        break;
                    default:
                        result = currentValue.ToObject();
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

                if (header == null || current == null)
                    return null;

                int lableIndex = i;

                int currentType = header[lableIndex].ColumnType;

                MessagePackObject currentValue = current[lableIndex];

                switch (currentType)
                {
                    case Types.DECIMAL:
                        result = decimal.Parse(currentValue.AsString());
                        break;
                    case Types.TIMESTAMP:
                        result = utcStartTime.AddMilliseconds(currentValue.AsUInt64());
                        break;
                    default:
                        result = currentValue.ToObject();
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
