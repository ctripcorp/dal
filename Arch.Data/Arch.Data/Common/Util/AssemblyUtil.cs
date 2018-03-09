using System;
using System.IO;
using System.Reflection;

namespace Arch.Data.Common.Util
{
    class AssemblyUtil
    {
        public static Type GetTypeFromAssembly(String assemblyName, String typeName)
        {
            Type type = null;
            if (assemblyName == null || typeName == null)
                return type;

            try
            {
                if (File.Exists(assemblyName))
                {
                    Assembly asm = Assembly.LoadFrom(assemblyName);
                    type = asm.GetType(typeName);
                }
                return type;
            }
            catch
            {
                return type;
            }
        }
    }
}