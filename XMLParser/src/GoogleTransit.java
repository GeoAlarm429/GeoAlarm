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

	private static final String GoogleTransitURL = "http://developer.cumtd.com/gtfs/google_transit.zip";
	
	private static void wget() {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try
        {
                in = new BufferedInputStream(new URL(GoogleTransitURL).openStream());
                fout = new FileOutputStream("google_transit.zip");

                byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1)
                {
                        fout.write(data, 0, count);
                }
                
                if (in != null)
                    in.close();
                if (fout != null)
                    fout.close();
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public static void main(String[] args) {
		try {
			Runtime.getRuntime().exec("rm -rf google_transit");
			Runtime.getRuntime().exec("mkdir google_transit");
			wget();
			Runtime.getRuntime().exec("unzip google_transit.zip -d google_transit");
			Runtime.getRuntime().exec("rm -f google_transit.zip");
		} catch (IOException e) {
			System.out.println("ERROR: Unexpected error detected.");
		}

	}

}
