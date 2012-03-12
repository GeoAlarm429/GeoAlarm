import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


public class GoogleTransit {

	// Constants
	private static final String GOOGLE_TRANSIT_URL = "http://developer.cumtd.com/gtfs/google_transit.zip";
	private static final String FILENAME = "google_transit.zip";
	
	/**
	 * We want to download the google_transit file that would better
	 * help us populate our database.
	 * @throws InterruptedException 
	 */
	public static void getGoogleTransit() throws InterruptedException {
		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("rm -rf google_transit").waitFor();
			System.out.println("Creating folder 'google_transit'...");
			runtime.exec("mkdir google_transit").waitFor();
			System.out.println("Downloading 'google_transit.zip'...");
			runtime.exec("wget " + GOOGLE_TRANSIT_URL).waitFor();
			//wget();
			System.out.println("Extracting files...");
			runtime.exec("unzip google_transit.zip -d google_transit").waitFor();
			System.out.println("Cleaning up...");
			runtime.exec("rm -f google_transit.zip");
		} catch (IOException e) {
			System.out.println("ERROR: Unexpected error detected.");
		}

	}

}
