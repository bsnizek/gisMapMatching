package ku.ign.mapmatcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ShellFileBuilder {

	String inDir = "out";
	String shellFileName = "yeah.sh";
	String sHTemplate = "curl -X POST -H 'Content-Type: application/gpx+xml' -H 'Accept: application/json'"
			+ " --data-binary @out/%s.gpx 'https://test.roadmatching.com/rest/mapmatch/?app_id=01e8761c&"
			+ "app_key=5c989d0272b48a9b2cbefd19d23d95e9&output.groupByWays=true&output.linkGeometries=true&output.osmProjection=false&"
			+ "output.linkMatchingError=true&output.waypoints=true&output.waypointsIds=true' -o json/%s.json\n";


	public ArrayList<String> getGPXFiles(String dir) {
		ArrayList<String> fileNames = new ArrayList<String>();
		File f = new File(dir);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		for (File file : files) {
			String[] tokens = file.getName().split("\\.(?=[^\\.]+$)");
			if (tokens[1].equals("gpx")) {
				System.out.println(tokens[0]);
				fileNames.add(tokens[0]);
			}
		}
		return fileNames;
	}

	private void build() throws IOException {
		
		ArrayList<String> files = this.getGPXFiles(inDir);
		FileWriter fw = new FileWriter(shellFileName);
		for (String file : files) {
			fw.write(String.format (sHTemplate, file, file) );
		}
		fw.close();
	}


	public static void main(String[] args) {
		ShellFileBuilder sfB = new ShellFileBuilder();
		try {
			sfB.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
