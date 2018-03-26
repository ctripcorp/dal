using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    /// <summary>
    /// 数据库提供者集合
    /// </summary>
    [ConfigurationCollection(typeof(DatabaseProviderElement))]
    public sealed class DatabaseProviderElementCollection : ConfigurationElementCollection
    {
        #region private static fields

        /// <summary>
        /// 属性集合
        /// </summary>
        private static readonly ConfigurationPropertyCollection s_Properties;

        /// <summary>
        /// 静态构造方法
        /// </summary>
        static DatabaseProviderElementCollection()
        {
            s_Properties = new ConfigurationPropertyCollection();
        }

        #endregion

        #region collection operator

        public new DatabaseProviderElement this[String name]
        {
            get { return (DatabaseProviderElement)base.BaseGet(name); }
        }

        public DatabaseProviderElement this[Int32 index]
        {
            get { return (DatabaseProviderElement)base.BaseGet(index); }
            set
            {
                if (base.BaseGet(index) != null)
                    base.BaseRemoveAt(index);
                BaseAdd(index, value);
            }
        }

        protected override ConfigurationPropertyCollection Properties
        {
            get { return s_Properties; }
        }

        public void Add(DatabaseProviderElement element)
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
            return new DatabaseProviderElement();
        }

        protected override Object GetElementKey(ConfigurationElement element)
        {
            return ((DatabaseProviderElement)element).Name;
        }

        public Int32 IndexOf(DatabaseProviderElement element)
        {
            return base.BaseIndexOf(element);
        }

        public void Remove(DatabaseProviderElement element)
        {
            if (base.BaseIndexOf(element) >= 0)
                base.BaseRemove(element.Name);
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
