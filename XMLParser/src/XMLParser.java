
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XMLParser {
	private static final String API_KEY = "ef23cfb988384e259056b4098c70d877";
	private static final String VERSION = "v2.1";
	private static final String STATIONS_URL = "http://developer.cumtd.com/api/" + VERSION +
			"/xml/GetStops?key=" + API_KEY;
	private static final String ROUTES_URL = "http://developer.cumtd.com/api/" + VERSION +
			"/xml/GetRoutes?key=" + API_KEY;
	private static final String TRIP_URL = "http://developer.cumtd.com/api/" + VERSION +
			"/xml/GetTripsByRoute?key=" + API_KEY + "&route_id=";
	private static final String STOPS_BY_TIME_URL = "http://developer.cumtd.com/api/" +
			VERSION + "/xml/GetStopTimesByTrip?key=" + API_KEY + "&trip_id=";
	
	private static Document stationDoc;
	private static Document routeDoc;
	
	private static String getData(String address) {
		URL url = null;
		InputStream is = null;
		BufferedReader dis;
		String data = "";
		String line;
		

	    try {
			url = new URL(address);
		} catch (MalformedURLException e) {
			System.err.println("ERROR: the URL is malformed. Now terminating...");
			System.exit(1);
		}
	    try {
			is = url.openStream();
		} catch (IOException e) {
			System.err.println("ERROR: cannot open stream. Now terminating...");
			System.exit(2);
		}  // throws an IOException
	    dis = new BufferedReader(new InputStreamReader(is));

	    try {
			while ((line = dis.readLine()) != null) {
				data += line;
			}
	        is.close();
		} catch (IOException e) {
			System.err.println("ERROR: unexpected error occured. Now terminating...");
			System.exit(2);
		}
	    return data;
	}
	
	
	
	/**
	 * Fill Stations
	 * @return HashMap of CUMTD Stations
	 * @throws XPathExpressionException
	 */
	private static HashMap<String, Station> parseStations() throws XPathExpressionException {
		String stationID, stopID, name, direction, longtitude, latitude;
		HashMap<String, Station> stations = new HashMap<String, Station>();
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//stop");
		
		Object result = expr.evaluate(stationDoc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currentNode = nodes.item(i);
			stationID = null;
			stopID = null;
			name = null;
			direction= null;
			longtitude = null;
			latitude = null;
			
			if (currentNode.hasAttributes()){
				NamedNodeMap curAttributes = currentNode.getAttributes();
				stationID = curAttributes.getNamedItem("stop_id").getTextContent();
				name = curAttributes.getNamedItem("stop_name").getTextContent();
				Station newStation = new Station(stationID, name);
				
				xpath = XPathFactory.newInstance().newXPath();
				expr = xpath.compile("//stop_point");
				result = expr.evaluate(currentNode, XPathConstants.NODESET);
				NodeList childNodes = (NodeList) result;
				
					for(int j=0; j < childNodes.getLength(); j++){
						currentNode = childNodes.item(j);
						curAttributes = currentNode.getAttributes();
						stopID = curAttributes.getNamedItem("stop_id").getTextContent();
						longtitude = curAttributes.getNamedItem("stop_lon").getTextContent();
						latitude = curAttributes.getNamedItem("stop_lat").getTextContent();
						newStation.addSubStation(stopID,longtitude,latitude);
					}
				
				
				stations.put(stationID, newStation);
			}
			
		}
		return stations;
	
	}
	
	
	// TODO: Populate the data structure with routes
	private static HashMap<String, Route> parseRoutes() throws XPathExpressionException {
		

		String routeID, name, number;
		HashMap<String, Route> routes = new HashMap<String, Route>();
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//route");
		
		Object result = expr.evaluate(routeDoc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		  
		for (int i = 0; i < nodes.getLength(); i++) {
			routeID = null;
			name = null;
			number = null;
			Node currentNode = nodes.item(i);
			if (currentNode.hasAttributes()){
				NamedNodeMap curAttributes = currentNode.getAttributes();
				routeID = curAttributes.getNamedItem("route_id").getTextContent();
				number = curAttributes.getNamedItem("route_short_name").getTextContent();
				name = curAttributes.getNamedItem("route_long_name").getTextContent();			
				//System.out.println(number);
				Route newRoute = new Route(routeID, name, Integer.parseInt(number));
				routes.put(routeID, newRoute);
			}
							  
		}
		  
		return routes;		
	}
	
	private static Vector<String> gatherTripIDs(HashMap<String, Route> routes) {
		int currIndex, endIndex;
		String tripID;
		
		Vector<String> tripIDs = new Vector<String>();
		
		Set<String> routesKeySet = routes.keySet();
		for(String key : routesKeySet) {
			String data = getData(TRIP_URL + key);
			currIndex = 0;
			while((currIndex = data.indexOf("trip_id", currIndex)) != -1) {
				currIndex += 9;
				endIndex = data.indexOf('"', currIndex);
				tripID = data.substring(currIndex, endIndex);
				if(!tripIDs.contains(tripID)) {
					tripIDs.add(tripID);
				}
			}
		}
		return tripIDs;
	}
	
	private static HashMap<String, Route> populateStations(HashMap<String, Route> routes) {
		System.out.print("test2");
		Vector<Station> stations = new Vector<Station>();
		Vector<String> tripIDs = gatherTripIDs(routes);
		
		int numTripIDs = tripIDs.size();
		for(int i = 0; i < numTripIDs; i++) {
			String stopsByTimeData = getData(STOPS_BY_TIME_URL + tripIDs.get(i));
		}
		System.out.print("test3");
		return routes;
		
	}
	
	public static String readFile(String path){
		 File file = new File(path);
		    int ch;
		    StringBuffer strContent = new StringBuffer("");
		    FileInputStream fin = null;
		    try {
		      fin = new FileInputStream(file);
		      while ((ch = fin.read()) != -1)
		        strContent.append((char) ch);
		      fin.close();
		    } catch (Exception e) {
		      System.out.println(e);
		    }
		    return strContent.toString();
	}
	
	
	private static void parseXmlFile() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		  DocumentBuilderFactory domFactory = 
				  DocumentBuilderFactory.newInstance();
		  domFactory.setNamespaceAware(false); 
		  DocumentBuilder builder = domFactory.newDocumentBuilder();
		  stationDoc = builder.parse("GetStops.xml");
		  routeDoc = builder.parse("GetRoutes.xml");
	}
	
	public static Document CreateXML(String xmlString) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory factory =
				   DocumentBuilderFactory.newInstance();
				InputSource source = new InputSource(
				   new StringReader(xmlString));
				Document document =
				   factory.newDocumentBuilder().parse(source);
				return document;
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException { // TODO: Change the function name to gatherData()
		parseXmlFile();
		// gets all routes
		String routesStr = 
				//getData(ROUTES_URL);
				readFile("GetRoutes.xml");
		//System.out.println(routesStr);
				
		
		// gets all stations
		String stationsStr = 
				//getData(STATIONS_URL);
				readFile("GetStops.xml");
		
		
		HashMap<String, Route> routes = parseRoutes();
		HashMap<String, Station> stations = parseStations();
		System.out.println(routes.get("1 YELLOW ALT").getName());
		System.out.println(stations.get("150DALE").getName());
		System.out.println(stations.get("150DALE").getlatitude("150DALE:1"));
//		routes = populateStations(routes);
	    
	}
}
