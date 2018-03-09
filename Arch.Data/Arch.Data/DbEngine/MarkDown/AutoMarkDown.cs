using Arch.Data.Common.Constant;
using Arch.Data.Common.Enums;
using Arch.Data.Common.Util;
using Arch.Data.Common.Vi;
using System;
using System.Collections.Generic;
using System.Linq;

namespace Arch.Data.DbEngine.MarkDown
{
    public class AutoMarkDown
    {
        private static Dictionary<String, MarkDownEnums> markDownDatabases = new Dictionary<String, MarkDownEnums>();
        private static Dictionary<String, MarkUpInfo> markingUpDatabases = new Dictionary<String, MarkUpInfo>();
        private static Object lockObj = new Object();

        private static IMarkDownBean markdownBean
        {
            get { return BeanManager.GetMarkDownBean(); }
        }

        public static String AutoMarkDownDB
        {
            get
            {
                lock (lockObj)
                {
                    return String.Join(",", markDownDatabases.Where(item => item.Value != MarkDownEnums.ManualMarkDown).Select(item => item.Key));
                }
            }
        }

        public static string MarkDownDB
        {
            get
            {
                lock (lockObj)
                {
                    return String.Join(",", markDownDatabases.Where(item => item.Value != MarkDownEnums.FakeAutoMarkDown).Select(item => item.Key));
                }
            }
            set
            {
                lock (lockObj)
                {
                    markDownDatabases.Clear();
                    foreach (var s in SplitUtil.SplitAsStringIgnoreEmpty(value))
                    {
                        //todo:distinguish auto and manual
                        markDownDatabases[s] = MarkDownEnums.ManualMarkDown;
                    }
                }
            }
        }

        /// <summary>
        /// 自动mark down,怎么区分？
        /// </summary>
        /// <param name="allInOneKey"></param>
        public static void AutoMarkDownADatabase(String allInOneKey)
        {
            lock (lockObj)
            {
                markDownDatabases[allInOneKey] = markdownBean.EnableAutoMarkDown ? MarkDownEnums.AutoMarkDown : MarkDownEnums.FakeAutoMarkDown;

                var schedules = SplitUtil.SplitAsInt32(markdownBean.AutoMarkUpSchedule).ToArray();
                //排序
                Array.Sort<Int32>(schedules);
                markingUpDatabases[allInOneKey] = new MarkUpInfo()
                {
                    PreMarkUp = false,
                    MarkDownTime = DateTime.Now,
                    CurrentMarkUpIndex = 0,
                    MarkUpSchedules = schedules,
                    MarkUpSuccess = new Int32[schedules.Length],
                    MarkUpFail = new Int32[schedules.Length],
                    CurrentMarkUpSchedule = 0,
                    CurrentBatch = 1,
                    MarkUpArray = MarkUpInfo.InitMarkUpArray(schedules[0])
                };

            }
        }

        public static void AutoMarkUpMonitor(String allInOneKey, Boolean isAbnormalException)
        {
            if (markingUpDatabases.ContainsKey(allInOneKey) && markdownBean.AutoMarkUpBatches > 0)
            {
                lock (lockObj)
                {
                    if (markingUpDatabases.ContainsKey(allInOneKey) && markdownBean.AutoMarkUpBatches > 0)
                    {
                        if (isAbnormalException) markingUpDatabases[allInOneKey].MarkUpFail[markingUpDatabases[allInOneKey].CurrentMarkUpSchedule]++;
                        else markingUpDatabases[allInOneKey].MarkUpSuccess[markingUpDatabases[allInOneKey].CurrentMarkUpSchedule]++;

                        if (markingUpDatabases[allInOneKey].CurrentBatch > markdownBean.AutoMarkUpBatches)
                        {
                            markingUpDatabases[allInOneKey].CurrentBatch = 1;
                            //Pre Mark Up已经结束，如果监控到的状态良好，则取消Mark Down，否则回到上个阶段，并清空Pre Mark Up的记录
                            if (markingUpDatabases[allInOneKey].MarkUpFail[markingUpDatabases[allInOneKey].CurrentMarkUpSchedule] > 0)
                            {
                                markingUpDatabases[allInOneKey].PreMarkUp = false;
                                markingUpDatabases[allInOneKey].MarkDownTime = DateTime.Now;
                                var markupMetrics = new MarkUpMetrics()
                                {
                                    AllInOneKey = allInOneKey,
                                    Batches = markdownBean.AutoMarkUpBatches,
                                    MarkUpDelay = markdownBean.AutoMarkUpDelay,
                                    SuccessCount = markingUpDatabases[allInOneKey].MarkUpSuccess.Sum(),
                                    Success = false
                                };

                                LogManager.Logger.MetricsMarkup(markupMetrics);
                                markingUpDatabases[allInOneKey].MarkUpSuccess[markingUpDatabases[allInOneKey].CurrentMarkUpSchedule] = 0;
                                markingUpDatabases[allInOneKey].MarkUpFail[markingUpDatabases[allInOneKey].CurrentMarkUpSchedule] = 0;
                                if (markingUpDatabases[allInOneKey].CurrentMarkUpSchedule > 0)
                                    markingUpDatabases[allInOneKey].CurrentMarkUpSchedule--;
                            }
                            else
                            {
                                //如果部分完成，进入下一阶段
                                if (markingUpDatabases[allInOneKey].MarkUpSchedules.Length - 1 > markingUpDatabases[allInOneKey].CurrentMarkUpSchedule)
                                {
                                    markingUpDatabases[allInOneKey].CurrentMarkUpSchedule++;
                                    markingUpDatabases[allInOneKey].MarkUpArray = MarkUpInfo.InitMarkUpArray(markingUpDatabases[allInOneKey].MarkUpSchedules[markingUpDatabases[allInOneKey].CurrentMarkUpSchedule]);
                                    markingUpDatabases[allInOneKey].CurrentMarkUpIndex = 0;
                                }
                                else
                                {
                                    var markupMetrics = new MarkUpMetrics()
                                    {
                                        AllInOneKey = allInOneKey,
                                        Batches = markdownBean.AutoMarkUpBatches,
                                        MarkUpDelay = markdownBean.AutoMarkUpDelay,
                                        SuccessCount = markingUpDatabases[allInOneKey].MarkUpSuccess.Sum(),
                                        Success = true
                                    };

                                    LogManager.Logger.MetricsMarkup(markupMetrics);
                                    //如果全部状态已完成，移除Mark Down数据库，情况Mark Up监控
                                    markDownDatabases.Remove(allInOneKey);
                                    markingUpDatabases.Remove(allInOneKey);
                                }
                            }
                        }
                    }
                }
            }
        }

