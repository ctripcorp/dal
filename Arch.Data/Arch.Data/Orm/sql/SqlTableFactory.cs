using System;
using System.Collections.Concurrent;
using System.Reflection;

namespace Arch.Data.Orm.sql
{
    public class SqlTableFactory
    {
        private SqlTableFactory() { }

        private static SqlTableFactory instance = new SqlTableFactory();

        public static SqlTableFactory Instance { get { return instance; } }

        private readonly ConcurrentDictionary<String, SqlTable> cache = new ConcurrentDictionary<String, SqlTable>();

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public SqlTable Build()
        {
            return cache.GetOrAdd("1_default_class", key => new SqlTable(null, String.Empty, String.Empty));
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public SqlTable Build(Type type)
        {
            return cache.GetOrAdd(type.FullName, key => BuildTable(type));
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="prefixName"></param>
        /// <param name="type"></param>
        /// <returns></returns>
        public SqlTable Build(String prefixName, Type type)
        {
            String fullName = String.Concat(prefixName, type.FullName);
            return cache.GetOrAdd(fullName, key => BuildTable(type));
        }

        private SqlTable BuildTable(Type type)
        {
            SqlTable table;

            if (!type.IsDefined(typeof(TableAttribute), false))
            {
                if (!type.IsDefined(typeof(SPResultAttribute), false))
                    throw new DalException("No TableAttribute nor SPResultAttribute found on " + type.FullName);
                table = new SqlTable(type, null, null);
            }
            else
            {
                var tableAttribute = (TableAttribute)type.GetCustomAttributes(typeof(TableAttribute), false)[0];
                String name = tableAttribute.Name;
                String schema = tableAttribute.Schema;

                if (String.IsNullOrEmpty(name)) name = type.Name;
                table = new SqlTable(type, name, schema);
            }

            ProcessMaps(type, table);
            ProcessFields(type, table);
            ProcessProperties(type, table);
            //ProcessMethods(type, table);
            return table;
        }

        private void ProcessMaps(Type type, SqlTable table)
        {
            if (!type.IsDefined(typeof(MapAttribute), false)) return;
            var attributes = type.GetCustomAttributes(typeof(MapAttribute), false);

            foreach (MapAttribute attribute in attributes)
            {
                IDataBridge dataBridge;
                var flags = BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic;
                MemberInfo member = type.GetProperty(attribute.Field, flags);

                if (member != null)
                {
                    dataBridge = new PropertyBridge((PropertyInfo)member);
                }
                else
                {
                    member = type.GetField(attribute.Field, flags);

                    if (member != null)
                    {
                        dataBridge = new FieldBridge((FieldInfo)member);
                    }
                    else
                    {
                        throw new DalException(String.Format("Member {0} not found in class {1}", attribute.Field, type.Name));
                    }
                }

                if (String.IsNullOrEmpty(attribute.Name)) attribute.Name = member.Name;

                var column = new SqlColumn(table, attribute.Name, dataBridge)
                {
                    Alias = String.IsNullOrEmpty(attribute.Alias) ? attribute.Field : attribute.Alias,
                    IsID = attribute.ID,
                    IsPK = attribute.PK,
                    IsOutPut = attribute.OutPut,
                    IsReturnValue = attribute.ReturnValue,
                    Length = attribute.Length
                };

                table.Add(column);
            }
        }

        private void ProcessProperties(Type type, SqlTable table)
        {
            var properties = type.GetProperties(BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public);
            if (properties == null) return;

            foreach (var property in properties)
            {
                if (!property.IsDefined(typeof(ColumnAttribute), false)) continue;

                var columnAttribute = (ColumnAttribute)property.GetCustomAttributes(typeof(ColumnAttribute), false)[0];
                if (String.IsNullOrEmpty(columnAttribute.Name)) columnAttribute.Name = property.Name;

                var propertyBridge = new PropertyBridge(property);
                var column = new SqlColumn(table, columnAttribute.Name, propertyBridge)
                {
                    Alias = String.IsNullOrEmpty(columnAttribute.Alias) ? property.Name : columnAttribute.Alias,
                    ColumnType = columnAttribute.NullableColumnType,
                    DefaultValue = columnAttribute.DefaultValue,
                    IsID = property.IsDefined(typeof(IDAttribute), false),
                    IsPK = property.IsDefined(typeof(PKAttribute), false),
                    IsOutPut = property.IsDefined(typeof(OutPutAttribute), false),
                    IsReturnValue = property.IsDefined(typeof(RetrunValueAttribute), false),
                    IsVersionLock = property.IsDefined(typeof(VersionLockAttribute), false),
                    IsIgnored = property.IsDefined(typeof(IgnoredAttribute), false),
                    Length = columnAttribute.Length
                };

                if (property.IsDefined(typeof(DateTimeLockAttribute), false))
                {
                    var datetimeAttribute = (DateTimeLockAttribute)property.GetCustomAttributes(typeof(DateTimeLockAttribute), false)[0];
                    column.IsTimestampLock = true;
                    column.TimestampExpression = datetimeAttribute.Expression;
                }

                table.Add(column);
            }
        }

        private void ProcessFields(Type type, SqlTable table)
        {
            var fields = type.GetFields(BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public);
            if (fields == null) return;

            foreach (var field in fields)
            {
                if (!field.IsDefined(typeof(ColumnAttribute), false)) continue;
                var columnAttribute = (ColumnAttribute)field.GetCustomAttributes(typeof(ColumnAttribute), false)[0];
                if (String.IsNullOrEmpty(columnAttribute.Name)) columnAttribute.Name = field.Name;

                var fieldBridge = new FieldBridge(field);
                var column = new SqlColumn(table, columnAttribute.Name, fieldBridge)
                {
                    Alias = String.IsNullOrEmpty(columnAttribute.Alias) ? field.Name : columnAttribute.Alias,
                    IsID = field.IsDefined(typeof(IDAttribute), false),
                    IsPK = field.IsDefined(typeof(PKAttribute), false),
                    IsOutPut = field.IsDefined(typeof(OutPutAttribute), false),
                    IsReturnValue = field.IsDefined(typeof(RetrunValueAttribute), false),
                    Length = columnAttribute.Length
                };

                table.Add(column);
            }
        }

        private void ProcessMethods(Type type, SqlTable table)
        {
            var methods = type.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
            if (methods == null) return;

            foreach (var method in methods)
            {
                if (!method.IsDefined(typeof(TriggerAttribute), false)) continue;

                var triggerAttribute = (TriggerAttribute)method.GetCustomAttributes(typeof(TriggerAttribute), false)[0];
                var trigger = new SqlTrigger(method, triggerAttribute.Timing);
                table.AddTrigger(trigger);
            }
        }

    }
}
