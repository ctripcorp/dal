using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.log
{
    public class ConsoleLoggerAdapter : AbstractLoggerAdapter
    {

        private string FormatLogMessage(string level, string message)
        {
            return string.Format("[{0}]----[{1}]----{2}",level, DateTime.Now, message);
        }

        public override void Trace(string message)
        {
            Console.WriteLine(FormatLogMessage("Trace", message));
        }

        public override void Debug(string message)
        {
            Console.WriteLine(FormatLogMessage("Debug", message));
        }

        public override void Info(string message)
        {
            Console.WriteLine(FormatLogMessage("Info", message));
        }

        public override void Warn(string message)
        {
            Console.WriteLine(FormatLogMessage("Warn", message));
        }

        public override void Error(string message)
        {
            Console.WriteLine(FormatLogMessage("Error", message));
        }

        public override void Fatal(string message)
        {
            Console.WriteLine(FormatLogMessage("Fatal", message));
        }

    }
}
