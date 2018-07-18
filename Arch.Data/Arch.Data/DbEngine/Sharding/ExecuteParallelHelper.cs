using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Transactions;

namespace Arch.Data.DbEngine.Sharding
{
    class ExecuteParallelHelper
    {
        public static List<T> ParallelExcuter<T>(IList<Func<T>> list, Boolean isSameShard = false)
        {
            var result = new List<T>();
            Int32 count = list.Count;

            if (count == 1)
            {
                result.Add(list[0].Invoke());
                return result;
            }

            if (Transaction.Current != null)
            {
                if (isSameShard)
                {
                    foreach (var item in list)
                    {
                        result.Add(item.Invoke());
                    }
                    return result;
                }
                else
                {
                    throw new DalException("Distributed transaction is not supported.");
                }
            }

            var tasks = new Task<T>[count];

            for (Int32 i = 0; i < count; i++)
            {
                var task = new Task<T>(list[i]);
                tasks[i] = task;
                task.Start();
            }

            Task.WaitAll(tasks);

            foreach (var item in tasks)
            {
                result.Add(item.Result);
            }

            return result;
        }

    }
}
