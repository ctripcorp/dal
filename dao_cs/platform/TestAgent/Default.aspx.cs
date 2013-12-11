using System;
using System.Collections.Generic;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace TestAgent
{
    public partial class _Default : System.Web.UI.Page
    {
        static _Default()
        {
            CTimer.Run(SummaryInfo.GetMetrics, 1000);
        }

        protected void Page_Load(object sender, EventArgs e)
        {
            string queryType = Request.QueryString["type"];

            Response.Clear();
            Response.ContentType = "application/json; charset=utf-8";

            if (string.IsNullOrEmpty(queryType) || queryType.ToLower().Equals("all"))
            {
                Response.Write(SummaryInfo.AllInfo);
            }
            else
            {
                Response.Write(SummaryInfo.MachineInfo);
            }

            Response.End();
        }
    }
}