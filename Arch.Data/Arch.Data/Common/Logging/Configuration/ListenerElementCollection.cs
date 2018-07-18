using System;
using System.Configuration;

namespace Arch.Data.Common.Logging.Configuration
{
    /// <summary>
    /// 日志侦听器集合
    /// </summary>
    [ConfigurationCollection(typeof(ListenerElement))]
    public sealed class ListenerElementCollection : ConfigurationElementCollection
    {
        #region collection operator

        public new ListenerElement this[String name]
        {
            get { return (ListenerElement)BaseGet(name); }
        }

        public ListenerElement this[Int32 index]
        {
            get { return (ListenerElement)BaseGet(index); }
            set
            {
                if (BaseGet(index) != null)
                    BaseRemoveAt(index);

                BaseAdd(index, value);
            }
        }

        public void Add(ListenerElement element)
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
            BaseClear();
        }

        protected override ConfigurationElement CreateNewElement()
        {
            return new ListenerElement();
        }

        protected override Object GetElementKey(ConfigurationElement element)
        {
            return ((ListenerElement)element).Name;
        }

        public Int32 IndexOf(ListenerElement element)
        {
            return BaseIndexOf(element);
        }

        public void Remove(ListenerElement element)
        {
            if (BaseIndexOf(element) >= 0)
                BaseRemove(element.Name);
        }

        public void Remove(String name)
        {
            BaseRemove(name);
        }

        public void RemoveAt(Int32 index)
        {
            BaseRemoveAt(index);
        }

        #region Sampling

        private const String SamplingProperty = "sampling";

        [ConfigurationProperty(SamplingProperty)]
        public String Sampling
        {
            get { return base[SamplingProperty] as String; }
        }

        #endregion

        #region Encrypt

        private const String EncryptProperty = "encrypt";

        [ConfigurationProperty(EncryptProperty)]
        public String Encrypt
        {
            get { return (String)base[EncryptProperty]; }
        }

        #endregion

        #region Switch

        private const String SwitchProperty = "switch";
        private const String SwitchDefaultValue = "on";

        [ConfigurationProperty(SwitchProperty, DefaultValue = SwitchDefaultValue)]
        public String Switch
        {
            get { return base[SwitchProperty] as String; }
        }

        #endregion

        #region ConcurrentCapacity

        private const String ConcurrentCapacityProperty = "concurrentCapacity";
        public const Int32 ConcurrentCapacityDefaultValue = 1000;

        [ConfigurationProperty(ConcurrentCapacityProperty)]
        private String concurrentCapacity
        {
            get { return base[ConcurrentCapacityProperty] as String; }
        }

        public Int32 ConcurrentCapacity
        {
            get
            {
                Int32 value = ConcurrentCapacityDefaultValue;
                String capacity = concurrentCapacity;

                if (!String.IsNullOrEmpty(capacity))
                {
                    if (!Int32.TryParse(capacity, out value))
                        value = ConcurrentCapacityDefaultValue;
                }

                return value;
            }
        }

        #endregion

        #region Cat Switch

        private const String CatProperty = "cat";
        private const String CatDefaultValue = "on";

        [ConfigurationProperty(CatProperty, DefaultValue = CatDefaultValue)]
        public String Cat
        {
            get { return base[CatProperty] as String; }
        }

        #endregion

        #region Cat Parameter Switch

        private const String SensitiveProperty = "sensitive";
        private const String SensitiveDefaultValue = "off";

        [ConfigurationProperty(SensitiveProperty, DefaultValue = SensitiveDefaultValue)]
        public String Sensitive
        {
            get { return base[SensitiveProperty] as String; }
        }

        #endregion

        #endregion
    }
}
