using System;

namespace Arch.Data.Common.Vi
{
    public interface ITimeoutMarkDownBean
    {
        Boolean EnableTimeoutMarkDown { get; set; }

        Int32 SamplingDuration { get; set; }

        Int32 ErrorPercentReferCount { get; set; }

        Int32 ErrorCountThreshold { get; set; }

        Double ErrorPercentThreshold { get; set; }

        String SqlServerErrorCodes { get; set; }

        String MySqlErrorCodes { get; set; }

    }
}
