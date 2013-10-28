using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MsgPack.Serialization;
using MsgPack;
using platform.dao.param;
using System.Text.RegularExpressions;

namespace platform.dao.request
{
    public class DefaultRequestSerializer : MessagePackSerializer<DefaultRequest>
    {

        public DefaultRequestSerializer()
            : this(SerializationContext.Default) { }

        public DefaultRequestSerializer(SerializationContext context)
        {
            // If the target objects has complex (non-primitive) objects,
            // you can get serializers which can handle complex type fields.
            // And then, you can cache them to instance fields of this custom serializer.
            context.Serializers.Register<DefaultRequest>(this);
        }

        protected override void PackToCore(Packer packer, DefaultRequest value)
        {
            //总共多少个属性需要序列化
            packer.PackArrayHeader(value.GetPropertyCount());

            //for (int i = 0; i < taskidArray.Length/2; i++)
            //{
            //    taskidArray[i] = 
            //}

            packer.Pack(value.Taskid.ToString());

            packer.Pack(value.DbName);

            packer.Pack(value.Credential);

            PackRequestMessage(packer, value.Message);

        }

        private void PackRequestMessage(Packer packer, RequestMessage value)
        {

            //总共多少个属性需要序列化
            packer.PackArrayHeader(value.GetPropertyCount());

            packer.Pack((int)value.StatementType);

            packer.Pack((int)value.OperationType);

            packer.Pack(value.UseCache);

            if (value.StatementType == enums.StatementType.Sql)
            {
                //packer.Pack(Regex.Replace(value.Sql, @"[@|:]\w+", "?"));
                packer.Pack(value.Sql);
            }
            else
            {
                //packer.Pack(Regex.Replace(value.SpName, @"[@|:]\w+", "?"));
                packer.Pack(value.SpName);
            }

            PackStatementParameterCollection(packer, value.Parameters);

            packer.Pack(value.Flags);

        }

        private void PackStatementParameterCollection(Packer packer, IList<IParameter> value)
        {

            //总共多少个属性需要序列化
            packer.PackArrayHeader(value.Count);

            //int i = 1;
            foreach (IParameter p in value)
            {
                packer.PackArrayHeader(7);
                packer.Pack((int)p.DbType);
                packer.Pack((int)p.Direction);
                packer.Pack(p.IsNullable);
                packer.Pack(p.IsSensitive);

                //TODO: change to index
                //packer.Pack(i);
                packer.Pack(p.Index);
                packer.Pack(p.Size);
                packer.Pack(p.Value);
                //i++;
            }

        }

        protected override DefaultRequest UnpackFromCore(Unpacker unpacker)
        {
            throw new NotImplementedException();
        }

    }
}
