import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.xml.sax.SAXException;



public class TestXMLParser {

	/**
	 * Make sure all the files are extracted from 'google_transit.zip'
	 */
	private void checkForGoogleTransitFiles() {
		File googleTransit = new File("google_transit");
		String[] files = googleTransit.list();
		assertTrue("No files have been extracted.", files != null);
		
		HashMap<String, Boolean> filenames = new HashMap<String, Boolean>();
		for(int i = 0; i < files.length; i++) {
			filenames.put(files[i], true);
		}
		
		assertTrue("'agency.txt' does not exist.", filenames.containsKey("agency.txt"));
		assertTrue("'calendar.txt' does not exist.", filenames.containsKey("calendar.txt"));
		assertTrue("'calendar_dates.txt' does not exist.", filenames.containsKey("calendar_dates.txt"));
		assertTrue("'routes.txt' does not exist.", filenames.containsKey("routes.txt"));
		assertTrue("'shapes.txt' does not exist.", filenames.containsKey("shapes.txt"));
		assertTrue("'stops.txt' does not exist.", filenames.containsKey("stops.txt"));
		assertTrue("'stop_times.txt' does not exist.", filenames.containsKey("stop_times.txt"));
		assertTrue("'trips.txt' does not exist.", filenames.containsKey("trips.txt"));
		
		for(int i = 0; i < files.length; i++) {
			filenames.remove(files[i]);
		}
		
		assertTrue("Unnecessary files found.", filenames.size() == 0);
	}
	
	@Test
	public void testXMLParser() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, InterruptedException {
		XMLParser.parse();
		
		checkForGoogleTransitFiles();
	}

}
