package ku.ign.point2gpx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class Point2GPX {

	private String csvFileName;
	private String gpxFileName;

	static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

	private static final String TAG_GPX = "<gpx"
			+ " xmlns=\"http://www.topografix.com/GPX/1/1\""
			+ " version=\"1.1\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \">";

	private class TrackPoint {
		private String lat;
		private String lon;
		private String name;

		public TrackPoint(String lat, String lon, String name) {
			this.setLat(lat);
			this.setLon(lon);
			this.setName(name);
		}

		public String getLat() {
			return lat;
		}

		public void setLat(String lat) {
			this.lat = lat;
		}

		public String getLon() {
			return lon;
		}

		public void setLon(String lon) {
			this.lon = lon;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private class LatLon {
		private String lat;
		private String lon;
		private Date date;
		private String id;

		public LatLon(String lat, String lon, Date date, String id) {
			this.lat = lat;
			this.lon = lon;
			this.date = date;
			this.id = id;
		}

		public String toString() {
			return this.lat + "/" + this.lon;
		}

		public String getId() {
			return id;
		}
	}

	String experiencePointFile = "/Users/besn/Dropbox/Bike Planning Article Gamble_Snizek (1)/0.1 - geodata/0.1.6 - Track Workshop/experience-points-raw.csv";

	HashMap<String, ArrayList<TrackPoint>> trackPoints = new HashMap<String, ArrayList<TrackPoint>>();

	public void loadExperiencePoints() throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(experiencePointFile));
		br.read();
		String line;
		while ((line = br.readLine()) != null) {
			String[] ll = line.split(",");
			System.out.println(line);
			String lat = ll[1];
			String lon = ll[0];
			String id = ll[6];

			TrackPoint t = new TrackPoint(lat, lon, id);

			if (trackPoints.containsKey(id)) {
				trackPoints.get(id).add(t);
			} else {
				trackPoints.put(id, new ArrayList<TrackPoint>());
				trackPoints.get(id).add(t);
			}
		}
		br.close();
	}

	@SuppressWarnings("deprecation")
	public Point2GPX(String string, String string2) throws IOException {

		loadExperiencePoints();

		this.setCsvFileName(string);
		this.gpxFileName = string2;

		BufferedReader br = null;
		String line = "";

		br = new BufferedReader(new FileReader(this.getCsvFileName()));

		String cvsSplitBy = ",";

		br.readLine();

		HashMap<String, ArrayList<LatLon>> trips = new HashMap<String, ArrayList<LatLon>>();

		Date date = new Date();
		date.setSeconds(0);

		while ((line = br.readLine()) != null) {

			// use comma as separator
			String[] objects = line.split(cvsSplitBy);


			String tripID = objects[4];

			if (trips.containsKey(tripID)) {
				trips.get(tripID).add(new LatLon(objects[2], objects[3], date, objects[4]));
			} else {
				trips.put(tripID, new ArrayList<LatLon>());
				trips.get(tripID).add(new LatLon(objects[2], objects[3], date, objects[4]));
			}

			date = new Date(date.getTime() + 1 * 60000);

		}

		ArrayList<String> tripIDs = new ArrayList<String>(trips.keySet());
		Collections.sort(tripIDs);

		for (String tripID : tripIDs) {
			// System.out.println(tripID + " : " + trips.get(tripID));
			File f = new File(this.getGpxFileName() + tripID + ".gpx");
			writeGpxFile(tripID, trips.get(tripID), f);
		}
		br.close();
	}

	/**
	 * Writes the GPX file
	 * @param trackName Name of the GPX track (metadata)
	 * @param cTrackPoints Cursor to track points.
	 * @param cWayPoints Cursor to way points.
	 * @param target Target GPX file
	 * @throws IOException 
	 */
	public void writeGpxFile(String trackName, ArrayList<LatLon> wayPoints, File target) throws IOException {
		FileWriter fw = new FileWriter(target);

		fw.write(XML_HEADER + "\n");
		fw.write(TAG_GPX + "\n");

		writeWayPoints(trackName, fw);

		writeTrackPoints(trackName, fw, wayPoints);

		fw.write("</gpx>");

		fw.close();
	}

	public String getCsvFileName() {
		return csvFileName;
	}

	public void writeWayPoints(String trackname, FileWriter fw) throws IOException {
		ArrayList<TrackPoint> trackpoints = this.trackPoints.get(trackname);
		if (trackpoints != null) {
			for (TrackPoint tP : trackpoints) {
				// System.out.println(tP);
				fw.write("\t<wpt lat=\"" + tP.getLat()  +  "\" lon=\"" + tP.getLon()  + "\">\n");
				fw.write("\t\t<name>" + tP.getName() +  "</name>\n");
				fw.write("\t</wpt>\n");
			}
		}
	}

	/**
	 * Iterates on track points and write them.
	 * @param trackName Name of the track (metadata).
	 * @param fw Writer to the target file.
	 * @param c Cursor to track points.
	 * @throws IOException
	 */
	public static void writeTrackPoints(String trackName, FileWriter fw, ArrayList<LatLon> latLons) throws IOException {
		fw.write("\t" + "<trk>\n");
		fw.write("\t\t" + "<name>" + trackName + "</name>" + "\n");

		fw.write("\t\t" + "<trkseg>" + "\n");

		for (LatLon ll : latLons) {
			StringBuffer out = new StringBuffer();
			out.append("\t\t\t" + "<trkpt lat=\"" 
					+ ll.lat + "\" "
					+ "lon=\"" + ll.lon + "\">");
			out.append("<ele>" + 0 + "</ele>");

			String ds = DATE_FORMAT.format(ll.date) + "T" + TIME_FORMAT.format(ll.date) + 'Z';

			out.append("<time>" + ds + "</time>");
			out.append("<name>" + ll.getId() + "</name>");
			out.append("</trkpt>" + "\n");
			fw.write(out.toString());
		}
		fw.write("\t\t" + "</trkseg>" + "\n");
		fw.write("\t" + "</trk>" + "\n");
	}


	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}


	public String getGpxFileName() {
		return gpxFileName;
	}

	public void setGpxFileName(String gpxFileName) {
		this.gpxFileName = gpxFileName;
	}


	public static void main(String[] args) {
		try {
			String a0 = "/Users/besn/Dropbox/Bike Planning Article Gamble_Snizek (1)/0.1 - geodata/0.1.6 - Track Workshop/points-raw-joined.csv";
			String a1 = "out/point-raw-joined";
			Point2GPX p2g = new Point2GPX(a0, a1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
