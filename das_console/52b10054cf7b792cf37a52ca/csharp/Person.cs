using System;
using platform.dao.orm.attribute;

namespace com.ctrip.flight.intl.platform
{
    [Serializable]
    [Table(Name="Person")]
    public class Person
    {
                /// <summary>
        /// ID
        /// </summary>
        [Column(Name="ID"), PrimaryKey]
        public int ID { get; set; }
                /// <summary>
        /// Address
        /// </summary>
        [Column(Name="Address")]
        public varchar Address { get; set; }
                /// <summary>
        /// Name
        /// </summary>
        [Column(Name="Name")]
        public varchar Name { get; set; }
                /// <summary>
        /// Telephone
        /// </summary>
        [Column(Name="Telephone")]
        public varchar Telephone { get; set; }
                /// <summary>
        /// Age
        /// </summary>
        [Column(Name="Age")]
        public int? Age { get; set; }
                /// <summary>
        /// Gender
        /// </summary>
        [Column(Name="Gender")]
        public int? Gender { get; set; }
                /// <summary>
        /// Birth
        /// </summary>
        [Column(Name="Birth")]
        public datetime? Birth { get; set; }
            }
}