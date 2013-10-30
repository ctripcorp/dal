using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.response
{
    public class ResultSetHeader
    {

        public int[] Indexes { get; set; }

        public string[] Lables { get; set; }

        public int[] Types { get; set; }

    }
}
