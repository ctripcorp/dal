using Arch.Data.Common.Vi;
using System;

namespace Arch.Data.DbEngine.MarkDown
{
    class MarkDownSampling
    {
        private TimeSpan samplingDuration;
        private TimeSpan samplingInterval;
        private DateTime samplingStart;
        private Int64[] counters = new Int64[4];

        public MarkDownSampling()
        {
            Initialize();
        }

        private ITimeoutMarkDownBean timeoutMarkDownBean
        {
            get { return BeanManager.GetTimeoutMarkDownBean(); }
        }

        /// <summary>
        /// 从外界清空计数器，目前仅在需要Mark Down时执行，否则，交由计数器自己执行
        /// </summary>
        public void Initialize()
        {
            samplingDuration = new TimeSpan(0, 0, timeoutMarkDownBean.SamplingDuration);
            samplingInterval = new TimeSpan(0, 0, timeoutMarkDownBean.SamplingDuration / 2);
            samplingStart = DateTime.Now;
        }

        public void Reset()
        {
            counters[0] = 0;
            counters[1] = 0;
            counters[2] = 0;
            counters[3] = 0;
        }

        /// <summary>
        /// 计数，并检测是否需要计算，不是线程安全的
        /// </summary>
        /// <param name="isTimeoutException"></param>
        /// <param name="totalCount"></param>
        /// <param name="exceptionCount"></param>
        /// <returns></returns>
        public void Sampling(Boolean isTimeoutException, out Int64 totalCount, out Int64 exceptionCount)
        {
            if (isTimeoutException) counters[1]++;
            counters[0]++;
            checkSampling(out totalCount, out exceptionCount);
        }

        private void checkSampling(out Int64 totalCount, out Int64 exceptionCount)
        {
            DateTime now = DateTime.Now;
            TimeSpan timeElapsed = now - samplingStart;
            totalCount = 0;
            exceptionCount = 0;

            //以60秒采样周期为例（如果中途修改了ConfigBean，下个采样开始时，才会生效），数据结构的描述如下：long[4]
            //index 0 : 最近30秒的总数计数器，
            //index 1 : 最近30秒的异常计数器
            //index 2 : 前一个30秒的总数计数器
            //index 3 : 前一个30秒的异常计数器
            //如果超过了90秒，表明过去30秒均没有数据访问，直接清空所有数据
            //如果处于60秒到90秒之间，将0，1置于2，3上,并将0，1置为0，将开始时间置为半分钟前，即保留最近30秒的计数器，并将最近30秒的起点置为整个计数器的起点
            //如果处于30秒到60秒之间，将0，1的数据与2，3的数据交换，使计数器仍然加在 0,1上
            if (timeElapsed > samplingDuration.Add(samplingInterval))
            {
                Initialize();
                Reset();
            }
            else if (timeElapsed > samplingDuration)
            {
                totalCount = counters[0] + counters[2];
                exceptionCount = counters[1] + counters[3];

                counters[2] = counters[0];
                counters[3] = counters[1];
                counters[0] = 0;
                counters[1] = 0;
                Initialize();
                samplingStart -= samplingInterval;
            }
            else if (timeElapsed > samplingInterval)
            {
                totalCount = counters[0] + counters[2];
                exceptionCount = counters[1] + counters[3];

                Int64 temp = counters[0];
                counters[0] = counters[2];
                counters[2] = temp;

                temp = counters[1];
                counters[1] = counters[3];
                counters[3] = temp;
            }
            else
            {
                totalCount = counters[0] + counters[2];
                exceptionCount = counters[1] + counters[3];
            }
        }

    }
}
