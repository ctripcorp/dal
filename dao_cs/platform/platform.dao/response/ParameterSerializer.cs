﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MsgPack.Serialization;
using platform.dao.param;
using MsgPack;
using System.Data;

namespace platform.dao.response
{
    public class ParameterSerializer : MessagePackSerializer<List<byte[][]>>
    //public class ParameterSerializer : MessagePackSerializer<List<List<IParameter>>>
    {

        public ParameterSerializer()
            : this(SerializationContext.Default) { }

        public ParameterSerializer(SerializationContext context)
        {
            // If the target objects has complex (non-primitive) objects,
            // you can get serializers which can handle complex type fields.
            // And then, you can cache them to instance fields of this custom serializer.
            context.Serializers.Register<List<byte[][]>>(this);
            //context.Serializers.Register<List<List<IParameter>>>(this);
        }

        //protected override void PackToCore(Packer packer, List<List<IParameter>> value)
        protected override void PackToCore(Packer packer, List<byte[][]> value)
        {

        }

        //protected override List<List<IParameter>> UnpackFromCore(Unpacker unpacker)
        protected override List<byte[][]> UnpackFromCore(Unpacker unpacker)
        {
            //共有多少行
            int arrayLength = (int)unpacker.ItemsCount;

            List<byte[][]> results = new List<byte[][]>(arrayLength);

            for (int i = 0; i < arrayLength; i++)
            {
                //每行有多少列
                long fieldCount;

                unpacker.ReadArrayLength(out fieldCount);

                List<byte[]> row = new List<byte[]>((int)fieldCount);

                for (int j = 0; j < fieldCount; j++)
                {
                    byte[] result;
                    //获取每一列的值
                    if (unpacker.ReadBinary(out result))
                    {
                        row.Add(result);
                    }

                }

                results.Add(row.ToArray());

            }

            return results;

            //int arrayLength = (int)unpacker.ItemsCount;

            //List<List<IParameter>> results = new List<List<IParameter>>(arrayLength);

            //for (int i = 0; i < arrayLength; i++)
            //{
            //    long currentLength;

            //    unpacker.ReadArrayLength(out currentLength);

            //    List<IParameter> result = new List<IParameter>((int)currentLength);

            //    for (int j = 0; j < currentLength; j++)
            //    {
            //        try
            //        {
            //            result.Add(UnpackStatementParameter(unpacker));
            //        }
            //        catch
            //        {

            //        }
            //    }

            //    results.Add(result);

            //}

            

            //return results;
        }

        public IParameter UnpackStatementParameter(Unpacker unpacker)
        {
            //long arrayLength;

            //unpacker.ReadArrayLength(out arrayLength);

            int dbType;

            unpacker.ReadInt32(out dbType);

            int direction;

            unpacker.ReadInt32(out direction);

            bool nullable;

            unpacker.ReadBoolean(out nullable);

            bool sensitive;

            unpacker.ReadBoolean(out sensitive);

            int index;

            unpacker.ReadInt32(out index);

            string name;

            unpacker.ReadString(out name);

            int size;

            unpacker.ReadInt32(out size);

            MessagePackObject value;

            unpacker.ReadObject(out value);

            return new StatementParameter()
            {
                DbType = (DbType)dbType,
                Direction = (ParameterDirection)direction,
                IsNullable = nullable,
                IsSensitive = sensitive,
                Name = name,
                Index=index,
                Size = size,
                Value = value
            };

        }


    }
}
