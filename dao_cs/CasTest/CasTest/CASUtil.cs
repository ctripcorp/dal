using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CasTest
{
    public class CASUtil
    {

        public static Dictionary<String, CASTicket> sessionTickets;

        private static System.Timers.Timer sessionTicketsChecker = new System.Timers.Timer();

        static CASUtil()
        {
            sessionTickets = new Dictionary<String, CASTicket>();
            sessionTicketsChecker = new System.Timers.Timer(2000);
            sessionTicketsChecker.Elapsed += new System.Timers.ElapsedEventHandler(CASUtil.checkSessionTickets);
            sessionTicketsChecker.Enabled = true;
        }

        private static void checkSessionTickets(object sender, System.Timers.ElapsedEventArgs e)
        {
            Dictionary<string, CASTicket>.KeyCollection kCol = sessionTickets.Keys;
            foreach (String ticket in kCol)
            {
                if (!sessionTickets[ticket].HttpSessionStateWR.IsAlive)
                {
                    sessionTickets.Remove(ticket);
                }
            }
        }


        public class CASTicket
        {
            public CASTicket(System.Web.SessionState.HttpSessionState session)
            {
                this.httpSessionStateWR = new WeakReference(session);
            }
            private WeakReference httpSessionStateWR;

            private bool logoutHappened = false;

            public WeakReference HttpSessionStateWR
            {
                get { return httpSessionStateWR; }
                set { httpSessionStateWR = value; }
            }

            public bool LogoutHappened
            {
                get { return logoutHappened; }
                set { logoutHappened = value; }
            }
        }
    }
}