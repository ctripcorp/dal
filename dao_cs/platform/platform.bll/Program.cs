using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Data;
using System.Data.SqlClient;
//using platform.dao.log;
using System.ComponentModel;
using System.Text.RegularExpressions;
using RDSys.Tools.ConfigGen.Core;

namespace platform.bll
{
    class Program
    {
        static void Main(string[] args)
        {
            string saveFolder = Consts.DefaultConfigFolder;
            ConfigGenEngine engine = new ConfigGenEngine(@"E:\codes\xuntu_pto_ddxt");
            saveFolder = engine.GenerateAllConfigs(new ConfigEnv("dev", "sh", "iis6","net4", "dev") , saveFolder);


            //FileInfo f = new FileInfo("E:/eclipse/workspace");

            //Console.WriteLine(f.DirectoryName);

            //foreach (string filePath in Directory.GetFiles(@"E:\ConfigGen\FlightConfigDemo", "*.xml"))
            //{
            //    Console.WriteLine(filePath);
            //}

            //AddPomFileToSlnFile("E:/test.sln", "xxx.xml");

            //string addText = string.Format("EndProject\r\nProject(\"{{2150E333-8FDC-42A3-9474-1A3956D46DE8}}\") = \"Solution Items\", \"Solution Items\", \"{0}\"\r\n\tProjectSection(SolutionItems) = preProject\r\n\t\t{1} = {2}\r\n\tEndProjectSection\r\nEndProject", 1, 2, 3);

            //BackgroundWorker bw = new BackgroundWorker();

            //bw.WorkerReportsProgress = true;
            //bw.WorkerSupportsCancellation = true;
            //bw.DoWork += new DoWorkEventHandler(bw_DoWork);
            //bw.ProgressChanged += new ProgressChangedEventHandler(bw_ProgressChanged);
            //bw.RunWorkerCompleted += new RunWorkerCompletedEventHandler(bw_RunWorkerCompleted);

            //bw.RunWorkerAsync();

            //PersonDAO person = new PersonDAO();

            //Person p = person.FetchByPk<Person>(37);

            //if (null != p)
            //{
            //    Console.WriteLine(p.ID);
            //    Console.WriteLine(p.Address);
            //    Console.WriteLine(p.Name);
            //    Console.WriteLine(p.Telephone);
            //    Console.WriteLine(p.Age);
            //    Console.WriteLine(p.Gender);
            //    Console.WriteLine(p.Birth.Value.ToString());
            //}

            //while (Console.ReadKey().Key != ConsoleKey.Escape)
            //{

            //    using (IDataReader reader = person.GetAddrNameByPk(21))
            //    {
            //        while (reader.Read())
            //        {
            //            Console.WriteLine(reader["Address"]);
            //            Console.WriteLine(reader["Name"]);

            //        }
            //    }
            //}
            //Console.Read();

        }

        static void bw_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (null != e.Error)
            {
                Console.WriteLine(e.Error.Message);
            }
        }

        static void bw_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            
        }

        static void bw_DoWork(object sender, DoWorkEventArgs e)
        {
            throw new NotImplementedException("xxx");
        }

        /// <summary>
        /// Add pom.xml to sln file
        /// </summary>
        /// <param name="slnFile"></param>
        /// <param name="pomFileName"></param>
        /// <returns></returns>
        public static bool AddPomFileToSlnFile(string slnFile, string pomFileName = "pom.xml")
        {
            try
            {
                Encoding encoding = GetFileEncoding(slnFile);
                string projFileContent = File.ReadAllText(slnFile, encoding);
                string lowerProjFileContent = projFileContent.ToLower();
                if (lowerProjFileContent.Contains("2150e333-8fdc-42a3-9474-1a3956d46de8"))
                {
                    if (lowerProjFileContent.Contains(pomFileName.ToLower()))
                    {
                        return true;
                    }
                    else
                    {
                        int solutionItemStartIndex = lowerProjFileContent.IndexOf("2150e333-8fdc-42a3-9474-1a3956d46de8");
                        string newProjFileContent = projFileContent.Substring(solutionItemStartIndex).ToLower();
                        int projectSectionIndex = newProjFileContent.IndexOf("endprojectsection");
                        string newText = string.Format("{0}{1}{2}",
                            projFileContent.Substring(0, solutionItemStartIndex + projectSectionIndex),
                            string.Format("\t{0} = {1}\r\n\t", pomFileName, pomFileName),
                            projFileContent.Substring(solutionItemStartIndex + projectSectionIndex));
                        File.WriteAllText(slnFile, newText, encoding);
                        return true;
                    }
                }
                else if (projFileContent.Contains("EndProject"))
                {
                    Regex regex = new Regex("EndProject");
                    string firstGuid = Guid.NewGuid().ToString();
                    string addText = string.Format("EndProject\r\nProject(\"{2150E333-8FDC-42A3-9474-1A3956D46DE8}\") = \"Solution Items\", \"Solution Items\", \"{{{0}}}\"\r\n\tProjectSection(SolutionItems) = preProject\r\n\t\t{1} = {2}\r\n\tEndProjectSection\r\nEndProject", firstGuid, pomFileName, pomFileName);
                    string newText = regex.Replace(projFileContent, addText, 1);
                    File.WriteAllText(slnFile, newText, encoding);
                    return true;
                }
                else { return false; }
            }
            catch
            {
                return false;
            }
        }

        public static Encoding GetFileEncoding(string fileName)
        {
            using (StreamReader r = new StreamReader(fileName, true))
            {
                Encoding e = r.CurrentEncoding;
                return e;
            }
        }


    }
}
