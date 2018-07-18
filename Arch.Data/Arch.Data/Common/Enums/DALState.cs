using System.ComponentModel;

namespace Arch.Data.Common.Enums
{
    public enum DALState
    {
        [Description("executing")]
        Executing,

        [Description("fail")]
        Fail,

        [Description("success")]
        Success
    }
}
