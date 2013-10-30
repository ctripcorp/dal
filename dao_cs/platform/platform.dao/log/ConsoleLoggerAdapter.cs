using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;

namespace platform.dao.log
{
    internal class ConsoleLoggerAdapter : AbstractLoggerAdapter
    {

        private static ConsoleLoggerAdapter instance = new ConsoleLoggerAdapter();

        private ConsoleLoggerAdapter()
        {
        }

        public static ConsoleLoggerAdapter GetInstance(string name, LogLevel level)
        {
            instance.Init(name, level);
            return instance;
        }

        public override void Trace(string message)
        {
            if (this.level <= LogLevel.Trace)
            {
                Console.WriteLine(FormatLogMessage("Trace", message));
            }
        }

        public override void Debug(string message)
        {
            if (this.level <= LogLevel.Debug)
            {
                Console.WriteLine(FormatLogMessage("Debug", message));
            }
        }

        public override void Info(string message)
        {
            if (this.level <= LogLevel.Info)
            {
                Console.WriteLine(FormatLogMessage("Info", message));
            }
        }

        public override void Warn(string message)
        {
            if (this.level <= LogLevel.Warn)
            {
                Console.WriteLine(FormatLogMessage("Warn", message));
            }
        }

        public override void Error(string message)
        {
            if (this.level <= LogLevel.Error)
            {
                Console.WriteLine(FormatLogMessage("Error", message));
            }
        }

        public override void Fatal(string message)
        {
            if (this.level <= LogLevel.Fatal)
            {
                Console.WriteLine(FormatLogMessage("Fatal", message));
            }
        }

    }
}
