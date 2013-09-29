using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using System.Data;
using platform.dao.type;

namespace platform.dao.param
{
    /// <summary>
    /// 指令参数
    /// </summary>
    internal sealed class StatementParameterCollection : KeyedCollection<string, IParameter>
    {
        public StatementParameterCollection()
            : base(StringComparer.CurrentCultureIgnoreCase)
        {
        }


        /// <summary>
        /// following add parameter functions is to be consistent to db.addinparameter functions
        /// Regular expression replace
        /// db.AddInParameter\(dbCommand,{.*}   => dic.AddInParameter(\1
        /// </summary>
        /// <param name="name"></param>
        /// <param name="dbType"></param>
        public void AddInParameter(string name, DbType dbType, bool sensi = false)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Direction = ParameterDirection.Input,
                IsSensitive = sensi
            });
        }

        public void AddInParameter(string name, DbType dbType, MsgPack.MessagePackObject value, bool sensi = false)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Value = value,
                Direction = ParameterDirection.Input,
                IsSensitive = sensi,
            });

        }

        public void AddOutParameter(string name, DbType dbType, bool sensi = false)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Direction = ParameterDirection.Output,
                IsSensitive = sensi,
            });

        }

        public void AddOutParameter(string name, DbType dbType, int size, bool sensi = false)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Direction = ParameterDirection.Output,
                Size = size,
                IsSensitive = sensi,
            });

        }

        public void AddParameter(string name, DbType dbType, MsgPack.MessagePackObject value, int size, ParameterDirection dir, bool sensi = false)
        {
            Add(new StatementParameter()
            {
                Name = name,
                DbType = dbType,
                Value = value,
                Direction = dir,
                Size = size,
                IsSensitive = sensi,
            });

        }

        protected override string GetKeyForItem(IParameter item)
        {
            return item.Name;
        }
    }
}
