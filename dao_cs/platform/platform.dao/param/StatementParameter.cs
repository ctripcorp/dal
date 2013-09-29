using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using platform.dao.type;

namespace platform.dao.param
{
    /// <summary>
    /// 对于In类型的参数，将@variable替换为多个?，然后根据传入参数的多少，
    /// 填入相应数量的？，传向DAS或者DbClient
    /// 其他参数直接替换为？，传向DAS或者DbClient
    /// </summary>
    internal sealed class StatementParameter : IParameter
    {

        private DbType dbType = DbType.Boolean;
        private ParameterDirection direction = ParameterDirection.Input;
        private bool isNullable = false;
        private string name;
        private int index;
        private int size;
        private MsgPack.MessagePackObject value;
        private bool isSensitive = false;

        public DbType DbType
        {
            get
            {
                return dbType;
            }
            set
            {
                dbType = value;   
            }
        }

        public ParameterDirection Direction
        {
            get
            {
                return direction;
            }
            set
            {
                direction = value;
            }
        }

        public bool IsNullable
        {
            get
            {
                return isNullable;
            }
            set
            {
                isNullable = value;
            }
        }

        public string Name
        {
            get
            {
                return name;
            }
            set
            {
                name = value;
            }
        }

        public int Index
        {
            get
            {
                return index;
            }
            set
            {
                index = value;
            }
        }

        public int Size
        {
            get
            {
                return size;
            }
            set
            {
                size = value;
            }
        }

        public MsgPack.MessagePackObject Value
        {
            get
            {
                return value;
            }
            set
            {
                this.value = value;
            }
        }

        public bool IsSensitive
        {
            get
            {
                return isSensitive;
            }
            set
            {
                isSensitive = value;
            }
        }

    }
}
