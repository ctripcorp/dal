using System;

namespace Arch.Data.DbEngine.MarkDown
{
    public class MarkUpMetrics
    {
        public String AllInOneKey { get; set; }

        public Int32 SuccessCount { get; set; }

        public Int32 Batches { get; set; }

        public Int32 MarkUpDelay { get; set; }

        public Boolean Success { get; set; }
    }
}
