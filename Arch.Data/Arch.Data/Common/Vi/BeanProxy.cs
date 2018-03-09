
namespace Arch.Data.Common.Vi
{
    class BeanProxy : IBeanProxy
    {
        public void Register()
        {
            AbstractDALBean.Register();
            AbstractHABean.Register();
            AbstractMarkDownBean.Register();
            AbstractTimeoutMarkDownBean.Register();
        }

        public IDALBean GetDALBean()
        {
            return AbstractDALBean.GetInstance();
        }

        public IHABean GetHABean()
        {
            return AbstractHABean.GetInstance();
        }

        public IMarkDownBean GetMarkDownBean()
        {
            return AbstractMarkDownBean.GetInstance();
        }

        public ITimeoutMarkDownBean GetTimeoutBean()
        {
            return AbstractTimeoutMarkDownBean.GetInstance();
        }

    }
}
