using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using platform.dao.orm.attribute;

namespace platform.demo.Entity
{
    [Serializable]
    [Table(Name="Person")]
    public class Person
    {
        /// <summary>
        /// ID
        /// </summary>
        [Column(Name="ID"), AutoIncrement, PrimaryKey]
        public int ID { get; set; }

        /// <summary>
        /// Address
        /// </summary>
        [Column(Name = "Address", Length = 50), Nullable]
        public string Address { get; set; }

        /// <summary>
        /// Name
        /// </summary>
        [Column(Name = "Name", Length = 50), Nullable]
        public string Name { get; set; }

        /// <summary>
        /// Telephone
        /// </summary>
        [Column(Name = "Telephone", Length = 50), Nullable]
        public string Telephone { get; set; }

        /// <summary>
        /// Age
        /// </summary>
        [Column(Name = "Age", Length = 4), Nullable]
        public int? Age { get; set; }

        /// <summary>
        /// Gender
        /// </summary>
        [Column(Name = "Gender", Length = 4), Nullable]
        public int? Gender { get; set; }

        /// <summary>
        /// Birth
        /// </summary>
        [Column(Name = "Birth", Length = 8), Nullable]
        public DateTime? Birth { get; set; }

    }
}