using Arch.Data.Common.Enums;
using Arch.Data.Common.Logging;
using Arch.Data.Common.Util;
using Arch.Data.Common.Vi;
using System;
using System.Collections.Concurrent;

namespace Arch.Data.DbEngine.MarkDown
{
    public abstract class TimeoutMarkDown : IMarkDown
    {
        protected ITimeoutMarkDownBean timeoutMarkDownBean
        {
            get { return BeanManager.GetTimeoutMarkDownBean(); }
        }

        public abstract Boolean IsAbnormalExceptionTimeout(Exception ex);

        //此处设计一个数据结构：
        /**
         *  每个数据访问都进行采样，取最近的三次采样，计算期望和方差
         *           
         *  设置一个时间周期，从某个时间点开始采样，每有一个采样发生，检测最初一个采样到当前采样的时间周期，
         *  如果采样到的数据，满足某种条件，则清空所有采样，并记录下来
         *  如果采样到的数据，不满足某种条件，则保留，并参与后续采样的期望和方差的计算
         *  
         * 如果满足条件：
         *          如果期望大于1万，则不再计算方差，直接Mark Down
         *          如果期望小于1万，则计算方差，方差参考值：((70%+85%+100%)*1万/3-70%*1万)^2 + ((70%+85%+100%)*1万/3-85%*1万)^2+((70%+85%+100%)*1万/3-100%*1万)^2，如果远大于参考值，暂时不Mark Down
         *  
         * 某种条件指：
         *          如果过去1分钟内，有1万个以上的数据访问错误，或者1千个数据访问中，有80%的错误，则满足条件
         *          
         * 放开条件：
         *          Mark Down 5分钟后，放开10%的数据访问
         *          1. 如果过去1分钟内，有1千个（1万*10%）以上的数据访问错误，或者100个数据访问中，有80%的错误（扩大10倍计算期望和方差），则再次Mark Down。
         *          2. 如果不再触发Mark Down条件，则共放开30%(70%,100%)数据访问，继续进行1步骤
         *          
         * 以上的数据部分均可调节，包括时间，比例，数量
         * 
         */
        private static ConcurrentDictionary<String, MarkDownSampling> samplings = new ConcurrentDictionary<String, MarkDownSampling>();

        public void Monitor(String databaseSet, String allInOneKey, Exception ex)
        {
            //多线程运作
            //如果Timeout Mark Down没有启用，跳出
            if (!timeoutMarkDownBean.EnableTimeoutMarkDown || (timeoutMarkDownBean.ErrorCountThreshold <= 0 && timeoutMarkDownBean.ErrorPercentThreshold <= 0.000001))
                return;

            if (AutoMarkDown.DatabaseMarkingUp(allInOneKey))
            {
                AutoMarkDown.AutoMarkUpMonitor(allInOneKey, IsAbnormalExceptionTimeout(ex));
            }
            else if (ex != null)
            {
                var sampling = samplings.GetOrAdd(databaseSet + allInOneKey, key => new MarkDownSampling());
                Int64 totalExceptionCount, timeoutExceptionCount;
                sampling.Sampling(IsAbnormalExceptionTimeout(ex), out totalExceptionCount, out timeoutExceptionCount);

                Boolean needMarkDown = timeoutExceptionCount >= timeoutMarkDownBean.ErrorCountThreshold;
                MarkDownCategory? category = null;
                if (needMarkDown)
                {
                    sampling.Initialize();
                    sampling.Reset();
                    category = MarkDownCategory.ErrorCount;
                }

                if (!needMarkDown && totalExceptionCount >= timeoutMarkDownBean.ErrorPercentReferCount)
                {
                    needMarkDown = timeoutExceptionCount >= timeoutMarkDownBean.ErrorPercentThreshold * totalExceptionCount;
                    if (needMarkDown) category = MarkDownCategory.ErrorPercent;
                }

                //mark it down
                if (needMarkDown && timeoutExceptionCount > 0)
                {
                    AutoMarkDown.AutoMarkDownADatabase(allInOneKey);
                    var info = new MarkDownMetrics
                    {
                        AllInOneKey = allInOneKey,
                        TotalCount = totalExceptionCount,
                        TimeoutCount = timeoutExceptionCount,
                        Category = category.Value,
                        MarkDownPolicy = MarkDownPolicy.Timeout,
                        SamplingDuration = timeoutMarkDownBean.SamplingDuration
                    };

                    LogManager.Logger.MetricsMarkdown(info);
                    LogManager.Logger.LogMarkdown(LogLevel.Error, info.ToString(), "SYS1L2001");
                }
            }
        }

    }
}