        /// <summary>
        /// 当前all in one key是否在Mark Up过程中
        /// </summary>
        /// <param name="allInOneKey"></param>
        /// <returns></returns>
        public static Boolean DatabaseMarkingUp(String allInOneKey)
        {
            Boolean result = false;
            if (markingUpDatabases.ContainsKey(allInOneKey))
            {
                lock (lockObj)
                {
                    if (markingUpDatabases.ContainsKey(allInOneKey))
                    {
                        result = markingUpDatabases[allInOneKey].PreMarkUp;
                    }
                }
            }
            return result;
        }

        /// <summary>
        /// 当前数据库是否被Mark Down了，如果在Pre Mark Up阶段，且当前请求满足放行条件，则仍然返回false
        /// </summary>
        /// <param name="allInOneKey"></param>
        /// <returns></returns>
        public static Boolean DatabaseMarkedDown(String allInOneKey, String logicDbName)
        {
            if (markDownDatabases.ContainsKey(allInOneKey))
            {
                lock (lockObj)
                {
                    if (markDownDatabases.ContainsKey(allInOneKey))
                    {
                        if (markingUpDatabases.ContainsKey(allInOneKey) && (markingUpDatabases[allInOneKey].PreMarkUp
                            || (DateTime.Now - markingUpDatabases[allInOneKey].MarkDownTime).TotalSeconds >= markdownBean.AutoMarkUpDelay))
                        {
                            if (!markdownBean.EnableAutoMarkDown || markdownBean.AutoMarkUpBatches <= 0)
                            {
                                var markupMetrics = new MarkUpMetrics()
                                {
                                    AllInOneKey = allInOneKey,
                                    Batches = markdownBean.AutoMarkUpBatches,
                                    MarkUpDelay = markdownBean.AutoMarkUpDelay,
                                    SuccessCount = 0,
                                    Success = true
                                };

                                LogManager.Logger.MetricsMarkup(markupMetrics);
                                markDownDatabases.Remove(allInOneKey);
                                markingUpDatabases.Remove(allInOneKey);
                                return false;
                            }
                            markingUpDatabases[allInOneKey].PreMarkUp = true;
                            try
                            {
                                //Let it pass
                                if (markingUpDatabases[allInOneKey].MarkUpArray[markingUpDatabases[allInOneKey].CurrentMarkUpIndex]) return false;
                            }
                            finally
                            {
                                //一轮放量结束，进入下一轮放量
                                if (markingUpDatabases[allInOneKey].CurrentMarkUpIndex >= (Constants.MarkUpReferCount - 1) &&
                                    markingUpDatabases[allInOneKey].CurrentBatch <= markdownBean.AutoMarkUpBatches)
                                {
                                    markingUpDatabases[allInOneKey].CurrentBatch++;
                                }
                                markingUpDatabases[allInOneKey].CurrentMarkUpIndex = (markingUpDatabases[allInOneKey].CurrentMarkUpIndex + 1) % Constants.MarkUpReferCount;
                            }
                        }
                        return markDownDatabases.ContainsKey(allInOneKey) && markDownDatabases[allInOneKey] != MarkDownEnums.FakeAutoMarkDown;
                    }
                }
            }
            return false;
        }

    }
}
