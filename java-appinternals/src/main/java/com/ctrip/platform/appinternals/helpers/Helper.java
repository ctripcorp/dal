package com.ctrip.platform.appinternals.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctrip.platform.appinternals.Result;
import com.ctrip.platform.appinternals.serialization.JSONConverter;
import com.ctrip.platform.appinternals.serialization.XMLConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

public class Helper {
	private static final String IPV4PATTERN = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])"
			+ "\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";

	private static final String URLPATTERN = "https?://(.*)/appinternals/?(.*)";

	private static Pattern ippattern = null;
	private static Pattern urlpattern = null;

	private static XStream xmlStream = null;
	private static XStream jsonStream = null;

	static {
		ippattern = Pattern.compile(IPV4PATTERN);
		urlpattern = Pattern.compile(URLPATTERN);

		xmlStream = new XStream();
		xmlStream.autodetectAnnotations(true);

		jsonStream = new XStream(new JsonHierarchicalStreamDriver() {
			public HierarchicalStreamWriter createWriter(Writer writer) {
				return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
			}
		});
		jsonStream.autodetectAnnotations(true);
	}

	public static boolean validateIPV4(String ipaddr) {
		Matcher m = ippattern.matcher(ipaddr);
		return m.matches();
	}

	public static boolean validateURL(String url) {
		Matcher m = urlpattern.matcher(url);
		return m.matches();
	}

	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuffer(strLen)
				.append(Character.toTitleCase(str.charAt(0)))
				.append(str.substring(1)).toString();
	}

	public static <T> String toXML(Class<?> type, String alias, T model) {
		xmlStream.setMode(XStream.NO_REFERENCES);
		xmlStream.alias(alias, type);
		xmlStream.registerConverter(new XMLConverter());
		return xmlStream.toXML(model);
	}

	public static <T> String toJSON(Class<?> type, String alias, T model) {
		jsonStream.setMode(XStream.NO_REFERENCES);
		jsonStream.alias(alias, type);
		jsonStream.registerConverter(new JSONConverter());
		return jsonStream.toXML(model);
	}
	
	public static String toChangeResultJson(Result result){
		return String.format("{\"Message\":\"Success\",\"IsSuccess\":\"True\"}", result.getMessage(), result.isSueccess() ? "Ture" : "False");
		
	}

	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String getIPV4() {
		String ipv4 = "";
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				if (!current.isUp() || current.isLoopback()
						|| current.isVirtual())
					continue;
				Enumeration<InetAddress> addresses = current.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress current_addr = addresses.nextElement();
					if (current_addr instanceof Inet4Address) {
						ipv4 = current_addr.getHostAddress();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipv4;
	}

	public static String getHostName(){
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void close(InputStream stream){
		try {
			if(stream != null)
				stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void close(OutputStream stream){
		try {
			if(stream != null)
				stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
