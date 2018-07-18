using System;
using System.Reflection;

namespace Arch.Data.Orm.sql
{
    public class SqlTrigger
    {
        public SqlTrigger(MethodInfo method, Timing timing)
        {
            Method = method;
            Timing = timing;
        }

        public MethodInfo Method { get; private set; }

        public Timing Timing { get; private set; }

        public void Fire(Object target, Object[] args)
        {
            Method.Invoke(target, args);
        }
    }
}
