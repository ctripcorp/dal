﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;
using platform.dao.param;

namespace platform.dao.response
{
    public class DefaultResponse : AbstractResponse
    {

        private static DefaultResponseSerializer serializer;

        static DefaultResponse()
        {
            serializer = new DefaultResponseSerializer();
        }

        public Guid Taskid { get; set; }

        public OperationType ResultType { get; set; }

        //public int AffectRowCount { get; set; }

        //public List<List<IParameter>> ResultSet { get; set; }

        //public long TotalTime { get; set; }
        //public long DecodeRequestTime { get; set; }
        //public long DbTime { get; set; }
        //public long EncodeResponseTime { get; set; }

        public static DefaultResponse UnpackFromByteArray(byte[] payload)
        {
            return serializer.UnpackSingleObject(payload);
        }

    }
}
