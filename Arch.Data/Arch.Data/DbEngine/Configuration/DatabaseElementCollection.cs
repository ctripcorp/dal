using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    /// <summary>
    /// 数据库集集合
    /// </summary>
    [ConfigurationCollection(typeof(DatabaseElement))]
    public sealed class DatabaseElementCollection : ConfigurationElementCollection
    {
        #region static

        /// <summary>
        /// 属性集合
        /// </summary>
        private static readonly ConfigurationPropertyCollection s_Properties;

        /// <summary>
        /// 静态构造
        /// </summary>
        static DatabaseElementCollection()
        {
            s_Properties = new ConfigurationPropertyCollection();
        }

        #endregion

        #region collection operator

        public new DatabaseElement this[String name]
        {
            get { return (DatabaseElement)base.BaseGet(name); }
        }

        public DatabaseElement this[Int32 index]
        {
            get { return (DatabaseElement)base.BaseGet(index); }
            set
            {
                if (base.BaseGet(index) != null)
                {
                    base.BaseRemoveAt(index);
                }

                BaseAdd(index, value);
            }
        }

        protected override ConfigurationPropertyCollection Properties
        {
            get { return s_Properties; }
        }

        public void Add(DatabaseElement element)
        {
            BaseAdd(element);
        }

        protected override void BaseAdd(Int32 index, ConfigurationElement element)
        {
            if (index == -1)
            {
                base.BaseAdd(element, false);
            }
            else
            {
                base.BaseAdd(index, element);
            }
        }

        public void Clear()
        {
            base.BaseClear();
        }

        protected override ConfigurationElement CreateNewElement()
        {
            return new DatabaseElement();
        }

        protected override Object GetElementKey(ConfigurationElement element)
        {
            return ((DatabaseElement)element).Name;
        }

        public Int32 IndexOf(DatabaseElement element)
        {
            return base.BaseIndexOf(element);
        }

        public void Remove(DatabaseElement element)
        {
            if (base.BaseIndexOf(element) >= 0)
            {
                base.BaseRemove(element.Name);
            }
        }

        public void Remove(String name)
        {
            base.BaseRemove(name);
        }

        public void RemoveAt(Int32 index)
        {
            base.BaseRemoveAt(index);
        }

        #endregion
    }
}
