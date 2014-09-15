package ku.ign.mapmatcher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JSON2PostGISCSV {
	
	String inDir = "json";
	String wKTLinesFile = "wkt.csv";


	public ArrayList<String> getJsonFiles(String dir) {
		ArrayList<String> fileNames = new ArrayList<String>();
		File f = new File(dir);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		for (File file : files) {
			String[] tokens = file.getName().split("\\.(?=[^\\.]+$)");
			if (tokens[1].equals("json")) {
				fileNames.add(tokens[0]);
			}
		}
		return fileNames;
	}

	private void build() throws IOException, ParseException {
		


		ArrayList<String> jsonFileNames = this.getJsonFiles(inDir);
		FileWriter fw = new FileWriter(wKTLinesFile);
		fw.write("id;wkt\n");

		for (String filename : jsonFileNames) {
			
			String id = "null";
			System.out.println(filename);

			String wkTString = "LINESTRING(";

			BufferedReader br = null;
			br = new BufferedReader(new FileReader(inDir + File.separatorChar + filename + ".json"));
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(br);
			JSONObject diary = (JSONObject) jsonObject.get("diary");
			JSONArray entries = (JSONArray) diary.get("entries");

			for (Object entry : entries) {
				JSONObject jEntry  = (JSONObject) entry;
				JSONObject route = (JSONObject) jEntry.get("route");
				JSONArray links = (JSONArray) route.get("links");

				for (Object link : links) {
					JSONObject jLink = (JSONObject) link;
					
					JSONArray wpts = (JSONArray) jLink.get("wpts");
					// System.out.println(wpts);
					if (wpts != null) {
						id = (String) ((JSONObject) wpts.get(0)).get("id");
					}
					
					
					String geometry = (String) jLink.get("geometry");
					JSONObject jSGeometry = (JSONObject) jsonParser.parse(geometry);
					JSONArray coordinates = (JSONArray) jSGeometry.get("coordinates");
					for (Object coord : coordinates) {
						String c = coord.toString();
						String[] cc = c.split(",");
						String lat = cc[0].substring(1,cc[0].length());
						String lon = cc[1].substring(0,cc[1].length()-1);	
						// wkTString = wkTString + lon + " " + lat + ",";
						wkTString = wkTString + lat + " " + lon + ",";
					}
				}
			}
			wkTString = wkTString.substring(0, wkTString.length()-1) + ")";
			fw.write(filename + ";" + wkTString + ";" + id + "\n");
		}
		fw.close();
	}


	public static void main(String[] args) {
		JSON2PostGISCSV j2PG = new JSON2PostGISCSV();
		try {
			j2PG.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
