using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.enums;
using System.IO;

namespace platform.dao.log
{
    internal class FileLoggerAdapter : AbstractLoggerAdapter
    {

         private static FileLoggerAdapter instance = new FileLoggerAdapter();

         private string m_FileNamePattern;
         private object m_SyncLock = new object();
         private StreamWriter m_Writer;
         private int m_FileSize;

         private FileLoggerAdapter()
        {
        }

         public static FileLoggerAdapter GetInstance(string name, LogLevel level)
        {
            instance.Init(name, level);
            return instance;
        }

         /// <summary>
         /// 文本文件名称
         /// </summary>
         public string FileNamePattern
         {
             get { return m_FileNamePattern; }
         }

         /// <summary>
         /// 初始化
         /// </summary>
         /// <param name="config">配置项</param>
         public override void Init(string name, LogLevel level)
         {
             base.Init(name, level);
             //parse setting
             m_FileNamePattern = "{0:yyyy_MM_dd}{1}.log";
             m_FileSize = 2;
         }

         private StreamWriter CreateStreamWriter()
         {
             DateTime now = DateTime.Now;

             StreamWriter result;
             while (true)
             {
                 string fileName = Path.GetFullPath(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, string.Format(m_FileNamePattern, now, string.Empty)));
                 try
                 {
                     int i = 1;
                     while (File.Exists(fileName) && (new FileInfo(fileName).Length > (long)m_FileSize * 1024 * 1024))
                     {
                         fileName = Path.GetFullPath(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, string.Format(m_FileNamePattern, now, string.Format("_{0}",i))));
                         i++;
                     }
                     result = new StreamWriter(fileName, true, Encoding.UTF8, 0x1000);
                     result.AutoFlush = true;
                     break;
                 }
                 catch (IOException)
                 {
                     //无法打开时
                 }
                 now = now.AddSeconds(1);
             }

             return result;
         }

         protected  void WriteLog(string data)
         {
             lock (m_SyncLock)
             {
                 if (m_Writer == null)
                 {
                     m_Writer = CreateStreamWriter();
                 }
                 else if (m_Writer.BaseStream.Position > (long)m_FileSize * 1024 * 1024)
                 {
                     m_Writer.Close();
                     m_Writer.Dispose();
                     m_Writer = CreateStreamWriter();
                 }
                 m_Writer.WriteLine(data);
             }
         }

         protected override void Dispose(bool isDisposing)
         {
             if (isDisposing)
             {
                 if (m_Writer != null)
                 {
                     m_Writer.Close();
                     m_Writer.Dispose();
                     m_Writer = null;
                 }
             }
             base.Dispose(isDisposing);
         }

        public override void Trace(string message)
        {
            if (this.level <= LogLevel.Trace)
            {
                WriteLog(FormatLogMessage("Trace", message));
            }
        }

        public override void Debug(string message)
        {
            if (this.level <= LogLevel.Debug)
            {
                WriteLog(FormatLogMessage("Debug", message));
            }
        }

        public override void Info(string message)
        {
            if (this.level <= LogLevel.Info)
            {
                WriteLog(FormatLogMessage("Info", message));
            }
        }

        public override void Warn(string message)
        {
            if (this.level <= LogLevel.Warn)
            {
                WriteLog(FormatLogMessage("Warn", message));
            }
        }

        public override void Error(string message)
        {
            if (this.level <= LogLevel.Error)
            {
                WriteLog(FormatLogMessage("Error", message));
            }
        }

        public override void Fatal(string message)
        {
            if (this.level <= LogLevel.Fatal)
            {
                WriteLog(FormatLogMessage("Fatal", message));
            }
        }

    }
}
