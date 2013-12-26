using System;
using platform.dao.orm.attribute;

namespace com.ctrip.flight.intl.engine
{
    [Serializable]
    [Table(Name="Task")]
    public class Task
    {
                /// <summary>
        /// TID
        /// </summary>
        [Column(Name="TID"), PrimaryKey]
        public int TID { get; set; }
                /// <summary>
        /// CompletionTime
        /// </summary>
        [Column(Name="CompletionTime")]
        public DateTime? CompletionTime { get; set; }
                /// <summary>
        /// Source
        /// </summary>
        [Column(Name="Source")]
        public string Source { get; set; }
                /// <summary>
        /// Status
        /// </summary>
        [Column(Name="Status")]
        public int Status { get; set; }
                /// <summary>
        /// Operator
        /// </summary>
        [Column(Name="Operator")]
        public string Operator { get; set; }
                /// <summary>
        /// ActualCompletionTime
        /// </summary>
        [Column(Name="ActualCompletionTime")]
        public DateTime? ActualCompletionTime { get; set; }
                /// <summary>
        /// Remark
        /// </summary>
        [Column(Name="Remark")]
        public string Remark { get; set; }
                /// <summary>
        /// Priority
        /// </summary>
        [Column(Name="Priority")]
        public int Priority { get; set; }
                /// <summary>
        /// Property
        /// </summary>
        [Column(Name="Property")]
        public int Property { get; set; }
                /// <summary>
        /// Title
        /// </summary>
        [Column(Name="Title")]
        public string Title { get; set; }
                /// <summary>
        /// Detail
        /// </summary>
        [Column(Name="Detail")]
        public string Detail { get; set; }
            }
}