using Arch.Data.DbEngine.Configuration;
using System;
using System.Collections.Generic;

namespace Arch.Data.DbEngine.Sharding
{
    class ShardingStrategyFactory
    {
        public static readonly ShardingStrategyFactory Instance = new ShardingStrategyFactory();

        private ShardingStrategyFactory() { }

        /// <summary>
        /// get shard strategy object via shard config
        /// </summary>
        /// <param name="element"></param>
        /// <returns></returns>
        public IShardingStrategy GetShardingStrategy(DatabaseSetElement element)
        {
            String shardingStrategy = element.ShardingStrategy;
            if (String.IsNullOrEmpty(shardingStrategy))
                return null;

            var shardconfs = shardingStrategy.Split(';');
            var config = new Dictionary<String, String>();

            foreach (var shardcnfg in shardconfs)
            {
                var param = shardcnfg.Split('=');
                if (param.Length != 2)
                    throw new ArgumentException("Sharding parameters invalid.");
                //will fix key issue(ignore case in the future)
                config.Add(param[0].Trim(), param[1].Trim());
            }

            String classname;
            if (!config.TryGetValue("class", out classname))
                throw new ArgumentException("Strategy invalid.");

            Type type = Type.GetType(classname);
            if (type == null)
                throw new ArgumentException("Strategy invalid.");

            try
            {
                var resultStrategy = Activator.CreateInstance(type) as IShardingStrategy;
                if (resultStrategy == null)
                {
                    throw new DalException("Strategy {0} didn't implement IShardingStrategy", classname);
                }
                else
                {
                    resultStrategy.SetShardConfig(config, element);
                    return resultStrategy;
                }

            }
            catch (Exception ex)
            {
                throw new DalException("Strategy invalid.", ex);
            }
        }

    }
}
