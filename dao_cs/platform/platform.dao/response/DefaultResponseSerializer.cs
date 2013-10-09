using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MsgPack.Serialization;
using MsgPack;
using platform.dao.param;
using System.Data;

namespace platform.dao.response
{
    public class DefaultResponseSerializer : MessagePackSerializer<DefaultResponse>
    {

        public DefaultResponseSerializer()
            : this(SerializationContext.Default) { }

        public DefaultResponseSerializer(SerializationContext context)
        {
            // If the target objects has complex (non-primitive) objects,
            // you can get serializers which can handle complex type fields.
            // And then, you can cache them to instance fields of this custom serializer.
            context.Serializers.Register<DefaultResponse>(this);
        }

        protected override void PackToCore(Packer packer, DefaultResponse value)
        {


        }

        protected override DefaultResponse UnpackFromCore(Unpacker unpacker)
        {
            DefaultResponse response = new DefaultResponse();

            //当前有多少元素需要反序列化
            //long arrayLength;

            //unpacker.ReadArrayLength(out arrayLength);

            //int arrayLength;

            //unpacker.ReadInt32(out arrayLength);

            //任务ID
            byte[] taskid;

            unpacker.ReadBinary(out taskid);

            response.Taskid = new Guid(taskid);

            //读或是写
            int operationType;

            unpacker.ReadInt32(out operationType);

            response.ResultType = (enums.OperationType)operationType;

            //影响行数或者查询结果
            if (response.ResultType == enums.OperationType.Read)
            {
                response.ResultSet = UnpackStatementParamenterCollection(unpacker);
            }
            else if (response.ResultType == enums.OperationType.Write)
            {
                int affectCount;
                unpacker.ReadInt32(out affectCount);

                response.AffectRowCount = affectCount;
            }

            long totalTime;
            unpacker.ReadInt64(out totalTime);

            response.TotalTime = totalTime;

            long decodeRequestTime;
            unpacker.ReadInt64(out decodeRequestTime);

            response.DecodeRequestTime = decodeRequestTime;

            long dbtime;
            unpacker.ReadInt64(out dbtime);

            response.DbTime = dbtime;

            long encodeResponse;
            unpacker.ReadInt64(out encodeResponse);

            response.EncodeResponseTime = encodeResponse;

            return response;
        }

        public List<List<IParameter>> UnpackStatementParamenterCollection(Unpacker unpacker)
        {
            long arrayLength;

            unpacker.ReadArrayLength(out arrayLength);

            List<List<IParameter>> results = new List<List<IParameter>>((int)arrayLength);

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
