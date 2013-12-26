import java.io.File;
import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.NetworkTrafficSelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class Runner {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String jettyVersion = Server.getVersion();
		final int port = Integer.parseInt(System.getProperty("port", "111"));
		ProtectionDomain protectionDomain = Runner.class.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();
		String warFile = location.toExternalForm();
		String currentDir = new File(location.getPath()).getParent();
		File workDir = new File(currentDir, "work");

		System.out.println("##########jettyVersion=" + jettyVersion);
		System.out.println("##########port=" + port);
		System.out.println("##########currentDir=" + currentDir);
		System.out.println("##########workDir=" + workDir);
		System.out.println("##########warFile=" + warFile);

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		// webapp.setResourceBase(".");
		webapp.setTempDirectory(workDir);
		// webapp.setClassLoader(Thread.currentThread().getContextClassLoader());
		webapp.setWar(warFile);

		Server server = new Server();

		NetworkTrafficSelectChannelConnector connector = new NetworkTrafficSelectChannelConnector(
				server);
		connector.setPort(port);
		connector.setSoLingerTime(-1);

		server.addConnector(connector);
		server.setHandler(webapp);
		server.start();
		server.join();
	}
}