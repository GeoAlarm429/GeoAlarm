package edu.illinois.geoalarm.parser;

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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class XMLParser {
	private static final String API_KEY = "ef23cfb988384e259056b4098c70d877";
	private static final String VERSION = "v2.1";
	private static String ORIGIN_LAT = "";
	private static String ORIGIN_LON = "";
	private static String DEST_LAT = "";
	private static String DEST_LON = "";
	private static final String STATIONS_URL = "http://developer.cumtd.com/api/"
			+ VERSION + "/xml/GetStops?key=" + API_KEY;
	private static final String ROUTES_URL = "http://developer.cumtd.com/api/"
			+ VERSION + "/xml/GetRoutes?key=" + API_KEY;
	private static final String TRIP_URL = "http://developer.cumtd.com/api/"
			+ VERSION + "/xml/GetTripsByRoute?key=" + API_KEY + "&route_id=";
	private static final String STOPS_BY_TIME_URL = "http://developer.cumtd.com/api/"
			+ VERSION + "/xml/GetStopTimesByTrip?key=" + API_KEY + "&trip_id=";

	private static String PLAN_TRIP_URL = "http://developer.cumtd.com/api/"
			+ VERSION + "/xml/GetPlannedTripsByLatLon?key=" + API_KEY;
	private static final String PATH_STATION = "/Users/solanki/Documents/workspace/CS429/src/GetStops.xml";
	private static final String PATH_ROUTE = "/Users/solanki/Documents/workspace/CS429/src/GetRoutes.xml";
	private static final String PATH_TRIP = "/Users/solanki/Documents/workspace/CS429/src/oneLeg.xml";

	private static Document stationDoc;
	private static Document routeDoc;
	private static Document tripDoc;

	/**
	 * gets data from query URL address
	 * 
	 * @param address
	 *            - URL query
	 * @return - data from query
	 */
	static String getData(String address) {
		URL url = null;
		InputStream is = null;
		BufferedReader dis;
		String data = "";
		String line;

		try 
		{
			url = new URL(address);
		} 
		catch (MalformedURLException e) 
		{
			System.err.println("ERROR: the URL is malformed. Now terminating...");			
		}
		try 
		{
			is = url.openStream();
		} 
		catch (IOException e) 
		{
			System.err.println("ERROR: cannot open stream. Now terminating...");			
		} // throws an IOException
		dis = new BufferedReader(new InputStreamReader(is));

		try 
		{
			while ((line = dis.readLine()) != null) 
			{
				data += line;
			}
			dis.close();
			is.close();
		} 
		catch (IOException e) 
		{
			System.err.println("ERROR: unexpected error occured. Now terminating...");			
		}
		return data;
	}

	/**
	 * Fill Stations
	 * 
	 * @return HashMap of CUMTD Stations
	 * @throws XPathExpressionException
	 */
	private static HashMap<String, Station> parseStations()
			throws XPathExpressionException {
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
			direction = null;
			longtitude = null;
			latitude = null;

			if (currentNode.hasAttributes()) {
				NamedNodeMap curAttributes = currentNode.getAttributes();
				stationID = curAttributes.getNamedItem("stop_id")
						.getTextContent();
				name = curAttributes.getNamedItem("stop_name").getTextContent();
				Station newStation = new Station(stationID, name);

				xpath = XPathFactory.newInstance().newXPath();
				expr = xpath.compile("//stop_point");
				result = expr.evaluate(currentNode, XPathConstants.NODESET);
				NodeList childNodes = (NodeList) result;

				for (int j = 0; j < childNodes.getLength(); j++) {
					currentNode = childNodes.item(j);
					curAttributes = currentNode.getAttributes();
					stopID = curAttributes.getNamedItem("stop_id")
							.getTextContent();
					longtitude = curAttributes.getNamedItem("stop_lon")
							.getTextContent();
					latitude = curAttributes.getNamedItem("stop_lat")
							.getTextContent();
					newStation.addSubStation(stopID, longtitude, latitude);
				}

				stations.put(stationID, newStation);
			}

		}
		return stations;

	}

	// Populate the data structure with routes
	private static HashMap<String, Route> parseRoutes()
			throws XPathExpressionException {

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
			if (currentNode.hasAttributes()) {
				NamedNodeMap curAttributes = currentNode.getAttributes();
				routeID = curAttributes.getNamedItem("route_id")
						.getTextContent();
				number = curAttributes.getNamedItem("route_short_name")
						.getTextContent();
				name = curAttributes.getNamedItem("route_long_name")
						.getTextContent();
				// System.out.println(number);
				Route newRoute = new Route(routeID, name,
						Integer.parseInt(number));
				routes.put(routeID, newRoute);
			}

		}

		return routes;
	}

	/**
	 * Parse Trip itinerary
	 * 
	 * @return Vector that contains all Itineraries
	 * @throws XPathExpressionException
	 */
	private static Vector<Itinerary> parseTrip() throws XPathExpressionException 
	{
		Vector<Itinerary> Itineraries = new Vector<Itinerary>();

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//itinerary");

		Object result = expr.evaluate(tripDoc, XPathConstants.NODESET);

		NodeList nodes = (NodeList) result;
		// traverse list of itineraries
		for (int i = 0; i < nodes.getLength(); i++) 
		{
			String startTime, endTime;

			Node currentItinerary = nodes.item(i);
			System.out.println(nodes.item(i).getNodeName());
			NamedNodeMap itineraryAttributes = currentItinerary.getAttributes();
			startTime = itineraryAttributes.getNamedItem("start_time")
					.getTextContent();
			endTime = itineraryAttributes.getNamedItem("end_time")
					.getTextContent();

			Itinerary itinerary = new Itinerary(startTime, endTime);

			// get legs from current itinerary
			xpath = XPathFactory.newInstance().newXPath();
			expr = xpath.compile("legs/leg");
			result = expr.evaluate(currentItinerary, XPathConstants.NODESET);
			NodeList legNodes = // (NodeList) result;
			currentItinerary.getChildNodes().item(0).getChildNodes();

			// traverse legs
			for (int j = 0; j < legNodes.getLength(); j++) 
			{
				Node currentLeg = legNodes.item(j);
				System.out.println(legNodes.item(j).getNodeName());
				// TODO: check Leg type
				NamedNodeMap legAttributes = currentLeg.getAttributes();
				System.out.println("type " + legAttributes.getNamedItem("type").getTextContent());
				if (!legAttributes.getNamedItem("type").getTextContent().equals("Service"))
					continue;
				// get service nodes
				NodeList serviceNodes = currentLeg.getChildNodes().item(0).getChildNodes();
				System.out.println("Number " + serviceNodes.getLength()	+ " NodeName " + serviceNodes.item(0).getNodeName());
				NodeList serviceDetailNodes = serviceNodes.item(0).getChildNodes();
				Trip begin = null, end = null;
				String lat, lon, time, busName;

				// traverse serviceDetails: begin,end,route,trip
				for (int k = 0; k < serviceDetailNodes.getLength(); k++) 
				{

					Node currentServiceDetailNode = serviceDetailNodes.item(k);
					System.out.println("Number "+ serviceDetailNodes.getLength() + " NodeName "	+ serviceDetailNodes.item(k).getNodeName());
					if (currentServiceDetailNode.getNodeName().equals("begin")) 
					{
						NamedNodeMap beginAttributes = currentServiceDetailNode.getAttributes();
						lat = (beginAttributes.getNamedItem("lat").getTextContent());
						lon = (beginAttributes.getNamedItem("lon").getTextContent());
						time = (beginAttributes.getNamedItem("time").getTextContent());
						busName = null;
						begin = new Trip(lat, lon, time, busName);
					}
					if (currentServiceDetailNode.getNodeName().equals("end")) 
					{
						NamedNodeMap beginAttributes = currentServiceDetailNode.getAttributes();
						lat = (beginAttributes.getNamedItem("lat").getTextContent());
						lon = (beginAttributes.getNamedItem("lon").getTextContent());
						time = (beginAttributes.getNamedItem("time").getTextContent());
						busName = null;
						end = new Trip(lat, lon, time, busName);
					}
					if (currentServiceDetailNode.getNodeName().equals("route")) 
					{
						NamedNodeMap beginAttributes = currentServiceDetailNode.getAttributes();
						busName = (beginAttributes.getNamedItem("route_short_name").getTextContent());
						begin.setBusName(busName);
						end.setBusName(busName);
					}
				}

				itinerary.addLeg(begin, end);
			}
			Itineraries.add(itinerary);
		}
		return Itineraries;
	}

	private static Vector<String> gatherTripIDs(HashMap<String, Route> routes) 
	{
		int currIndex, endIndex;
		String tripID;

		Vector<String> tripIDs = new Vector<String>();

		Set<String> routesKeySet = routes.keySet();
		for (String key : routesKeySet) {
			String data = getData(TRIP_URL + key);
			currIndex = 0;
			while ((currIndex = data.indexOf("trip_id", currIndex)) != -1) 
			{
				currIndex += 9;
				endIndex = data.indexOf('"', currIndex);
				tripID = data.substring(currIndex, endIndex);
				if (!tripIDs.contains(tripID)) 
				{
					tripIDs.add(tripID);
				}
			}
		}
		return tripIDs;
	}

	private static HashMap<String, Route> populateStations(HashMap<String, Route> routes) 
	{
		System.out.print("test2");
		Vector<Station> stations = new Vector<Station>();
		Vector<String> tripIDs = gatherTripIDs(routes);

		int numTripIDs = tripIDs.size();
		for (int i = 0; i < numTripIDs; i++) 
		{
			String stopsByTimeData = getData(STOPS_BY_TIME_URL + tripIDs.get(i));
		}
		System.out.print("test3");
		return routes;

	}

	/**
	 * DEBUG: Used to parse XML from local
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	private static void parseXmlFile() throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException 
			{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(false);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		stationDoc = builder.parse(PATH_STATION);
		routeDoc = builder.parse(PATH_ROUTE);
		tripDoc = builder.parse(PATH_TRIP);
	}

	/**
	 * Creates XML from a string
	 * 
	 * @param xmlString
	 * @return XML Document
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Document CreateXML(String xmlString) throws SAXException,
			IOException, ParserConfigurationException 
			{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		InputSource source = new InputSource(new StringReader(xmlString));
		Document document = factory.newDocumentBuilder().parse(source);
		return document;
	}

	/**
	 * Creates query for trip
	 * 
	 * @param origin_lat
	 * @param origin_lon
	 * @param dest_lat
	 * @param dest_lon
	 * @return query as a String
	 */
	public static String tripPlanner(String origin_lat, String origin_lon,
			String dest_lat, String dest_lon) 
	{
		String trip_query = PLAN_TRIP_URL;

		trip_query += "&origin_lat=" + origin_lat;
		trip_query += "&origin_lon=" + origin_lon;
		trip_query += "&destination_lat=" + dest_lat;
		trip_query += "&destination_lon=" + dest_lon;

		return trip_query;
	}

	/**
	 * user function to get all Itinerary
	 * 
	 * @param origin_lat
	 * @param origin_lon
	 * @param dest_lat
	 * @param dest_lon
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static Vector<Itinerary> getItinerary(String origin_lat, String origin_lon, String dest_lat, String dest_lon)
			throws SAXException, IOException, ParserConfigurationException,
			XPathExpressionException 
			{
		String trip_url = tripPlanner(origin_lat, origin_lon, dest_lat,
				dest_lon);
		String tripStr = XMLParser.getData(trip_url);
		tripDoc = CreateXML(tripStr);

		Vector<Itinerary> Itineraries = parseTrip();
		return Itineraries;
	}

	public Vector<Itinerary> getTripItinerary(String origin_lat, String origin_lon, String dest_lat, String dest_lon) 
	{
		String trip_url = tripPlanner(origin_lat, origin_lon, dest_lat, dest_lon);
		String tripStr = getData(trip_url);
		try 
		{
			setTripDoc(CreateXML(tripStr));
			return parseTrip();
		} 
		catch (XPathExpressionException e) 
		{			
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{			
			e.printStackTrace();
		} 
		catch (IOException e) 
		{			
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			 e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a string array of available itineraries
	 * @param origin_lat latitude of the start
	 * @param origin_lon longitude of the start
	 * @param dest_lat latitude of the end
	 * @param dest_lon longitude of the end
	 * @return string array
	 */
	public String[] getItineraryArray(String origin_lat, String origin_lon,
			String dest_lat, String dest_lon) 
	{
		
		Vector<Itinerary> itineraries;
		String[] itineraryArray;
		try {
			itineraries = getItinerary(origin_lat, origin_lon, dest_lat,
					dest_lon);
			if (itineraries.size() == 0) {
				itineraryArray = new String[1];
				itineraryArray[0] = "No routes available at this time";
				return itineraryArray;
			}
			itineraryArray = new String[itineraries.size()];
			for (int i = 0; i < itineraries.size(); i++) {
				Vector<Itinerary.Leg> legs = itineraries.get(i).getLegs();
				itineraryArray[i] = "Start: "
						+ legs.get(0).getBegin().getTime().substring(11,16) + " End: "
						+ legs.get(legs.size() - 1).getEnd().getTime().substring(11,16);
			}
			return itineraryArray;
		} 
		catch (XPathExpressionException e) 
		{			
			e.printStackTrace();
			itineraryArray = new String[1];
			itineraryArray[0] = "No routes available at this time";
		} 
		catch (SAXException e) 
		{			
			e.printStackTrace();
			itineraryArray = new String[1];
			itineraryArray[0] = "No routes available at this time";
		} 
		catch (IOException e) 
		{			
			e.printStackTrace();
			itineraryArray = new String[1];
			itineraryArray[0] = "No routes available at this time";
		} 
		catch (ParserConfigurationException e) 
		{			
			e.printStackTrace();
			itineraryArray = new String[1];
			itineraryArray[0] = "No routes available at this time";
		}
		return null;
	}

	/**
	 * Retrieves the legs of a given itinerary. 
	 * @param origin_lat latitude of the start
	 * @param origin_lon longitude of the start
	 * @param dest_lat latitude of the end
	 * @param dest_lon longitude of the start
	 * @param itinerary nth itinerary to be fetched
	 * @return string array
	 */
	public String[] getLegArray(String origin_lat, String origin_lon, String dest_lat, String dest_lon, int itinerary) 
	{	
		Vector<Itinerary> itineraries;
		String[] legArray;
		try 
		{
			itineraries = getItinerary(origin_lat, origin_lon, dest_lat,
					dest_lon);
			if (itineraries.size() <= itinerary) {
				legArray = new String[1];
				legArray[0] = "No routes available at this time";
				return legArray;
			}
			Vector<Itinerary.Leg> legs = itineraries.get(itinerary).getLegs();
			legArray = new String[legs.size()];
			for (int j = 0; j < legs.size(); j++) {
				Trip begin = legs.get(j).getBegin();
				Trip end = legs.get(j).getEnd();
				legArray[j] = "Bus: " + begin.getBusName() + " "
						+ begin.getTime().substring(11, 16) + " - " + end.getTime().substring(11,16);
			}
			return legArray;
		} 
		catch (XPathExpressionException e) 
		{			
			e.printStackTrace();
			legArray = new String[1];
			legArray[0] = "No routes available at this time";
		} 
		catch (SAXException e) 
		{			
			e.printStackTrace();
			legArray = new String[1];
			legArray[0] = "No routes available at this time";
		} 
		catch (IOException e) 
		{			
			e.printStackTrace();
			legArray = new String[1];
			legArray[0] = "No routes available at this time";
		} 
		catch (ParserConfigurationException e) 
		{			
			e.printStackTrace();
			legArray = new String[1];
			legArray[0] = "No routes available at this time";
		}
		return legArray;
	}

	public void setTripDoc(Document tripDoc) 
	{
		XMLParser.tripDoc = tripDoc;
	}

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		/** DEBUG SETUP **/
		// parseXmlFile();

		// SC -> Terminal
		// String origin_lat = "40.116468";
		// String origin_lon = "-88.223846";
		// String dest_lat = "40.115935";
		// String dest_lon = "-88.240947";

		// University & Goodwin -> Transit Plaza
		String origin_lat = "40.116009";
		String origin_lon = "-88.224117";
		String dest_lat = "40.108202";
		String dest_lon = "-88.228923";
		String trip_url = tripPlanner(origin_lat, origin_lon, dest_lat,
				dest_lon);

		// get trip information
		String tripStr = getData(trip_url);
		System.out.println(tripStr);
		tripDoc = CreateXML(tripStr);

		Vector<Itinerary> Itineraries = parseTrip();

		// TODO: print trip information
		for (int i = 0; i < Itineraries.size(); i++) {
			System.out.println("Itinerary " + i);
			Vector<Itinerary.Leg> legs = Itineraries.get(i).getLegs();
			for (int j = 0; j < legs.size(); j++) {
				System.out.println("Leg " + j);
				Trip begin = legs.get(j).getBegin();
				Trip end = legs.get(j).getEnd();
				System.out.println("Begin: \n" + "lat" + begin.getLatitude()
						+ " " + "lon" + begin.getLongitude() + " " + "time"
						+ begin.getTime() + " " + "busName"
						+ begin.getBusName());
				System.out.println("End: \n" + "lat" + end.getLatitude() + " "
						+ "lon" + end.getLongitude() + " " + "time"
						+ end.getTime() + " " + "busName" + end.getBusName());

			}
		}

		// gets all routes
		// String routesStr =
		// getData(ROUTES_URL);
		// routeDoc = CreateXML(routesStr);
		// HashMap<String, Route> routes = parseRoutes();

		// gets all stations
		// String stationsStr =
		// getData(STATIONS_URL);
		// stationDoc = CreateXML(stationsStr);
		// HashMap<String, Station> stations = parseStations();

		/** route/station check **/
		// System.out.println(routes.get("1 YELLOW ALT").getName());
		// System.out.println(stations.get("150DALE").getName());
		// System.out.println(stations.get("150DALE").getlatitude("150DALE:1"));
		// routes = populateStations(routes);

	}

}
