using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using platform.dao.param;

namespace platform.dao.client
{
    public class DasDataReader : IDataReader
    {
        private static readonly DateTime utcStartTime;

        static DasDataReader()
        {
            utcStartTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
        }

        private int cursor = 0;

        private List<IParameter> current;

        public List<List<IParameter>> ResultSet { get; set; }

        public void Close()
        {
            this.Dispose();
        }

        public int Depth
        {
            get { throw new NotImplementedException(); }
        }

        public DataTable GetSchemaTable()
        {
            throw new NotImplementedException();
        }

        public bool IsClosed
        {
            get { throw new NotImplementedException(); }
        }

        public bool NextResult()
        {
            throw new NotImplementedException();
        }

        public bool Read()
        {
            var result = cursor < ResultSet.Count;
            if (result)
            {
                current = ResultSet[cursor].OrderBy(o => o.Index).ToList();
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
            this.current.Clear();
            this.ResultSet.Clear();
            this.current = null;
            this.ResultSet = null;
        }

        public int FieldCount
        {
            get { throw new NotImplementedException(); }
        }

        public bool GetBoolean(int i)
        {
            throw new NotImplementedException();
        }

        public byte GetByte(int i)
        {
            throw new NotImplementedException();
        }

        public long GetBytes(int i, long fieldOffset, byte[] buffer, int bufferoffset, int length)
        {
            throw new NotImplementedException();
        }

        public char GetChar(int i)
        {
            throw new NotImplementedException();
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
        }

        public decimal GetDecimal(int i)
        {
            throw new NotImplementedException();
        }

        public double GetDouble(int i)
        {
            throw new NotImplementedException();
        }

        public Type GetFieldType(int i)
        {
            throw new NotImplementedException();
        }

        public float GetFloat(int i)
        {
            throw new NotImplementedException();
        }

        public Guid GetGuid(int i)
        {
            throw new NotImplementedException();
        }

        public short GetInt16(int i)
        {
            throw new NotImplementedException();
        }

        public int GetInt32(int i)
        {
            throw new NotImplementedException();
        }

        public long GetInt64(int i)
        {
            throw new NotImplementedException();
        }

        public string GetName(int i)
        {
            throw new NotImplementedException();
        }

        public int GetOrdinal(string name)
        {
            throw new NotImplementedException();
        }

        public string GetString(int i)
        {
            throw new NotImplementedException();
        }

        public object GetValue(int i)
        {
            throw new NotImplementedException();
        }

        public int GetValues(object[] values)
        {
            throw new NotImplementedException();
        }

        public bool IsDBNull(int i)
        {
            throw new NotImplementedException();
        }

        public object this[string name]
        {
            get
            {
                object result = null;
                IParameter param = null;
                foreach (var p in current)
                {
                    if (p.Name.Equals(name))
                    {
                        param = p;
                        break;
                    }
                }

                if (param != null)
                {
                    if (param.DbType == DbType.Decimal)
                        result = decimal.Parse(param.Value.AsString());
                    else if (param.DbType == DbType.StringFixedLength)
                        result = (char)param.Value.AsUInt16();
                    else if (param.DbType == DbType.Guid)
                        result = new Guid(param.Value.AsBinary());
                    else if (param.DbType == DbType.DateTime)
                        result = utcStartTime.AddMilliseconds(param.Value.AsUInt64());
                    else
                        result = param.Value.ToObject();
                }
                
                return result;
            }
        }

        public object this[int i]
        {
            get
            {
                object result = null;
                foreach (var p in current)
                {
                    if (p.Index.Equals(i))
                    {
                        result = p.Value;
                        break;
                    }
                }
                return result;
            }
        }
    }
}
