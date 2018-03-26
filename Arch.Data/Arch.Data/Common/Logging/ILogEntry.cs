using System;

namespace Arch.Data.Common.Logging
{
    public interface ILogEntry
    {
        String ToBrief();

        String ToJson();
    }
}