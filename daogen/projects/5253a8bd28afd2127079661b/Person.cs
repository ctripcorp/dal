using System;
using platform.dao.orm.attribute;

namespace platform.apptools.demo
{
    [Serializable]
    [Table(Name="Person")]
    public class Person
    {
        
        /// <summary>
        /// Name
        /// </summary>
        [Column(Name="Name")]
        public string Name { get; set; }
        
        /// <summary>
        /// Gender
        /// </summary>
        [Column(Name="Gender")]
        public int? Gender { get; set; }
        
        /// <summary>
        /// Age
        /// </summary>
        [Column(Name="Age")]
        public int? Age { get; set; }
        
        /// <summary>
        /// Telephone
        /// </summary>
        [Column(Name="Telephone")]
        public string Telephone { get; set; }
        
        /// <summary>
        /// Birth
        /// </summary>
        [Column(Name="Birth")]
        public DateTime? Birth { get; set; }
        
        /// <summary>
        /// Address
        /// </summary>
        [Column(Name="Address")]
        public string Address { get; set; }
        
        /// <summary>
        /// ID
        /// </summary>
        [Column(Name="ID"), PrimaryKey]
        public int? ID { get; set; }
        
    }
}