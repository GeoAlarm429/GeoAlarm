import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.ws.Response;

public class XMLParser {

	private static final String API_KEY = "79512d08e57e44459469eac8153b8a31";
	private static final String URL_PREFIX = 
			"http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?" +
			"key=79512d08e57e44459469eac8153b8a31&mapid=";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String response = null;
		for(int i = 40000; i < 50000; i++) {
			response = "";
			URL url = new URL(URL_PREFIX + Integer.toString(i));
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String request = "GET /api/1.0/ttarrivals.aspx?" +
					"key=79512d08e57e44459469eac8153b8a31&mapid=" +
					Integer.toString(i) + " HTTP/1.1\r\n" +
					"Host: lapi.transitchicago.com\r\n\r\n";
			out.write(request);
			String line = null;
			while((line = in.readLine()) != null) {
				response += line;
			}	
			if(response.contains("<errCd>103</errCd>"))
				continue;
			System.out.println(response);
			Thread.sleep(2);
		}
	}
}
