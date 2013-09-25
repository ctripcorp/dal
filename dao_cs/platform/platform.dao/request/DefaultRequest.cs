using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.request
{
    public class DefaultRequest : AbstractRequest
    {

        private static DefaultRequestSerializer serializer;

        static DefaultRequest()
        {
            serializer = new DefaultRequestSerializer();
        }

        public Guid Taskid { get; set; }

        public string DbName { get; set; }

        public string Credential { get; set; }

        public RequestMessage Message {get;set;}

        public override int GetPropertyCount()
        {
            return 4;
        }

        public override byte[] Pack2ByteArray()
        {
            return serializer.PackSingleObject(this);
        }

    }
}
