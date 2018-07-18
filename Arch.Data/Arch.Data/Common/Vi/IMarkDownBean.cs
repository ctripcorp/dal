using System;

namespace Arch.Data.Common.Vi
{
    public interface IMarkDownBean
    {
        Boolean EnableAutoMarkDown { get; set; }

        Boolean AppIsMarkDown { get; set; }

        Int32 AutoMarkUpDelay { get; set; }

        Int32 AutoMarkUpBatches { get; set; }

        String AutoMarkUpSchedule { get; set; }

        String MarkDownKeys { get; set; }

        String AutoMarkDowns { get; }

        String AllInOneKeys { get; set; }

    }
}
