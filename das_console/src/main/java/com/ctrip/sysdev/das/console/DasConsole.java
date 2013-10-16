package com.ctrip.sysdev.das.console;

import org.eclipse.jetty.server.Server;

public class DasConsole {
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new DasConsoleHandler());
  
        server.start();
        server.join();
    }
}
