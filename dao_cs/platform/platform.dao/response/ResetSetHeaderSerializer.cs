using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MsgPack.Serialization;

namespace platform.dao.response
{
    public class ResultSetHeaderSerializer : MessagePackSerializer<List<ResultSetHeader>>
    {

         public ResultSetHeaderSerializer()
            : this(SerializationContext.Default) { }

         public ResultSetHeaderSerializer(SerializationContext context)
        {
            // If the target objects has complex (non-primitive) objects,
            // you can get serializers which can handle complex type fields.
            // And then, you can cache them to instance fields of this custom serializer.
            context.Serializers.Register<List<ResultSetHeader>>(this);
        }

         protected override void PackToCore(MsgPack.Packer packer, List<ResultSetHeader> objectTree)
        {
            throw new NotImplementedException();
        }

         protected override List<ResultSetHeader> UnpackFromCore(MsgPack.Unpacker unpacker)
        {

            int counts;

            //unpacker.ReadArrayLength(out counts);
            counts = (int)unpacker.ItemsCount;


            List<ResultSetHeader> results = new List<ResultSetHeader>(counts);

            for (int i = 0; i < counts; i++)
            {
                
                string columnName;
                    
                unpacker.ReadString(out columnName);

                int columnType;

                unpacker.ReadInt32(out columnType);

                results.Add(new ResultSetHeader() { ColumnName = columnName, ColumnType = columnType});
            }

            unpacker.Dispose();

            return results;

            ////读取下标索引
            //long indexesCount;

            //unpacker.ReadArrayLength(out indexesCount);

            //List<int> indexes = new List<int>((int)indexesCount);

            //for (int i = 0; i < indexesCount; i++)
            //{
            //    int index;

            //    unpacker.ReadInt32(out index);

            //    indexes.Add(index);

            //}

            ////读取所有列名
            //long labelsCount;

            //unpacker.ReadArrayLength(out labelsCount);

            //List<string> lables = new List<string>((int)labelsCount);

            //for (int i = 0; i < labelsCount; i++)
            //{
            //    string lable;

            //    unpacker.ReadString(out lable);

            //    lables.Add(lable);

            //}

            ////读取所有类型的标识
            //long typesCount;

            //unpacker.ReadArrayLength(out typesCount);

            //List<int> types = new List<int>((int)typesCount);

            //for (int i = 0; i < typesCount; i++)
            //{
            //    int type;

            //    unpacker.ReadInt32(out type);

            //    types.Add(type);

            //}

            //return new ResultSetHeader()
            //{ 
            //    Indexes = indexes.ToArray(),
            //    Lables = lables.ToArray(),
            //    Types = types.ToArray()
            //};

        }
    }
}
