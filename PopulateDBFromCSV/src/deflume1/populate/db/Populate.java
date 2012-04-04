package deflume1.populate.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.*;


public class Populate 
{
	private static HashSet<RouteData> routeData;
	private static HashSet<TripData> tripData;
	private static HashSet<StopTimeData> stopTimeData;
	private static HashSet<StopData> stopData;
	private static HashSet<AttributePair<Integer, Integer>> routesAndStops;
	private static HashSet<TimetableEntry> timetable;
	
	public static void main (String [] args)
	{				
		routeData = new HashSet<RouteData>();
		tripData = new HashSet<TripData>();
		stopTimeData = new HashSet<StopTimeData>();
		stopData = new HashSet<StopData>();
		routesAndStops = new HashSet<AttributePair<Integer, Integer>>();
		timetable = new HashSet<TimetableEntry>();
		
		parseTrips();
		parseRoutes();
		parseStops();
		parseStopTimes();
		
		mapStopsToRoutes();
		constructTimetable();
		
		writeRoutesToDB();
		writeStopsToDB();
		writeRoutesAndStops();
		writeTimetable();
		
	}
	
	/**
	 * This function parses the routes file for data
	 */
	public static void parseRoutes()
	{
		try
		{
			FileInputStream fInputStream = new FileInputStream("google_transit\\routes.txt");
			DataInputStream dInputStream = new DataInputStream(fInputStream);
						
			BufferedReader reader = new BufferedReader(new InputStreamReader(dInputStream));
			
			String line;
			line = reader.readLine();
			
			while((line = reader.readLine()) != null)
			{
				Scanner lineScanner = new Scanner(line).useDelimiter(",");
								
				String routeID = lineScanner.next();
				lineScanner.next();
				lineScanner.next();
				String routeName = lineScanner.next();
				lineScanner.next();
				lineScanner.next();
				lineScanner.next();
				String routeColor = lineScanner.next();
				
				routeData.add(new RouteData(routeID, routeName, routeColor));
			}			
			
			reader.close();
			dInputStream.close();
			fInputStream.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * This function parses the trips.txt file to extract the <routeID tripID> pairs
	 */
	public static void parseTrips()
	{		
		try
		{
			FileInputStream fInputStream = new FileInputStream("google_transit\\trips.txt");
			DataInputStream dInputStream = new DataInputStream(fInputStream);
						
			BufferedReader reader = new BufferedReader(new InputStreamReader(dInputStream));
			
			String line;
			line = reader.readLine();
			
			while((line = reader.readLine()) != null)
			{
				Scanner lineScanner = new Scanner(line).useDelimiter(",");
				
				String routeID = lineScanner.next();
				String serviceID = lineScanner.next();
				String tripID = lineScanner.next();
				String headsign = lineScanner.next();
				String directionIDStr = lineScanner.next();
				int directionID = Integer.parseInt(directionIDStr);
				String blockID = lineScanner.next();
				String shapeID = lineScanner.next();
				
				tripData.add(new TripData(routeID, serviceID, tripID, headsign, directionID, blockID, shapeID));
			}			
			
			reader.close();
			dInputStream.close();
			fInputStream.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * This function parses the stop_times.txt file to get the <trip_id, stop_id> pairs
	 */
	public static void parseStopTimes()
	{		
		try
		{
			FileInputStream fInputStream = new FileInputStream("google_transit\\stop_times.txt");
			DataInputStream dInputStream = new DataInputStream(fInputStream);
						
			BufferedReader reader = new BufferedReader(new InputStreamReader(dInputStream));
			
			String line;
			line = reader.readLine();
			
			while((line = reader.readLine()) != null)
			{
				Scanner lineScanner = new Scanner(line).useDelimiter(",");
				
				String tripID = lineScanner.next();
				String arrivalTime = lineScanner.next();
				String departureTime = lineScanner.next();
				String stopID = lineScanner.next();
				String stopSequenceStr = lineScanner.next();
				Integer stopSequence = Integer.parseInt(stopSequenceStr);				
				
				stopTimeData.add(new StopTimeData(tripID, arrivalTime, departureTime, stopID, stopSequence));
			}			
			
			reader.close();
			dInputStream.close();
			fInputStream.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
	
	/**
	 * This function parses the stop_times.txt file to get the <trip_id, stop_id> pairs
	 */
	public static void parseStops()
	{		
		try
		{
			FileInputStream fInputStream = new FileInputStream("google_transit\\stops.txt");
			DataInputStream dInputStream = new DataInputStream(fInputStream);
						
			BufferedReader reader = new BufferedReader(new InputStreamReader(dInputStream));
			
			String line;
			line = reader.readLine();
			
			while((line = reader.readLine()) != null)
			{
				Scanner lineScanner = new Scanner(line).useDelimiter(",");
				
				String stopID = lineScanner.next();
				lineScanner.next();
				String stopName = lineScanner.next();
				lineScanner.next();
				String stopLatitude = lineScanner.next();
				String stopLongitude = lineScanner.next();
				
				stopData.add(new StopData(stopID, stopName, stopLatitude, stopLongitude));
			}			
			
			reader.close();
			dInputStream.close();
			fInputStream.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
	
	/**
	 * This function pairs all of the routes with all of the stops
	 */
	public static void mapStopsToRoutes()
	{
		for(TripData entryOne : tripData)
		{
			for(StopTimeData entryTwo : stopTimeData)
			{
				if(entryOne.getTripIDHash() == entryTwo.getTripIDHash())
				{
					routesAndStops.add(new AttributePair<Integer, Integer>(entryOne.getRouteIDHash(), entryTwo.getStopIDHash()));
				}
			}
			
		}
		
	}
	
	public static void constructTimetable()
	{
		for(StopTimeData entryOne : stopTimeData)
		{
			for(TripData entryTwo : tripData)
			{				
				if(entryOne.getTripIDHash() == entryTwo.getTripIDHash())
				{
					timetable.add(new TimetableEntry(entryOne.getStopID(), entryTwo.getRouteID(), entryOne.getArrivalTime()));
				}
			}
		}
	}
	
	/**
	 * This function outputs the <routeID, stopID> pairs to a text file
	 */
	public static void outputRoutesAndStops()
	{
		try
		{
			File outputFile = new File("routes_to_stops.txt");
		
			FileOutputStream fOutputStream = new FileOutputStream(outputFile);
			DataOutputStream dOutputStream = new DataOutputStream(fOutputStream);
						
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dOutputStream));
			
			for(AttributePair<Integer, Integer> entry : routesAndStops)
			{
				writer.write(entry.getFirstElement() + "," + entry.getSecondElement());
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
			dOutputStream.close();
			fOutputStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void writeRoutesToDB()
	{	
		try 
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:assets\\geoAlarmDB.sqlite");
			
			PreparedStatement prep = conn.prepareStatement("insert into routes values (?, ?, ?);");
			
			for(RouteData entry : routeData)
			{
				prep.setInt(1, entry.getRouteIDHash());
				prep.setInt(2, 0);
				prep.setString(3, entry.getName());
				prep.addBatch();
			}
			
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			conn.close();
		} 
		catch (ClassNotFoundException e) 
		{			
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}			
	}
	
	public static void writeStopsToDB()
	{
		try 
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:assets\\geoAlarmDB.sqlite");
			
			PreparedStatement prep = conn.prepareStatement("insert into station values (?, ?, ?, ?);");
			
			for(StopData entry : stopData)
			{
				prep.setInt(1, entry.getStopIDHash());
				prep.setDouble(2, entry.getLongitude());
				prep.setDouble(3, entry.getLatitude());
				prep.setString(4, entry.getName());
				prep.addBatch();
			}
			
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			conn.close();
		} 
		catch (ClassNotFoundException e) 
		{			
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}			
	}
	
	public static void writeRoutesAndStops()
	{
		try 
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:assets\\geoAlarmDB.sqlite");
			
			PreparedStatement prep = conn.prepareStatement("insert into route_station values (?, ?);");
			
			for(AttributePair<Integer, Integer> entry : routesAndStops)
			{
				prep.setInt(1, entry.getFirstElement());
				prep.setInt(2, entry.getSecondElement());
				prep.addBatch();
			}
			
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			conn.close();
		} 
		catch (ClassNotFoundException e) 
		{			
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}			
	}
	
	public static void writeTimetable()
	{
		try 
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:assets\\geoAlarmDB.sqlite");
			
			PreparedStatement prep = conn.prepareStatement("insert into timetable values (?, ?, ?);");
			DateFormat d = DateFormat.getInstance();
			for(TimetableEntry entry : timetable)
			{
				prep.setInt(1, entry.getStopIDHash());
				prep.setInt(2, entry.getRouteIDHash());
				prep.setString(3, entry.getArrivalTime());
				prep.addBatch();
			}
			
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			conn.close();
		} 
		catch (ClassNotFoundException e) 
		{			
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}			
	}
	

}
