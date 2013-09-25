using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Security;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.Security;
using System.Xml;
using System.Configuration;

namespace CasTest
{
    public partial class _Default : System.Web.UI.Page
    {
        // Local specific CAS host
        //private const string CASHOST = "https://cas.ctripcorp.com/caso/";

        protected void Page_Load(object sender, EventArgs e)
        {
             String logoutRequest = Request.Params.Get("logoutRequest");
            if (logoutRequest != null)
            {
                Regex regex = new Regex("<.*\\:?SessionIndex\\>(.*)\\<\\/.*\\:?SessionIndex>");
                Match match = regex.Match(logoutRequest);
                if (match != null && match.Success)
                {
                    string signOutTicket = match.Groups[1].Value;
                    try {
                    CASUtil.CASTicket casTicket = CASUtil.sessionTickets[signOutTicket];
                    casTicket.LogoutHappened = true;
                    
                    } catch (KeyNotFoundException ex) {

                        // Do not care about it
                    }
                    return;
                }
            }
            // Look for the "ticket=" after the "?" in the URL
            string tkt = Request.QueryString["ticket"];

            // This page is the CAS service=, but discard any query string residue
            string service = Request.Url.GetLeftPart(UriPartial.Path);

            // First time through there is no ticket=, so redirect to CAS login
            if (tkt == null || tkt.Length == 0)
            {
                string redir = ConfigurationManager.AppSettings["cashost"] + "login?" +
                  "service=" + service;
                Response.Redirect(redir);
                return;
            }

            // Second time (back from CAS) there is a ticket= to validate
            string validateurl = ConfigurationManager.AppSettings["cashost"] + "serviceValidate?" +
              "ticket=" + tkt + "&" +
              "service=" + service;

            //Change SSL checks so that all checks pass
            ServicePointManager.ServerCertificateValidationCallback =
                new RemoteCertificateValidationCallback(
                    delegate
                    { return true; }
                );
   
            StreamReader Reader = new StreamReader(new WebClient().OpenRead(validateurl));
            string resp = Reader.ReadToEnd();
            // I like to have the text in memory for debugging rather than parsing the stream

            // Some boilerplate to set up the parse.
            NameTable nt = new NameTable();
            XmlNamespaceManager nsmgr = new XmlNamespaceManager(nt);
            XmlParserContext context = new XmlParserContext(null, nsmgr, null, XmlSpace.None);
            XmlTextReader reader = new XmlTextReader(resp, XmlNodeType.Element, context);

            string netid = null;

            // A very dumb use of XML. Just scan for the "user". If it isn't there, its an error.
            while (reader.Read())
            {
                if (reader.IsStartElement())
                {
                    string tag = reader.LocalName;
                    if (tag == "user")
                        netid = reader.ReadString();
                }
            }
            // if you want to parse the proxy chain, just add the logic above
            reader.Close();
            // If there was a problem, leave the message on the screen. Otherwise, return to original page.
            if (netid == null)
            {
                Label1.Text = "CAS returned to this application, "
                    + "but then refused to validate your identity.";
            }
            else
            {

                HttpContext.Current.Session.Add("user", tkt);
                CASUtil.CASTicket casTicket = new CASUtil.CASTicket(HttpContext.Current.Session);
                CASUtil.sessionTickets.Add(tkt, casTicket);
                Label1.Text = "Welcome " + netid;
                FormsAuthentication.RedirectFromLoginPage(netid, false); // set netid in ASP.NET blocks
            }
        
        }

        public void Logout()
        {
        }

    }
}
