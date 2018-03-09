using Arch.Data.Common.Constant;
using System;
using System.Collections;
using System.Text;

namespace Arch.Data.DbEngine.MarkDown
{
    class MarkUpInfo
    {
        public Boolean PreMarkUp { get; set; }

        public DateTime MarkDownTime { get; set; }

        public Int32 CurrentMarkUpIndex { get; set; }

        public BitArray MarkUpArray { get; set; }

        public Int32[] MarkUpSchedules { get; set; }

        public Int32[] MarkUpSuccess { get; set; }

        public Int32[] MarkUpFail { get; set; }

        public Int32 CurrentMarkUpSchedule { get; set; }

        /// <summary>
        /// 总的数据访问数
        /// </summary>
        public Int32 CurrentBatch { get; set; }

        public override String ToString()
        {
            return new StringBuilder().AppendFormat("PreMarkUp:{0},", PreMarkUp)
                .AppendFormat("MarkDownTime:{0},", MarkDownTime)
                .AppendFormat("CurrentMarkUpIndex:{0},", CurrentMarkUpIndex)
                .AppendFormat("MarkUpArray:{0},", String.Join(",", MarkUpArray))
                .ToString();
        }

        public static BitArray InitMarkUpArray(Int32 markUpPercent)
        {
            BitArray array = new BitArray(Constants.MarkUpReferCount);
            Int32 threshold = Constants.MarkUpReferCount - markUpPercent;
            for (Int32 i = 0; i < Constants.MarkUpReferCount; i++)
            {
                array[i] = i >= threshold;
            }
            return array;
        }

    }
}
