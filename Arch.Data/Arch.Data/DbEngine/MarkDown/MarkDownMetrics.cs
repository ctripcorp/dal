using Arch.Data.Common.Enums;
using System;
using System.Text;

namespace Arch.Data.DbEngine.MarkDown
{
    public class MarkDownMetrics
    {
        public String AllInOneKey { get; set; }

        public MarkDownPolicy MarkDownPolicy { get; set; }

        public Int32 SamplingDuration { get; set; }

        public Int64 TotalCount { get; set; }

        public Int64 TimeoutCount { get; set; }

        public MarkDownCategory Category { get; set; }

        public override String ToString()
        {
            return new StringBuilder()
                .AppendFormat("AllInOneKey:{0},", AllInOneKey)
                .AppendFormat("MarkDownPolicy:{0},", MarkDownPolicy)
                .AppendFormat("SamplingDuration:{0},", SamplingDuration)
                .AppendFormat("TotalExceptionCount:{0},", TotalCount)
                .AppendFormat("AbnormalTimeoutCount:{0},", TimeoutCount)
                .AppendFormat("Category:{0},", Category)
                .ToString();
        }
    }
}
