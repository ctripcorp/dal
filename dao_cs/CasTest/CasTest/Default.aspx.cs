using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Security;
using System.Web.Security;
using System.Xml;
using System.Configuration;

namespace CasTest
{
    public partial class Default : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            try
            {
                string ticket = (string)Context.Session["user"];

                if (ticket != null)
                {
                    CASUtil.CASTicket casTicket = CASUtil.sessionTickets[ticket];
                    if (casTicket.LogoutHappened)
                    {
                        FormsAuthentication.SignOut();
                        FormsAuthentication.RedirectToLoginPage();
                    }
                }
                else
                {
                    FormsAuthentication.RedirectToLoginPage();
                }
            }
            catch (KeyNotFoundException ex)
            {
                //Do nothing
            }
        }

        protected void Button1_Click(object sender, EventArgs e)
        {
            // Look for the "ticket=" after the "?" in the URL
            string tkt = (string)Context.Session["user"];

            // This page is the CAS service=, but discard any query string residue
            string service = Request.Url.GetLeftPart(UriPartial.Path);

            // First time through there is no ticket=, so redirect to CAS login
            if (tkt != null || tkt.Length > 0)
            {
                Context.Session.Remove("user");
                string redir = ConfigurationManager.AppSettings["cashost"] + "logout?" +
                  "service=" + service;
                Response.Redirect(redir);
                return;
            }

       
        }
    }
}