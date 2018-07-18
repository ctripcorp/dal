using System;

namespace Arch.Data.Common.Vi
{
    public interface IHABean
    {
        Boolean EnableHA { get; set; }

        Int32 RetryTimes { get; set; }

        String SqlServerErrorCodes { get; set; }

        String MySqlErrorCodes { get; set; }

    }
}
