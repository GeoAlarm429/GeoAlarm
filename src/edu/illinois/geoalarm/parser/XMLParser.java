package edu.illinois.geoalarm.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Queries CUMTD for real-time data, returned as XML.  Then parses the XML and 
 * returns objects encapsulating its data
 * @author GeoAlarm
 *
 */

public class XMLParser 
{
	private static final String API_KEY = "ef23cfb988384e259056b4098c70d877";
	private static final String VERSION = "v2.1";
	private static String PLAN_TRIP_URL = "http://developer.cumtd.com/api/"
			+ VERSION + "/xml/GetPlannedTripsByLatLon?key=" + API_KEY;
	private static Document tripDoc;

	/**
	 * gets data from query URL address
	 * 
	 * @param address
	 *            - URL query
	 * @return - data from query
	 */
	static String getData(String address) 
	{
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
			return null;
		}
		try 
		{
			is = url.openStream();			
		} 
		catch (IOException e) 
		{
			System.err.println("ERROR: cannot open stream. Now terminating...");	
			return null;
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
			return null;
		}
		return data;
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

	/**
	 * Creates XML from a string
	 * 
	 * @param xmlString A String containing XML data
	 * @return XML Document A Document containing XML data
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
	 * @param origin_lat The trip origin latitude
	 * @param origin_lon The trip origin longitude
	 * @param dest_lat The trip destination latitude
	 * @param dest_lon The trip destination longitude
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
	 * @param origin_lat The trip origin latitude
	 * @param origin_lon The trip origin longitude
	 * @param dest_lat The trip destination latitude
	 * @param dest_lon The trip destination longitude
	 * @return A Vector containing all of the Itinerary parsed
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
		Vector<Itinerary> Itineraries;
		if(tripStr != null)
		{
			tripDoc = CreateXML(tripStr);
			Itineraries = parseTrip();
		}
		else
		{
			Itineraries = new Vector<Itinerary>();			
		}
		return Itineraries;
	}

	/***
	 * Parses and returns Itineraries for a trip
	 * @param origin_lat The trip origin latitude
	 * @param origin_lon The trip origin longitude
	 * @param dest_lat The trip destination latitude
	 * @param dest_lon The trip destination longitude
	 * @return A Vector containing all of the Itinerary parsed
	 */
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
	 * @return string array of itineraries
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
	 * @return string array of legs
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

	/**
	 * Sets the trip document used by the parser
	 * @param tripDoc The new Documents
	 */
	public void setTripDoc(Document tripDoc) 
	{
		XMLParser.tripDoc = tripDoc;
	}	

}
