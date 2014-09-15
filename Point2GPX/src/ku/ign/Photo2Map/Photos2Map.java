package ku.ign.Photo2Map;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Photos2Map {

	String dir = "/Users/besn/Dropbox/Bike Planning Article Gamble_Snizek (1)/0.1 - geodata/Bike Signs and Accessibility/Bike Signs and Parking";
	String csVFile = "images.csv";

	public Photos2Map() throws Exception {
		
		FileWriter fw = new FileWriter(csVFile);
		fw.write("id,lon,lat,altitiude,file\n");
		
		int i = 1;
		
		ArrayList<File> files = getImageFiles(dir);
		for (File f : files) {
			
			GeoTag x = new JpegGeoTagReader().readMetadata(f);
			
			fw.write(i + "," + x.getLongitude() + "," + x.getLatitude() + "," + x.getAltitude() + "," + f.getName() + "\n");
			i++;
		}
		fw.close();
	}

	public ArrayList<File> getImageFiles(String dir) {
		ArrayList<File> fs = new ArrayList<File>();
		File f = new File(dir);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		for (File file : files) {
			String[] tokens = file.getName().split("\\.(?=[^\\.]+$)");
			if (tokens[1].toLowerCase().equals("jpg")) {
				fs.add(file);
			}
		}
		return fs;
	}


	public static void main(String[] args) {
		try {
			Photos2Map p2m = new Photos2Map();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
