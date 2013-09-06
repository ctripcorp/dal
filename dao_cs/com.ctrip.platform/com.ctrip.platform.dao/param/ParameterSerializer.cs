using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MsgPack;
using MsgPack.Serialization;

namespace com.ctrip.platform.dao.param
{
    public class ParameterSerializer : MessagePackSerializer<IParameter>
    {

        public ParameterSerializer()
            : this(SerializationContext.Default) { }

        public ParameterSerializer(SerializationContext context)
        {
            // If the target objects has complex (non-primitive) objects,
            // you can get serializers which can handle complex type fields.
            // And then, you can cache them to instance fields of this custom serializer.
            context.Serializers.Register<IParameter>(this);
        }

        protected override void PackToCore(Packer packer, IParameter value)
        {

            //value.pack(packer);
        }



        protected override IParameter UnpackFromCore(Unpacker unpacker)
        {
            throw new NotImplementedException();
        }
    }
}
