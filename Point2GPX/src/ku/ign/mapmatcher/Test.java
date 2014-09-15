package ku.ign.mapmatcher;

import java.io.File;
import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Test {

	private CloseableHttpResponse response;
	private HttpEntity responseEntity;

	String app_id = "01e8761c";
	String app_key = "5c989d0272b48a9b2cbefd19d23d95e9";
	String file = "/Users/besn/Documents/workspace/Point2GPX/out/point-raw-0.gpx";
	String requestURL = "https://test.roadmatching.com/rest/mapmatch";

	/*
	public xxx() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		FileBody uploadFilePart = new FileBody(uploadFile);
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("upload-file", uploadFilePart);
		httpPost.setEntity(reqEntity);

		HttpResponse response = httpclient.execute(httpPost);
	}*/

	public void xx() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(requestURL);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("app_id", app_id, ContentType.TEXT_PLAIN);
		builder.addTextBody("app_key", app_key, ContentType.TEXT_PLAIN);
		builder.addBinaryBody("file", new File(file), ContentType.APPLICATION_OCTET_STREAM, "file.ext");
		HttpEntity multipart = builder.build();

		uploadFile.setEntity(multipart);

		try {
			response = httpClient.execute(uploadFile);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		responseEntity = response.getEntity();
	}

	public static void main(String[] args) {
//		HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );
//		System.setProperty( "javax.net.ssl.trustStore", "keystore_file" );
//		System.setProperty( "javax.net.ssl.trustStorePassword", "jaksch01" );
//		System.setProperty( "sun.security.ssl.allowUnsafeRenegotiation", "true" );
		Test t = new Test();
		t.xx();
	}

}
