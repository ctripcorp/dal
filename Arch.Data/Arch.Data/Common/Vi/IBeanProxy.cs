
namespace Arch.Data.Common.Vi
{
    public interface IBeanProxy
    {
        void Register();

        IDALBean GetDALBean();

        IHABean GetHABean();

        IMarkDownBean GetMarkDownBean();

        ITimeoutMarkDownBean GetTimeoutBean();

    }
}
