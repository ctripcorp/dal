using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    /// <summary>
    /// 数据库集配置集合
    /// </summary>
    [ConfigurationCollection(typeof(DatabaseSetElement), AddItemName = "databaseSet")]
    public sealed class DatabaseSetElementCollection : ConfigurationElementCollection
    {
        #region static fields
        /// <summary>
        /// 属性集合
        /// </summary>
        private static readonly ConfigurationPropertyCollection s_Properties;

        /// <summary>
        /// 静态构造
        /// </summary>
        static DatabaseSetElementCollection()
        {
            s_Properties = new ConfigurationPropertyCollection();
        }

        #endregion

        #region collection operator

        protected override String ElementName
        {
            get { return "databaseSet"; }
        }

        public new DatabaseSetElement this[String name]
        {
            get { return (DatabaseSetElement)base.BaseGet(name); }
        }

        public DatabaseSetElement this[Int32 index]
        {
            get { return (DatabaseSetElement)base.BaseGet(index); }
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

        public void Add(DatabaseSetElement element)
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
            return new DatabaseSetElement();
        }

        protected override Object GetElementKey(ConfigurationElement element)
        {
            return ((DatabaseSetElement)element).Name;
        }

        public Int32 IndexOf(DatabaseSetElement element)
        {
            return base.BaseIndexOf(element);
        }

        public void Remove(DatabaseSetElement element)
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
