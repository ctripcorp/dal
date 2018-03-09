using System;

namespace Arch.Data.Orm
{
    [AttributeUsage(AttributeTargets.Method, Inherited = false)]
    public class TriggerAttribute : Attribute
    {
        public TriggerAttribute(Timing timing)
        {
            Timing = timing;
        }

        public Timing Timing { get; private set; }
    }
}
