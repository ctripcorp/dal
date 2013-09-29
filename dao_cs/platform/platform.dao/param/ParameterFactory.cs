using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using MsgPack;
using platform.dao.utils;

namespace platform.dao.param
{
    /// <summary>
    /// 几个显而易见的转换：
    /// decimal -> string
    /// StringFixedLengh -> ushort
    /// Guid -> byte[]
    /// Datetime -> long
    /// </summary>
    public sealed class ParameterFactory
    {

        /// <summary>
        /// 创建参数
        /// </summary>
        /// <param name="name"></param>
        /// <param name="value"></param>
        /// <param name="direction"></param>
        /// <param name="index"></param>
        /// <param name="nullable"></param>
        /// <param name="sensitive"></param>
        /// <param name="size"></param>
        /// <returns></returns>
        public static IParameter CreateValue(string name, object value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            DbType type = TypeConverter.ResolveType(value.GetType());
            MessagePackObject obj;

            if (type == DbType.Decimal)
                obj = MessagePackObject.FromObject(value.ToString());
            else if (type == DbType.StringFixedLength)
                obj = MessagePackObject.FromObject((ushort)value);
            else if (type == DbType.Guid)
                obj = MessagePackObject.FromObject(((Guid)value).ToByteArray());
            else if (type == DbType.DateTime)
                obj = MessagePackObject.FromObject(((DateTime)value).Ticks / TimeSpan.TicksPerMillisecond);
            else
                obj = MessagePackObject.FromObject(value);

            return new StatementParameter()
            {
                DbType = type,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = obj
            };
        }

        /// <summary>
        /// 创建布尔型参数
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateBooleanValue(string name, bool value, ParameterDirection direction = ParameterDirection.Input, int index=0, bool nullable=false, bool sensitive=false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Boolean,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建字节类型参数
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateByteValue(string name, byte value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Byte,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建可负字节参数
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateSByteValue(string name, sbyte value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.SByte,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建short
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateShortValue(string name, short value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Int16,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建整形
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateIntValue(string name, int value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Int32,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建长整型
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateLongValue(string name, long value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Int64,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建非负Short型
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateUShortValue(string name, ushort value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.UInt16,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建非负整形
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateUIntValue(string name, uint value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.UInt32,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建非负长整型
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateULongValue(string name, ulong value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.UInt64,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建Float
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateFloatValue(string name, float value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Single,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建Double
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateDoubleValue(string name, double value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Double,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建超精度型
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateDecimalValue(string name, decimal value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Decimal,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value.ToString())
            };
        }

        /// <summary>
        /// 创建字符类型
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateCharValue(string name, char value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.StringFixedLength,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建字符串
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateStringValue(string name, string value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.String,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

        /// <summary>
        /// 创建GUID
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateGuidValue(string name, Guid value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Guid,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value.ToByteArray())
            };
        }

        /// <summary>
        /// 创建日志类型
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateDateTimeValue(string name, DateTime value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.DateTime,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value.Ticks/TimeSpan.TicksPerMillisecond)
            };
        }

        /// <summary>
        /// 创建二进制数据
        /// </summary>
        /// <param name="name">参数名，必须</param>
        /// <param name="value">参数值，必须</param>
        /// <param name="direction">参数方向，默认Input</param>
        /// <param name="index">参数编号，不填则程序默认生成（有错误产生的可能）</param>
        /// <param name="nullable">是否可空，默认否</param>
        /// <param name="sensitive">是否包含敏感字符</param>
        /// <param name="size">参数对应的数据库类型大小</param>
        /// <returns>创建好的参数</returns>
        public static IParameter CreateBinaryValue(string name, byte[] value, ParameterDirection direction = ParameterDirection.Input, int index = 0, bool nullable = false, bool sensitive = false, int size = 50)
        {
            return new StatementParameter()
            {
                DbType = DbType.Binary,
                Direction = direction,
                Index = index,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Size = size,
                Value = new MessagePackObject(value)
            };
        }

    }
}
