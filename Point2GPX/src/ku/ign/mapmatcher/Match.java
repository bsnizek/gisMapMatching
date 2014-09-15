package ku.ign.mapmatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import ku.ign.point2gpx.MultipartUtility;

public class Match {

	public Match() {
		String app_id = "01e8761c";
		String app_key = "5c989d0272b48a9b2cbefd19d23d95e9";
		String file = "/Users/besn/Documents/workspace/Point2GPX/out/point-raw-0.gpx";

		mapmatch(app_id, app_key, file);

	}

	public void mapmatch(String app_id, String app_key, String gpsfile) {
		String charset = "UTF-8";
		File uploadFile1 = new File(gpsfile);
		String requestURL = "https://test.roadmatching.com/rest/mapmatch";

		try {
			MultipartUtility multipart = new MultipartUtility(requestURL, charset);

			multipart.addHeaderField("User-Agent", "CodeJava");
			multipart.addHeaderField("Test-Header", "Header-Value");

			multipart.addFormField("description", "Cool Pictures");
			multipart.addFormField("keywords", "Java,upload,Spring");

			multipart.addFilePart("fileUpload", uploadFile1);

			List<String> response = multipart.finish();

			System.out.println("SERVER REPLIED:");

			for (String line : response) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	// Create a trust manager that does not validate certificate chains
	final static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		
		public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
		}
		
		public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
		}

		@Override
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
	}};


	public static String httpPost(String urlStr, String[] paramName,
			String[] paramVal) throws Exception {
		URL url = new URL(urlStr);

		final SSLContext sslContext = SSLContext.getInstance( "SSL" );
		sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
		
		final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

		final URLConnection conn = new URL( urlStr ).openConnection();
		

//		HttpURLConnection conn =
//				(HttpURLConnection) url.openConnection();
//		conn.setRequestMethod("POST");
//		conn.setDoOutput(true);
//		conn.setDoInput(true);
//		conn.setUseCaches(false);
//		conn.setAllowUserInteraction(false);
//		conn.setRequestProperty("Content-Type",
//				"application/x-www-form-urlencoded");

		// Create the form content
		OutputStream out = conn.getOutputStream();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		for (int i = 0; i < paramName.length; i++) {
			writer.write(paramName[i]);
			writer.write("=");
			writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
			writer.write("&");
		}
		writer.close();
		out.close();

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		// conn.disconnect();
		return sb.toString();
	}

	public static void main(String[] args) {
		//System.setProperty( "javax.net.ssl.trustStore", "keystore_file" );
		//System.setProperty( "javax.net.ssl.trustStorePassword", "jaksch01" );
		System.setProperty( "sun.security.ssl.allowUnsafeRenegotiation", "true" );
		Match m = new Match();
	}

}
