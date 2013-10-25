using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MsgPack.Serialization;
using platform.dao.param;
using MsgPack;
using System.Data;

namespace platform.dao.response
{
    public class ParameterSerializer : MessagePackSerializer<List<List<IParameter>>>
    {

        public ParameterSerializer()
            : this(SerializationContext.Default) { }

        public ParameterSerializer(SerializationContext context)
        {
            // If the target objects has complex (non-primitive) objects,
            // you can get serializers which can handle complex type fields.
            // And then, you can cache them to instance fields of this custom serializer.
            context.Serializers.Register<List<List<IParameter>>>(this);
        }

        protected override void PackToCore(Packer packer, List<List<IParameter>> value)
        {

        }

        protected override List<List<IParameter>> UnpackFromCore(Unpacker unpacker)
        {
            //long arrayLength;

            //unpacker.ReadArrayLength(out arrayLength);

            //List<List<IParameter>> results = new List<List<IParameter>>((int)arrayLength);

            int arrayLength = (int)unpacker.ItemsCount;

            List<List<IParameter>> results = new List<List<IParameter>>(arrayLength);

            for (int i = 0; i < arrayLength; i++)
            {
                long currentLength;

                unpacker.ReadArrayLength(out currentLength);

                List<IParameter> result = new List<IParameter>((int)currentLength);

                for (int j = 0; j < currentLength; j++)
                {
                    result.Add(UnpackStatementParameter(unpacker));
                }

                results.Add(result);

            }

            return results;
        }

        public IParameter UnpackStatementParameter(Unpacker unpacker)
        {
            long arrayLength;

            unpacker.ReadArrayLength(out arrayLength);

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
